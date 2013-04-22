package net.sourceforge.cobertura.reporting.generic.node.java;

import net.sourceforge.cobertura.reporting.generic.node.AbstractNode;
import net.sourceforge.cobertura.reporting.generic.node.NewNode;

public class PackageNode extends AbstractNode {
  public PackageNode(String name, int totalBranches, int coveredBranches, int totalLines, int coveredLines,
                     long hits, double cyclomaticComplexityNumber) {
    super(name, totalBranches, coveredBranches, totalLines, coveredLines, hits, cyclomaticComplexityNumber);
  }

  @Override
  protected boolean acceptNode(NewNode node) {
    return node instanceof SourceFileNode;
  }
}
