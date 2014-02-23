package com.sequenceiq.samples.core.config;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.sequenceiq.samples.core"})
public class CoreConfiguration {

	@Bean
	public Region region() {
		return Region.getRegion(Regions.US_WEST_2);
	}

}
