package com.sequenceiq.samples;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Locale;

public class ToUpperCaseBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("toUpperCase");
    }

    @Override
    public Command build(Config config, Command command, Command command2, MorphlineContext morphlineContext) {
        return new ToUpperCase(this, config, command, command2, morphlineContext);
    }

    private static final class ToUpperCase extends AbstractCommand {

        private final String fieldName;
        private final Locale locale;

        public ToUpperCase(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            this.fieldName = getConfigs().getString(config, "field");
            this.locale = getConfigs().getLocale(config, "locale", Locale.ROOT);
            LOG.debug("fieldName: {}", fieldName);
            validateArguments();
        }

        @Override
        protected boolean doProcess(Record record) {
            ListIterator iter = record.get(fieldName).listIterator();
            while (iter.hasNext()) {
                iter.set(transformFieldValue(iter.next()));
            }
            return super.doProcess(record);
        }

        private Object transformFieldValue(Object value) {
            return value.toString().toUpperCase(locale);
        }

        @Override
        protected void doNotify(Record notification) {
            LOG.debug("myNotification: {}", notification);
            super.doNotify(notification);
        }

    }
}
