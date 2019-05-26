package com.github.mob41.osumer.debug;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;

public class NotReportedFilter implements MetricFilter {

	@Override
	public boolean matches(String name, Metric metric) {
		return !name.contains("not_reported");
	}

}
