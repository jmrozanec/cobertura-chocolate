package net.sourceforge.cobertura.reporting.generic.metric;

import net.sourceforge.cobertura.reporting.generic.node.NodeType;

/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2012 Jose M. Rozanec
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
@Deprecated
public interface ICustomMetric extends IMetric{
    /**
     * Returns the node type to which the metric applies.
     * @return
     */
    NodeType getApplicableType();

    //TODO this should be modified so that we can leverage any existing metrics
    //TODO and not depend on BasicMetricData
    /**
     * Sets a MetricRegistry, so that can access other node metrics to perform calculations.
     *
     * @param registry
     */
    void setMetricRegistry(MetricRegistry registry);

    /**
     * Metric value;
     * Must throw an InsufficientInfoException if has no enough data to perform the calculation.
     * @return
     */
    @Override
    double getValue();
}
