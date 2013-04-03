package net.sourceforge.cobertura.reporting.generic.report.java;

import com.google.common.collect.Sets;
import japa.parser.ParseException;
import net.sourceforge.cobertura.coveragedata.*;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.reporting.generic.report.java.JavaSourceFileBuilder;
import net.sourceforge.cobertura.reporting.generic.SourceFile;
import net.sourceforge.cobertura.reporting.generic.SourceFileEntry;
import net.sourceforge.cobertura.reporting.generic.filter.CompositeFilter;
import net.sourceforge.cobertura.reporting.generic.filter.NameFilter;
import net.sourceforge.cobertura.reporting.generic.filter.Relation;
import net.sourceforge.cobertura.reporting.generic.filter.RelationFilter;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.EqCriteria;
import net.sourceforge.cobertura.reporting.generic.node.*;
import net.sourceforge.cobertura.reporting.generic.node.java.PackageNode;
import net.sourceforge.cobertura.reporting.generic.node.java.ProjectNode;
import net.sourceforge.cobertura.reporting.generic.node.java.SourceFileNode;
import net.sourceforge.cobertura.reporting.generic.report.*;
import net.sourceforge.cobertura.util.Constants;
import net.sourceforge.cobertura.util.FileFinder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
public class JavaReportBuilderStrategyNew implements IReportBuilderStrategy {
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
    //TODO parse source file and extract comment lines and code lines. See how to introduce into class/method/line hierarchy
    try {
      JavaSourceFile sourceFile = new JavaSourceFileReader().build(fileFinder, sourceFileData.getName(), encoding);

    } catch (IOException e) {
      //TODO handle
    } catch (ParseException e) {
      //TODO handle
    }
    return node;
  }

  private JavaSourceFile buildJavaSourceFile(SourceFileData sourceFileData){
    return null;//TODO implement
  }

  public Report getReport(
    List<ProjectData> projects,
    String sourceEncoding, FileFinder finder) {
    this.encoding = sourceEncoding;
    this.fileFinder = finder;
    ComplexityCalculator complexity = new ComplexityCalculator(fileFinder);

    Set<NewNode>nodes = Sets.newHashSet();
    for (ProjectData project : projects) {
      nodes.add(buildProjectNode(project));
    }

    return null;//TODO
//    return new ReportNew(nodes);
  }

  @Override
  public JVMLanguage getTargetedLanguage() {
    return JVMLanguage.JAVA;
  }
}
