package net.sourceforge.cobertura.reporting.generic.report.java;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import japa.parser.ParseException;
import net.sourceforge.cobertura.coveragedata.*;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.reporting.generic.SourceFile;
import net.sourceforge.cobertura.reporting.generic.node.NewNode;
import net.sourceforge.cobertura.reporting.generic.node.java.*;
import net.sourceforge.cobertura.reporting.generic.report.JVMLanguage;
import net.sourceforge.cobertura.reporting.generic.report.ReportNew;
import net.sourceforge.cobertura.util.FileFinder;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2011 Jose M. Rozanec
 *
 * Cobertura is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * Cobertura is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cobertura; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

/**
 * Handles ProjectData information and puts it into a Report object.
 * Assumes ProjectData information corresponds to a Java project.
 */
public class JavaReportBuilderStrategyNew {
//implements IReportBuilderStrategy //TODO commented to avoid issues while loading by reflection
  private static final Logger log = Logger.getLogger(JavaReportBuilderStrategy.class);
  private Set<SourceFile> sourceFiles;

  private String encoding;
  private FileFinder fileFinder;

  private ProjectNode buildProjectNode(ProjectData projectData){
    ProjectNode node = new ProjectNode(
      projectData.getName(),
      projectData.getNumberOfValidBranches(),
      projectData.getNumberOfCoveredBranches(),
      projectData.getNumberOfValidLines(),
      projectData.getNumberOfCoveredLines()
    );

    for(Object o: projectData.getPackages()){
      node.addNode(buildPackageNode((PackageData)o));
    }

    return node;
  }

  private PackageNode buildPackageNode(PackageData packageData){
    PackageNode node = new PackageNode(
      packageData.getName(),
      packageData.getNumberOfValidBranches(),
      packageData.getNumberOfCoveredBranches(),
      packageData.getNumberOfValidLines(),
      packageData.getNumberOfCoveredLines());

    for(Object o: packageData.getSourceFiles()){
      node.addNode(buildSourceFileNode((SourceFileData)o));
    }

    return node;
  }

  private SourceFileNode buildSourceFileNode(SourceFileData sourceFileData){
    SourceFileNode node = new SourceFileNode(
      sourceFileData.getName(),
      sourceFileData.getNumberOfValidBranches(),
      sourceFileData.getNumberOfCoveredBranches(),
      sourceFileData.getNumberOfValidLines(),
      sourceFileData.getNumberOfCoveredLines()
    );

    try {
      JavaSourceFile sourceFile = new JavaSourceFileReader().build(fileFinder, sourceFileData.getName(), encoding);

      Predicate<JavaSourceFileLine>linesDoNotBelongToAnyClass = new Predicate<JavaSourceFileLine>() {
        @Override
        public boolean apply(@Nullable JavaSourceFileLine javaSourceFileLine) {
          return (javaSourceFileLine != null ? javaSourceFileLine.getClassName() : null) ==null;
        }
      };
      for (JavaSourceFileLine line : sourceFile.getSourceFileLines(linesDoNotBelongToAnyClass)) {
        LineNode lineNode = new LineNode("" + line.getLineNumber(), 0, 0, 1, 0);
        lineNode.setLineString(line.getText());
        node.addNode(lineNode);
      }

      for(Object o:sourceFileData.getClasses()){
        node.addNode(buildClassNode((ClassData)o, sourceFile));
      }
    } catch (IOException e) {
      //TODO handle
    } catch (ParseException e) {
      //TODO handle
    }
    return node;
  }

