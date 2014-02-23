package com.aws.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

@Configuration
@ComponentScan(basePackages = {"com.aws.core"})
public class CoreConfiguration {

	@Bean
	public Region region() {
		return Region.getRegion(Regions.US_WEST_2);
	}

}
