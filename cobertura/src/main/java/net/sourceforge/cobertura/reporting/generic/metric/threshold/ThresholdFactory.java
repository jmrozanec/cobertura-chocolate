package net.sourceforge.cobertura.reporting.generic.metric.threshold;

import com.google.common.base.Predicate;
import net.sourceforge.cobertura.reporting.generic.node.NewNode;

public abstract class ThresholdFactory {

  private Predicate<NewNode> predicate;

  public ThresholdFactory(Predicate<NewNode> predicate){
    this.predicate = predicate;
  }

  public boolean match(NewNode node){
    return predicate.apply(node);
  }

  public abstract Threshold getInstance();
}
