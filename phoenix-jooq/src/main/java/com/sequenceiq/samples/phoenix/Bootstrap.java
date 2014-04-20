package com.sequenceiq.samples.phoenix;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Bootstrap {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(AppConfig.class);

        Upsert upsert = appContext.getBean(Upsert.class);
        upsert.fillTables();

        Query query = appContext.getBean(Query.class);
        query.selectUsersWithLessAmount(10000);
    }
}
