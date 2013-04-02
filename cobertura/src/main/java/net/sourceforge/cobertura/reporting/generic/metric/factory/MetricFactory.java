package net.sourceforge.cobertura.reporting.generic.metric.factory;

import com.google.common.base.Predicate;
import net.sourceforge.cobertura.reporting.generic.metric.FixedValueMetric;
import net.sourceforge.cobertura.reporting.generic.metric.IMetric;
import net.sourceforge.cobertura.reporting.generic.node.NewNode;

public abstract class MetricFactory {

  private Predicate<NewNode> predicate;

  public MetricFactory(Predicate<NewNode> predicate){
    this.predicate = predicate;
  }

  public boolean match(NewNode node){
    return predicate.apply(node);
  }

  public abstract IMetric getInstance(NewNode node);

    /**
   * @param predicate - predicate to determine if a Node instance should get a metric instance;
   * @return MetricFactory instance;
   */
  public static MetricFactory getCoveredBranchesMetricFactory(Predicate<NewNode>predicate){
    return new MetricFactory(predicate) {
      @Override
      public IMetric getInstance(NewNode node) {
        return new FixedValueMetric("Covered branches","Number of covered branches", node.getCoveredBranches());
      }
    };
  }
}