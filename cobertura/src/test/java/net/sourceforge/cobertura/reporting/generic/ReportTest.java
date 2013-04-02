package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.data.CoverageData;
import net.sourceforge.cobertura.reporting.generic.node.Node;
import net.sourceforge.cobertura.reporting.generic.node.NodeType;
import net.sourceforge.cobertura.reporting.generic.report.GenericReportEntry;
import net.sourceforge.cobertura.reporting.generic.report.Report;
import net.sourceforge.cobertura.reporting.generic.report.ReportConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;

public class ReportTest {

    Report report;

    @Before
    public void setUp(){
        Set<Node> entries = new HashSet<Node>();
        GenericReportEntry entry = new GenericReportEntry(NodeType.PROJECT, "project",
                new CoverageData(), new CoverageData(), 0, 0);
        entries.add(entry);
        this.report = new Report(entries);
    }

    @Test
    public void testGetEntriesForLevel(){
        assertFalse(report.getEntriesForLevel(ReportConstants.level_project).isEmpty());
    }
}
