package net.sourceforge.cobertura.reporting.generic.node;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.sourceforge.cobertura.reporting.generic.metric.IMetric;
import net.sourceforge.cobertura.reporting.generic.metric.factory.MetricFactory;
import net.sourceforge.cobertura.reporting.generic.metric.threshold.Threshold;
import net.sourceforge.cobertura.reporting.generic.metric.threshold.ThresholdFactory;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

public abstract class AbstractNode implements NewNode{

  protected Set<NewNode> childs;
  protected SortedSet<IMetric>metrics;
  protected SortedSet<Threshold>thresholds;
  protected final String name;
  protected final int totalBranches;
  protected final int coveredBranches;
  protected final int totalLines;
  protected final int coveredLines;

  protected final double cyclomaticComplexity;
  protected final long hits;

  @Override
  public void addNode(NewNode node) {
    notNull(node);
    if(acceptNode(node)){
      childs.add(node);
    }
  }

  public AbstractNode(String name, int totalBranches, int coveredBranches, int totalLines, int coveredLines,
                      long hits, double ccn) {
    notNull(name, "Name cannot be null");
    isTrue(totalBranches>=0, "Total branches should be a positive number");
    isTrue(coveredBranches>=0, "Covered branches should be a positive number");
    isTrue(totalLines>=0, "Total lines should be a positive number");
    isTrue(coveredLines>=0, "Covered lines should be a positive number");
    isTrue(hits>=0, "Hits should be a positive number");
    isTrue(ccn>=0, "Total branches should be a positive number");

    this.totalBranches = totalBranches;
    this.coveredBranches = coveredBranches;
    this.totalLines = totalLines;
    this.coveredLines = coveredLines;
    childs = Sets.newHashSet();
    metrics = new TreeSet<IMetric>();
    thresholds = new TreeSet<Threshold>();
    this.name = name;
    this.hits = hits;
    this.cyclomaticComplexity = ccn;
  }

  /**
   * Contains criteria to accept a Node instance.
   * @param node - some Node instance; must not be <code>null</code>
   * @return boolean - true if accepts Node, false otherwise;
   */
  protected abstract boolean acceptNode(NewNode node);

  @Override
  public Set<NewNode> getNodes(Predicate<NewNode> predicate) {
    notNull(predicate);
    return ImmutableSet.copyOf(Iterables.filter(childs, predicate));
  }

  @Override
  public Set<NewNode> getAllNodes(Predicate<NewNode> predicate) {
    notNull(predicate);
    Set<NewNode> set = Sets.newHashSet();
    set.addAll(ImmutableSet.copyOf(Iterables.filter(childs, predicate)));
    for(NewNode node : childs){
      set.addAll(node.getAllNodes(predicate));
    }
    return Collections.unmodifiableSet(set);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getTotalBranches() {
    return totalBranches;
  }

  @Override
  public int getCoveredBranches() {
    return coveredBranches;
  }

  @Override
  public double getBranchRate() {
    return (double)getCoveredBranches()/(double)getTotalBranches();
  }

  @Override
  public int getTotalLines() {
    return totalLines;
  }

  @Override
  public int getCoveredLines() {
    return coveredLines;
  }

  @Override
  public double getLinesRate() {
    return (double)getCoveredLines()/(double)getTotalLines();
  }

  @Override
  public double getCyclomaticComplexityNumber(){
    return cyclomaticComplexity;
  }

  @Override
  public long getHits(){
    return hits;
  }

  @Override
  public void addMetric(MetricFactory metricFactory) {
    if(metricFactory.match(this)){
      metrics.add(metricFactory.getInstance(this));
    }
  }

  @Override
  public SortedSet<IMetric> getMetrics() {
    return Collections.unmodifiableSortedSet(metrics);
  }

  @Override
  public void addThreshold(ThresholdFactory thresholdFactory) {
    if(thresholdFactory.match(this)){
      thresholds.add(thresholdFactory.getInstance());
    }
  }

  @Override
  public SortedSet<Threshold> getThresholds(){
    return Collections.unmodifiableSortedSet(thresholds);
  }
}
