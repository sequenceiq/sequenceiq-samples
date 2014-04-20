package com.sequenceiq.samples.phoenix;

import static org.apache.commons.io.IOUtils.readLines;
import static org.apache.commons.lang.StringEscapeUtils.escapeSql;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class Upsert {

    @Autowired
    private DefaultDSLContext dslContext;
    @Autowired
    private Random random;
    private List<String> names;

    public Upsert() throws IOException {
        names = readLines(getClass().getResourceAsStream("/names.txt"));
    }

    public void fillTables() {
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 1; i < 1000; i++) {
            String userSql = String.format("upsert into customers values (%d, '%s', %d, '%s')",
                    i,
                    escapeSql(names.get(random.nextInt(names.size() - 1))),
                    random.nextInt(40) + 18,
                    escapeSql(locales[random.nextInt(locales.length - 1)].getDisplayCountry()));
            String orderSql = String.format("upsert into orders values (%d, CURRENT_DATE(), %d, %d)",
                    i,
                    i,
                    random.nextInt(1_000_000));
            System.out.println("Execute: " + userSql);
            System.out.println("Execute: " + orderSql);
            dslContext.execute(userSql);
            dslContext.execute(orderSql);
        }
    }

}
