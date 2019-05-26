package com.github.mob41.osumer.debug;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;

public class ResettableGauge implements Gauge<Long> {
	
	private final Counter c;
	
	public ResettableGauge(Counter c) {
		this.c = c;
	}

	@Override
	public Long getValue() {
		long count = c.getCount();
		c.dec(count);
		return count;
	}

}
