package com.verifone.svc.global.ms.cucumber.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.TimeZone;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.DATE_TIME_PATTERN;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.NORWAY_TIME_ZONE;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.LOG_STAN_VALUE;

/**
 * @author AbhishekR2
 */
public class PayloadDataGenerator {

    public static String combine_login_initiatorId_for_Reversal;
    public static Integer saleInitiatorId;

    /**
     * it'll return the current Norway dateTime in a required form
     * @return String
     */
    public static String getCurrentNorwayDateTime() {
        LocalDateTime localNow = LocalDateTime.now(TimeZone.getTimeZone(NORWAY_TIME_ZONE).toZoneId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return dateTimeFormatter.format(localNow);
    }

    /**
     * This function generate random six digits and it'll combine it with specific log format
     *
     * @return saleInitiatorId
     */
    public static Integer generateRandomSixDigits() {
        saleInitiatorId =100000 + new Random().nextInt(900000);
        combine_login_initiatorId_for_Reversal = LOG_STAN_VALUE+saleInitiatorId;
        return saleInitiatorId;
    }
}
