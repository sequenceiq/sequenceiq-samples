package com.sequenceiq.lastfm.etl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.typesafe.config.Config;

public class LatestSongCommand implements CommandBuilder {

    public static final String HIGHER = ">";
    public static final String HIGHER_OR_EQUALS = ">=";
    public static final String LOWER = "<";
    public static final String LOWER_OR_EQUALS = "<=";
    public static final String EQUALS = "=";
    public static final String NOT_EQUALS = "!=";

    @Override
    public Collection<String> getNames() {
        return Collections.singletonList("latestSongs");
    }

    @Override
    public Command build(Config config, Command command, Command command2, MorphlineContext morphlineContext) {
        return new DateCheck(this, config, command, command2, morphlineContext);
    }

    private static final class DateCheck extends AbstractCommand {

        private final String fieldName;
        private final String command;

        public DateCheck(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
            super(builder, config, parent, child, context);
            this.fieldName = getConfigs().getString(config, "field");
            this.command = getConfigs().getString(config, "command");
            LOG.debug("fieldName: {}", fieldName);
            validateArguments();
        }

        @Override
        protected boolean doProcess(Record record) {
            Map attachmentBody = (LinkedHashMap) record.get("_attachment_body").get(0);
            String fieldValue = attachmentBody.get(fieldName).toString();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                int commandDate = Integer.valueOf(command
                        .replaceAll(LOWER, "")
                        .replaceAll(HIGHER, "")
                        .replaceAll(NOT_EQUALS, "")
                        .replaceAll(EQUALS, "")
                        .trim());
                Date fieldDate = sdf.parse(fieldValue);
                Calendar fieldValueAsCalendar = Calendar.getInstance();
                fieldValueAsCalendar.setTime(fieldDate);
                if (command.startsWith(HIGHER_OR_EQUALS)) {
                    if (fieldValueAsCalendar.get(Calendar.YEAR) < commandDate) {
                        return false;
                    }
                } else if (command.startsWith(LOWER_OR_EQUALS)) {
                    if (fieldValueAsCalendar.get(Calendar.YEAR) > commandDate) {
                        return false;
                    }
                } else if (command.startsWith(NOT_EQUALS)) {
                    if (fieldValueAsCalendar.get(Calendar.YEAR) == commandDate) {
                        return false;
                    }
                } else if (command.startsWith(HIGHER)) {
                    if (fieldValueAsCalendar.get(Calendar.YEAR) < commandDate) {
                        return false;
                    }
                } else if (command.startsWith(LOWER)) {
                    if (fieldValueAsCalendar.get(Calendar.YEAR) > commandDate) {
                        return false;
                    }
                } else if (command.startsWith(EQUALS)) {
                    if (fieldValueAsCalendar.get(Calendar.YEAR) != commandDate) {
                        return false;
                    }
                } else {
                    LOG.debug("bad command syntax");
                }
            } catch (ParseException e) {
                LOG.debug("parse exception: " + e.getMessage());
            }

            return super.doProcess(record);
        }

        @Override
        protected void doNotify(Record notification) {
            LOG.debug("myNotification: {}", notification);
            super.doNotify(notification);
        }

    }
}