  private ClassNode buildClassNode(final ClassData classData, JavaSourceFile sourceFile){
    ClassNode node = new ClassNode(
        classData.getName(),
        classData.getNumberOfValidBranches(),
        classData.getNumberOfCoveredBranches(),
        classData.getNumberOfValidLines(),
        classData.getNumberOfCoveredLines()
    );

    Predicate<JavaSourceFileLine>sameClass = new Predicate<JavaSourceFileLine>() {
      @Override
      public boolean apply(@Nullable JavaSourceFileLine javaSourceFileLine) {
        return javaSourceFileLine != null && javaSourceFileLine.getClassName().equals(classData.getName());
      }
    };
    Predicate<JavaSourceFileLine>doesNotBelongToMethod = new Predicate<JavaSourceFileLine>() {
      @Override
      public boolean apply(@Nullable JavaSourceFileLine javaSourceFileLine) {
        return (javaSourceFileLine != null ? javaSourceFileLine.getMethodName() : null) ==null;
      }
    };
    for (JavaSourceFileLine line :
        sourceFile.getSourceFileLines(Predicates.<JavaSourceFileLine>and(sameClass, doesNotBelongToMethod))) {
      node.addNode(new LineNode("" + line.getLineNumber(), 0, 0, 1, 0));
    }

    for(final String method:classData.getMethodNamesAndDescriptors()){
      Collection<CoverageData> lineDatas = Collections2.filter(classData.getLines(), new Predicate<CoverageData>() {
        @Override
        public boolean apply(@Nullable CoverageData coverageData) {
          return coverageData != null && ((LineData) coverageData).getMethodName().equals(method);
        }
      });

      node.addNode(buildMethodNode(method, sourceFile, lineDatas));
    }
    return node;
  }

  private MethodNode buildMethodNode(final String method, JavaSourceFile sourceFile, Collection<CoverageData>lineDatas){
    List<CoverageData>linesList = new ArrayList<CoverageData>(lineDatas);
    Collections.sort(linesList, new Comparator<CoverageData>() {
      @Override
      public int compare(CoverageData o1, CoverageData o2) {
        return ((LineData)o1).compareTo(o2);
      }
    });

    Predicate<JavaSourceFileLine>sameMethod = new Predicate<JavaSourceFileLine>() {
      @Override
      public boolean apply(@Nullable JavaSourceFileLine javaSourceFileLine) {
        return javaSourceFileLine != null && javaSourceFileLine.getMethodName().equals(method);
      }
    };

    Iterator<CoverageData>lineDataIterator = linesList.iterator();
    Iterator<JavaSourceFileLine> lines=sourceFile.getSourceFileLines(sameMethod).iterator();
    int numberOfValidBranches = 0;
    int numberOfCoveredBranches = 0;
    int numberOfValidLines = 0;
    int numberOfCoveredLines = 0;
    Set<LineNode>lineNodes = Sets.newHashSet();
    while(lines.hasNext()){
      JavaSourceFileLine line = lines.next();
      LineData lineData = (LineData)lineDataIterator.next();
      LineNode lineNode = new LineNode(""+line.getLineNumber(), 
          lineData.getNumberOfValidBranches(),
          lineData.getNumberOfCoveredBranches(), 
          lineData.getNumberOfValidLines(),
          lineData.getNumberOfCoveredLines());
      lineNode.setLineString(line.getText());
      
      numberOfValidBranches+=lineData.getNumberOfValidBranches();
      numberOfCoveredBranches+=lineData.getNumberOfCoveredBranches();
      numberOfValidLines+=lineData.getNumberOfValidLines();
      numberOfCoveredLines+=lineData.getNumberOfCoveredLines();

      lineNodes.add(lineNode);
    }
    
    MethodNode node = 
        new MethodNode(method,numberOfValidBranches, numberOfCoveredBranches, numberOfValidLines, numberOfCoveredLines);
    
    for(LineNode lineNode:lineNodes){
      node.addNode(lineNode);
    }

    return node;
  }

  public ReportNew getReport(
    List<ProjectData> projects,
    String sourceEncoding, FileFinder finder) {
    this.encoding = sourceEncoding;
    this.fileFinder = finder;
    ComplexityCalculator complexity = new ComplexityCalculator(fileFinder);

    Set<NewNode>nodes = Sets.newHashSet();
    for (ProjectData project : projects) {
      nodes.add(buildProjectNode(project));
    }
    return new ReportNew(nodes);
  }


  public JVMLanguage getTargetedLanguage() {
    return JVMLanguage.JAVA;
  }
}
