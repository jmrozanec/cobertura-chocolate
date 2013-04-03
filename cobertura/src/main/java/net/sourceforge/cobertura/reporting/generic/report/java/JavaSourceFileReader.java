package net.sourceforge.cobertura.reporting.generic.report.java;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.*;
import japa.parser.ast.body.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import net.sourceforge.cobertura.reporting.generic.SourceFile;
import net.sourceforge.cobertura.reporting.generic.SourceFileEntry;
import net.sourceforge.cobertura.util.FileFinder;
import net.sourceforge.cobertura.util.Source;

import java.io.*;
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
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import net.sourceforge.cobertura.util.FileFinder;
import net.sourceforge.cobertura.util.Source;

import java.io.*;
import java.util.*;

public class JavaSourceFileReader {

  private String packageName;

  public JavaSourceFile build(FileFinder fileFinder, String sourceFileName, String encoding)
    throws IOException, ParseException {
    JavaSourceFile sourceFile = readSourceFileLines(fileFinder, sourceFileName, encoding);
    enrichWithParserInfo(sourceFile, fileFinder.getFileForSource(sourceFileName).getAbsolutePath());
    return sourceFile;
  }

  private JavaSourceFile readSourceFileLines(FileFinder fileFinder, String sourceFileName, String encoding) {
    JavaSourceFile sourceFile = new JavaSourceFile(sourceFileName);

    Source source = fileFinder.getSource(sourceFileName);

    if (source == null) {
      throw new RuntimeException("Unable to locate " + sourceFileName);
    }
    BufferedReader br;
    try {
      br = new BufferedReader(new InputStreamReader(source.getInputStream(), encoding));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    try {
      String lineStr;
      int lineNumber = 0;
      while ((lineStr = br.readLine()) != null) {
        //we want to retrieve all the lines...
        sourceFile.addSourceFileLine(new JavaSourceFileLine(++lineNumber, lineStr));
      }
    } catch (IOException e) {
      //TODO handle this
    }
    return sourceFile;
  }

  private void enrichWithParserInfo(JavaSourceFile sourceFile, String sourceAbsolutePath) throws IOException, ParseException {
    // creates an input stream for the file to be parsed
    FileInputStream in = new FileInputStream(sourceAbsolutePath);
    CompilationUnit cu;
    try {
      // parse the file
      cu = JavaParser.parse(in);
    } finally {
      in.close();
    }

    // visit and print the methods names
    new ClassVisitor(sourceFile).visit(cu, null);
    new MethodVisitor(sourceFile).visit(cu, null);
    new JDocVisitor(sourceFile).visit(cu, null);
    new AnnotationVisitor(sourceFile).visit(cu, null);
    new ImportVisitor(sourceFile).visit(cu, null);
    new ConstructorVisitor(sourceFile).visit(cu, null);
  }

  /**
   * Simple visitor implementation for visiting PackageDeclaration nodes.
   */
  private class PackageVisitor extends VoidVisitorAdapter {
    @Override
    public void visit(PackageDeclaration n, Object arg) {
      packageName = n.getName().getName();
    }
  }

  /**
   * Simple visitor implementation for visiting ClassOrInterfaceDeclaration nodes.
   */
  private class ClassVisitor extends VoidVisitorAdapter {
    private JavaSourceFile sourceFile;
    public ClassVisitor(JavaSourceFile sourceFile){
      this.sourceFile = sourceFile;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
      int beginLine = n.getBeginLine();
      for(;beginLine<n.getEndLine();beginLine++){
        JavaSourceFileLine line = sourceFile.getSourceFileLine(beginLine);
        line.setClassName(packageName + "." + n.getName());
        line.setType(LineType.CODE);
      }
    }
  }

  /**
   * Simple visitor implementation for visiting MethodDeclaration nodes.
   */
  private class MethodVisitor extends VoidVisitorAdapter {
    private JavaSourceFile sourceFile;
    public MethodVisitor(JavaSourceFile sourceFile){
      this.sourceFile = sourceFile;
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
      int beginLine = n.getBeginLine();
      for(;beginLine<n.getEndLine();beginLine++){
        JavaSourceFileLine line = sourceFile.getSourceFileLine(beginLine);
        line.setMethodName(n.getName());
        line.setType(LineType.CODE);
      }
    }
  }

  /**
   * Simple visitor implementation for visiting JavadocComment nodes.
   */
  private class JDocVisitor extends VoidVisitorAdapter {
    private JavaSourceFile sourceFile;
    public JDocVisitor(JavaSourceFile sourceFile){
      this.sourceFile = sourceFile;
    }

    @Override
    public void visit(JavadocComment n, Object arg) {
      int beginLine = n.getBeginLine();
      for(;beginLine<n.getEndLine();beginLine++){
        sourceFile.getSourceFileLine(beginLine).setType(LineType.COMMENT);
      }
    }
  }

  /**
   * Simple visitor implementation for visiting AnnotationDeclaration nodes.
   */
  private class AnnotationVisitor extends VoidVisitorAdapter {
    private JavaSourceFile sourceFile;
    public AnnotationVisitor(JavaSourceFile sourceFile){
      this.sourceFile = sourceFile;
    }

    @Override
    public void visit(AnnotationDeclaration n, Object arg) {
      int beginLine = n.getBeginLine();
      for(;beginLine<n.getEndLine();beginLine++){
        sourceFile.getSourceFileLine(beginLine).setType(LineType.ANNOTATION);
      }
    }
  }


//  ConstructorDeclaration, ImportDeclaration,
  /**
   * Simple visitor implementation for visiting ImportDeclaration nodes.
   */
  private class ImportVisitor extends VoidVisitorAdapter {
    private JavaSourceFile sourceFile;
    public ImportVisitor(JavaSourceFile sourceFile){
      this.sourceFile = sourceFile;
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
      int beginLine = n.getBeginLine();
      for(;beginLine<n.getEndLine();beginLine++){
        sourceFile.getSourceFileLine(beginLine).setType(LineType.CODE);
      }
    }
  }

  /**
   * Simple visitor implementation for visiting ConstructorDeclaration nodes.
   */
  private class ConstructorVisitor extends VoidVisitorAdapter {
    private JavaSourceFile sourceFile;
    public ConstructorVisitor(JavaSourceFile sourceFile){
      this.sourceFile = sourceFile;
    }

    @Override
    public void visit(ConstructorDeclaration n, Object arg) {
      int beginLine = n.getBeginLine();
      for(;beginLine<n.getEndLine();beginLine++){
        sourceFile.getSourceFileLine(beginLine).setType(LineType.CODE);
      }
    }
  }
}
