package net.sourceforge.cobertura.reporting.generic.report;

import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.reporting.generic.report.JVMLanguage;
import net.sourceforge.cobertura.reporting.generic.report.ReportNew;
import net.sourceforge.cobertura.util.FileFinder;

import java.util.List;

public interface ReportBuilderStrategyNew {
  /**
   * Returns a Report instance with collected data.
   * @return
   */
  ReportNew getReport(List<ProjectData> projects,
                   String sourceEncoding, FileFinder finder);

  /**
   * Returns the name of the targeted lang is capable
   * of interpreting information from.
   * @return
   */
  JVMLanguage getTargetedLanguage();

}
