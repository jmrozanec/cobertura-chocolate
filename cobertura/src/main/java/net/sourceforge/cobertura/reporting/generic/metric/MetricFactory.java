package net.sourceforge.cobertura.reporting.generic.metric;

import com.google.common.base.Predicate;
import net.sourceforge.cobertura.reporting.generic.node.NewNode;

public abstract class MetricFactory {

  private Predicate<NewNode> predicate;

  public MetricFactory(Predicate<NewNode> predicate){
    this.predicate = predicate;
  }

  public boolean match(NewNode node){
    return predicate.apply(node);
  }

  public abstract IMetric getInstance();
}
