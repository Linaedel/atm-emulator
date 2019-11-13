package ru.sbrf.ku.atm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ATMLogger {
    private static Logger logger = LoggerFactory.getLogger(ATMLogger.class);

    public static Logger getLogger() {
        return logger;
    }
}
