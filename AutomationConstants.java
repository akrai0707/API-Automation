package com.verifone.svc.global.ms.cucumber.constant;

public final class AutomationConstants {

    public static final String SALE_FILE_NAME = "salePayload.json";
    public static final String REFUND_FILE_NAME = "refundPayload.json";
    public static final String LOG_STAN_VALUE = "NETSBaseISOInterface 11 ==> ";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String NORWAY_TIME_ZONE ="Europe/Oslo";
    public static final String LOG_FILE_PATH ="LogFilePath";
    public static final String SERVER_FILE_NAME ="serverFileName";
    public static final String JSON_FILE_PATH ="jsonFilePath";
    public static final String URL ="url";
    public static final String STATUS_URL ="statusUrl";
    public static final String UP_PIM_STATUS ="UP";
    public static final String CANCEL_TRANSACTION ="cancelTransaction";
    public static final String AUTHORISATION_CODE ="authorisationCode";
    public static final String INITIATOR_TRACE_ID ="initiatorTraceId";
    public static final String CREATED_DATE_TIME ="createdDateTime";
    public static final String TRANSACTION ="transaction";
    public static final String MERCHANT ="merchant";
    public static final String MERCHANT_ID ="merchantId";
    public static final String ACQUIRING_INSTITUTION_CODE ="acquiringInstitutionCode";
    public static final String TOTAL_AMOUNT ="totalAmount";
    public static final String CURRENCY_CODE ="currencyCode";
    public static final String TOTAL_AMOUNT_VALUE ="value";
    public static final String POSITIVE_AUTHORISATION_LOG ="NETSBaseISOInterface 39 ==> 00";
    public static final String TIMESTAMP_ISSUE_LOG ="NETSBaseISOInterface 39 ==> 9A";
    public static final String FORMAT_ERROR_LOG ="NETSBaseISOInterface 39 ==> 30";
    public static final String INVALID_ACQID_MERCHANTID_LOG ="NETSBaseISOInterface 39 ==> 03";
    public static final String INVALID_AMOUNT_LOG ="NETSBaseISOInterface 39 ==> 13";
    public static final Integer NO_OF_LINE_TO_SCAN =15;
    public static final Integer RESPONSE_STAN =2;
    public static final String LOG_ECHO_RESPONSE210 = "NETSBaseISOInterface --------- [0210]";
    public static final String LOG_ECHO_RESPONSE430 = "NETSBaseISOInterface --------- [0430]";

    private AutomationConstants() {
    }
}
