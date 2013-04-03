package net.sourceforge.cobertura.reporting.generic.report.java;

public class JavaSourceFileLine implements Comparable{

  private final int lineNumber;
  private final String text;
  private LineType type;

  private String className;
  private String methodName;

  public JavaSourceFileLine(int lineNumber, String text) {
    this.lineNumber = lineNumber;
    this.text = text;
    type = LineType.NONE;
  }


  @Override
  public int compareTo(Object o) {
    return lineNumber-((JavaSourceFileLine)o).getLineNumber();
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public String getText() {
    return text;
  }

  public LineType getType() {
    return type;
  }

  public void setType(LineType type) {
    this.type = type;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }
}
