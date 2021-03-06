package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.metric.CrapMetric;
import net.sourceforge.cobertura.reporting.generic.metric.ICustomMetric;
import net.sourceforge.cobertura.reporting.generic.metric.MetricsLoader;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertTrue;

public class MetricsLoaderTest {
    @Test
    public void testGetMetrics() throws Exception {
        boolean loadedCustomMetrics = false;
        Iterator<ICustomMetric>metrics =
                new MetricsLoader().getMetrics().iterator();

        while (metrics.hasNext()){
            if(metrics.next().getClass().equals(CrapMetric.class)){
                loadedCustomMetrics = true;
            }
        }
        assertTrue(loadedCustomMetrics);
    }
}
