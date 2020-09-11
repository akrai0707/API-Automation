package com.verifone.svc.global.ms.cucumber.util;

import lombok.extern.log4j.Log4j2;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import static java.lang.Boolean.FALSE;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.POSITIVE_AUTHORISATION_LOG;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.FORMAT_ERROR_LOG;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.TIMESTAMP_ISSUE_LOG;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.INVALID_ACQID_MERCHANTID_LOG;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.INVALID_AMOUNT_LOG;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.NO_OF_LINE_TO_SCAN;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.RESPONSE_STAN;

/**
 * @author AbhishekR2
 */
@Log4j2
public class LogValidation
{
    private static final String  ACQ_RESPONSE_FIELD39= "NETSBaseISOInterface 39";
    /**
     * This function is to validate the expected-log to the Run-time-logs
     *
     * @param LogFile src/test/resources/Payloads
     * @param logResponse Contains Acquirer Response message
     * @param combineLogInitatorID generated stan with concatenation of log message
     * @return Boolean
     */
    public static Boolean validateLogs(String LogFile, String logResponse, String combineLogInitatorID) {
        Boolean logMsg = null;
        try {
            String logsOfResponseMsg = "";
            String initatorID = "";
            logMsg = FALSE;
            boolean flag1 = false;
            int initatorIDCount = 0;
            int counter = 0;

            File file = new File(LogFile);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains(logResponse)) {
                    logsOfResponseMsg = line;
                    flag1 = true;
                }
                if (line.contains(combineLogInitatorID)) {
                    initatorID = line;
                    initatorIDCount++;
                }
                if (flag1 && initatorIDCount == RESPONSE_STAN) {
                    if (line.contains(ACQ_RESPONSE_FIELD39)) {
                        log.info(logsOfResponseMsg);
                        log.info(initatorID);
                        log.info(line);
                        printErrorLogs(line);
                        logMsg = true;
                        break;
                    }
                    counter++;
                }
                if (counter == NO_OF_LINE_TO_SCAN) {
                    log.info(logsOfResponseMsg);
                    log.info(initatorID);
                    log.info("Response message 'NETSBaseISOInterface 39' Not Found");
                    break;
                }
            }
        }
        catch (IOException e)
        {
            log.warn("Log Validation Failed, LogFile Not Found");
        }
        return logMsg;
    }

    /**
     * this function is to initiating the validateLogs method and printing the logs as per its response
     * @param LogFile src/test/resources/Payloads
     * @param logResponse boolean
     * @param combineLogInitatorID generated stan with concatenation of log message
     */
    public static void initiateValidateLogs(String LogFile, String logResponse, String combineLogInitatorID)
    {
        boolean response = validateLogs(LogFile,logResponse,combineLogInitatorID);
        if (response) {
            log.info("Log Validation Successfully Completed");
        } else {
            log.warn("Log Validation Failed");
        }
    }

    /**
     * @param line Printing the logs according to the Acq response
     */
    public static void printErrorLogs(String line){
        if(line.contains(POSITIVE_AUTHORISATION_LOG)){
            log.warn(" : Positive authorisation made in STIP.");
        }
        if(line.contains(FORMAT_ERROR_LOG)){
            log.warn(" : Format error.");
        }
        if ((line.contains(TIMESTAMP_ISSUE_LOG)))
        {
            log.warn(" : Timestamp in P12/P13 differs more than permitted (± 75 minutes).");
        }
        if ((line.contains(INVALID_ACQID_MERCHANTID_LOG)))
        {
            log.warn(" : Invalid acquirer Id (P32) or merchant no (P42).");
        }
        if ((line.contains(INVALID_AMOUNT_LOG)))
        {
            log.warn(" : Invalid amount.");
        }
    }
}
