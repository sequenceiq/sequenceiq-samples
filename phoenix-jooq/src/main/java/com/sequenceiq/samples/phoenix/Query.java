package com.sequenceiq.samples.phoenix;

import static org.jooq.impl.DSL.fieldByName;
import static org.jooq.impl.DSL.tableByName;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Query {

    @Autowired
    private DefaultDSLContext dslContext;

    public void selectUsersWithLessAmount(int amount){
        Result<Record> result = dslContext
                .select()
                .from(tableByName("customers").as("c"))
                .join(tableByName("orders").as("o")).on("o.customer_id = c.id")
                .where(fieldByName("o.amount").lessThan(amount))
                .orderBy(fieldByName("c.name").asc())
                .fetch();
        System.out.println(result.format(10));
    }
}
