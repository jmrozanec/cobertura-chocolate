package net.sourceforge.cobertura.reporting.generic.node.java;

import net.sourceforge.cobertura.reporting.generic.node.AbstractNode;
import net.sourceforge.cobertura.reporting.generic.node.NewNode;

public class SourceFileNode extends AbstractNode {

  public SourceFileNode(String name, int totalBranches, int coveredBranches, int totalLines, int coveredLines) {
    super(name, totalBranches, coveredBranches, totalLines, coveredLines);
  }

  @Override
  protected boolean acceptNode(NewNode node) {
    return node instanceof ClassNode;
  }
}
