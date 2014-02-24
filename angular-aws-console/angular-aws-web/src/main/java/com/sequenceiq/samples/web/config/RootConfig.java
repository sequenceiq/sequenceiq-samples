package com.sequenceiq.samples.web.config;

import com.sequenceiq.samples.core.config.CoreConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

/**
 * User: doktoric
 */

@Configuration
@ComponentScan(basePackages = {"com.sequenceiq.samples.web"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = org.springframework.stereotype.Controller.class)})
@Import(CoreConfiguration.class)
public class RootConfig {

}
