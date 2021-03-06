package org.freeshr.journal.utils.log4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class NormalLogFilter extends Filter{
   
    @Override
    public int decide(LoggingEvent loggingEvent) {
        return StringUtils.startsWith(loggingEvent.getMessage().toString(), "ACCESS:") ? -1 : 1;
    }
}
