package net.sourceforge.cobertura.reporting.generic.report;

import net.sourceforge.cobertura.reporting.generic.metric.threshold.Threshold;
import net.sourceforge.cobertura.reporting.generic.node.NewNode;
import org.joda.time.DateTime;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.Collections;
import java.util.Set;

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

/**
 * This class groups data to be reported,
 * so that can be easily accessed when building reports.
 */
public class ReportNew {

  @Attribute
  private DateTime created;

  @ElementList(inline = true)
  private Set<NewNode> entries;

  public ReportNew(Set<NewNode> entries) {
    created = DateTime.now();
    this.entries = Collections.unmodifiableSet(entries);
  }

  public void addThreshold(Threshold threshold) {
    //TODO
  }

  public void export(IReportFormatStrategy reportFormat) {
//    reportFormat.save(this);//TODO refactor
  }
}