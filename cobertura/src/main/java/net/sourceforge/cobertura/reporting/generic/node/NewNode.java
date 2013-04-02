package net.sourceforge.cobertura.reporting.generic.node;
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

import com.google.common.base.Predicate;
import net.sourceforge.cobertura.reporting.generic.metric.IMetric;
import net.sourceforge.cobertura.reporting.generic.metric.MetricFactory;
import net.sourceforge.cobertura.reporting.generic.metric.threshold.Threshold;
import net.sourceforge.cobertura.reporting.generic.metric.threshold.ThresholdFactory;

import java.util.Set;
import java.util.SortedSet;

public interface NewNode {
  /**
   * Add node;
   * @param node - Node instance;
   */
  void addNode(NewNode node);

  /**
   * Returns all immediate nodes matching this predicate;
   * @return
   */
  Set<? extends NewNode>getNodes(Predicate<NewNode> predicate);

  /**
   * Recursively get all nodes matching this predicate;
   * @param predicate
   * @return
   */
  Set<? extends NewNode>getAllNodes(Predicate<NewNode> predicate);

  /**
   * Get nodes name.
   * @return
   */
  String getName();

  /**
   * @return int - total branches for given Node instance
   */
  int getTotalBranches();

  /**
   * @return int - covered branches for given Node instance
   */
  int getCoveredBranches();

  /**
   * @return double - branches coverage ratio for given Node instance
   */
  double getBranchRate();

  /**
   * @return int - total lines for given Node instance
   */
  int getTotalLines();

  /**
   * @return int - covered lines for given Node instance
   */
  int getCoveredLines();

  /**
   * @return int - lines coverage ratio for given Node instance
   */
  double getLinesRate();

  /**
   * Recursively creates IMetric instance for Node instances;
   * @param metricFactory
   */
  void addMetric(MetricFactory metricFactory);

  /**
   * Gets metrics for this Node instance
   * @return
   */
  SortedSet<IMetric> getMetrics();

  /**
   * Recursively applies threshold to matching Node instances;
   * @param thresholdFactory
   */
  void addThreshold(ThresholdFactory thresholdFactory);

  /**
   * Get thresholds for this Node instance;
   * @return
   */
  SortedSet<Threshold> getThresholds();

}
