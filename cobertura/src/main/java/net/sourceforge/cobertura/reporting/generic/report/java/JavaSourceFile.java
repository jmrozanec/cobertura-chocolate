package net.sourceforge.cobertura.reporting.generic.report.java;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JavaSourceFile {

  private ReadWriteLock lock;
  private final String filename;
  private List<JavaSourceFileLine> sourceFileLines;
  private AtomicBoolean sorted;

  public JavaSourceFile(String filename) {
    this.filename = filename;
    sourceFileLines= Lists.newArrayList();
    sorted = new AtomicBoolean(true);
    lock = new ReentrantReadWriteLock();
  }

  public String getFilename() {
    return filename;
  }

  public void addSourceFileLine(JavaSourceFileLine sourceFileLine){
    lock.writeLock().lock();
    sourceFileLines.add(sourceFileLine);
    sorted.set(false);
    lock.writeLock().unlock();
  }

  public JavaSourceFileLine getSourceFileLine(int lineNumber){
    lock.writeLock().lock();
      if(!sorted.get()){
        Collections.sort(sourceFileLines);
        sorted.set(true);
      }
    JavaSourceFileLine line = sourceFileLines.get(lineNumber);
    lock.writeLock().unlock();
    return line;
  }

  public Iterable<JavaSourceFileLine> getSourceFileLines(Predicate<JavaSourceFileLine> predicate){
    return Iterables.filter(sourceFileLines, predicate);
  }
}
