package com.sequenceiq.samples;

import com.typesafe.config.Config;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Ricsi on 2014.02.13..
 */
public class SplitBuilder implements CommandBuilder {

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("split");
    }

    @Override
    public Command build(Config config, Command command, Command command2, MorphlineContext morphlineContext) {
        return new Split(this, config, command, command2, morphlineContext);
    }

    private static final class Split extends AbstractCommand {

        private final String fieldName;
        private final String separator;
        private final List<String> newFields;
        private boolean dropUndeclaredField;
        private boolean trimSegments;

        public Split(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            this.fieldName = getConfigs().getString(config, "field");
            this.separator = getConfigs().getString(config, "separator");
            this.newFields = getConfigs().getStringList(config, "newFields");
            this.dropUndeclaredField = getConfigs().getBoolean(config, "dropUndeclaredField", true);
            this.trimSegments = getConfigs().getBoolean(config, "trimSegments", true);
            LOG.debug("fieldName: {}", fieldName);
            validateArguments();
        }

        @Override
        protected boolean doProcess(Record record) {
            ListIterator iter = record.get(fieldName).listIterator();
            while (iter.hasNext()) {
                String[] segments = iter.next().toString().split(separator);
                iter.remove();
                for (int i = 0; i < segments.length; i++) {
                    if (i < newFields.size()) {
                        record.put(newFields.get(i), trimIfNeeded(segments[i]));
                    } else {
                        if (!dropUndeclaredField) {
                           record.put(String.valueOf(i), trimIfNeeded(segments[i]));
                        }
                    }
                }
            }
            return super.doProcess(record);
        }

        private String trimIfNeeded(Object input){
            return trimSegments ? input.toString().trim() : input.toString();
        }

        @Override
        protected void doNotify(Record notification) {
            LOG.debug("myNotification: {}", notification);
            super.doNotify(notification);
        }

    }
}
