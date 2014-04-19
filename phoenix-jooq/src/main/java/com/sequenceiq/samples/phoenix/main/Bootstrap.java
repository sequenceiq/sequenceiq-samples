package com.sequenceiq.samples.phoenix.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sequenceiq.samples.phoenix.configuration.AppConfig;

public class Bootstrap {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
    }
}
