package com.verifone.svc.global.ms.cucumber.util;

import com.verifone.svc.global.ms.cucumber.commonClass.ReversalProperties;
import com.verifone.svc.global.ms.cucumber.commonClass.SaleProperties;
import com.verifone.svc.global.ms.cucumber.pojo.ResponseData;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.FileReader;
import java.io.IOException;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.AUTHORISATION_CODE;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.TRANSACTION;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.MERCHANT;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.TOTAL_AMOUNT;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.INITIATOR_TRACE_ID;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.CREATED_DATE_TIME;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.MERCHANT_ID;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.ACQUIRING_INSTITUTION_CODE;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.CANCEL_TRANSACTION;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.CURRENCY_CODE;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.TOTAL_AMOUNT_VALUE;
import static com.verifone.svc.global.ms.cucumber.util.PayloadDataGenerator.generateRandomSixDigits;
import static com.verifone.svc.global.ms.cucumber.util.PayloadDataGenerator.getCurrentNorwayDateTime;
import static org.springframework.http.HttpMethod.DELETE;

/**
 * @author AbhishekR2
 */
@Log4j2
public class PayloadProcessor {

    @Autowired
    private SaleProperties saleProperties;

    @Autowired
    private ReversalProperties reversalProperties;

    private RestTemplate restTemplate;

    private HttpHeaders headers;

    public PayloadProcessor() {
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * This function is used to invoke Nets-Pim and from its response fetching the Response and Response message
     *
     * @param url          http://localhost:9026/api/v1/full/
     * @param contextPath  endpoints : sales/refunds
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     salePayload.json/refundPayload.json
     * @return responseEntity
     */
    public ResponseEntity<ResponseData> invokingNetsPimForInvalidData(final String url,final String contextPath,
                                                                      final String jsonFilePath,final String fileName,
                                                                      final String invalidMerchantId,
                                                                      final String acquiringInstitutionCode,
                                                                      final String totalAmountValue,
                                                                      final String currencyCode,
                                                                      final String invalidCreatedDateTime,
                                                                      final String invalidInitiatorTraceId){
        ResponseEntity<ResponseData> responseEntity = null;
        try {
            HttpEntity<Object> request = new HttpEntity<>(writeJsonForInvalidValue(jsonFilePath, fileName,
                    invalidMerchantId, acquiringInstitutionCode, totalAmountValue, currencyCode,
                    invalidCreatedDateTime, invalidInitiatorTraceId), headers);
            responseEntity = restTemplate.postForEntity(url.concat(contextPath), request, ResponseData.class);
            String jsonResponse =responseEntity.getBody().getResponse().getPaymentData().getOutcome().getResponse();
            String jsonResponseMessage =responseEntity.getBody().getResponse().getPaymentData()
                    .getOutcome().getResponseMessage();
            log.info(": Json Response : "+jsonResponse);
            log.info(": Json Response Message : "+jsonResponseMessage);
        }
        catch (Exception e) {
            log.error("Post API for Invalid Data failed. {}", e.getMessage());
        }
        return responseEntity;
    }

    /**
     * This function is used to update the required parameters(valid and Invalid) in the respective json file
     *
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     salePayload.json/refundPayload.json
     * @return jsonObject
     */
    private JSONObject writeJsonForInvalidValue(final String jsonFilePath,final String fileName,
                                                final String invalidMerchantId,final String acquiringInstitutionCode,
                                                final String totalAmountValue,final String currencyCode,
                                                final String invalidCreatedDateTime,
                                                final String invalidInitiatorTraceId){
        JSONObject jsonObject =null;
        try {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(new FileReader(jsonFilePath.concat(fileName)));

            JSONObject transactionValues = (JSONObject) jsonObject.get(TRANSACTION);
            JSONObject transactionMerchantValues = (JSONObject)transactionValues.get(MERCHANT);
            JSONObject transactionTotalAmountValues = (JSONObject)transactionValues.get(TOTAL_AMOUNT);
            if(!invalidInitiatorTraceId.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, invalidInitiatorTraceId);
                transactionValues.put(CREATED_DATE_TIME, saleProperties.getSaleCurrentNorwayDateTime());
            }
            if(!invalidCreatedDateTime.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, saleProperties.getSaleInitiatorId());
                transactionValues.put(CREATED_DATE_TIME, invalidCreatedDateTime);
            }
            if(!invalidMerchantId.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, saleProperties.getSaleInitiatorId());
                transactionValues.put(CREATED_DATE_TIME, saleProperties.getSaleCurrentNorwayDateTime());
                transactionMerchantValues.put(MERCHANT_ID, invalidMerchantId);
            }
            if(!acquiringInstitutionCode.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, saleProperties.getSaleInitiatorId());
                transactionValues.put(CREATED_DATE_TIME, saleProperties.getSaleCurrentNorwayDateTime());
                transactionMerchantValues.put(ACQUIRING_INSTITUTION_CODE, acquiringInstitutionCode);
            }
            if(!currencyCode.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, saleProperties.getSaleInitiatorId());
                transactionValues.put(CREATED_DATE_TIME, saleProperties.getSaleCurrentNorwayDateTime());
                transactionTotalAmountValues.put(CURRENCY_CODE, currencyCode);
            }
            if(!totalAmountValue.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, saleProperties.getSaleInitiatorId());
                transactionValues.put(CREATED_DATE_TIME, saleProperties.getSaleCurrentNorwayDateTime());
                transactionTotalAmountValues.put(TOTAL_AMOUNT_VALUE, totalAmountValue);
            }

        }
        catch (IOException | ParseException e)
        {
            log.error("Failed to write json file for Invalid Value. {}", e.getMessage());
        }
        return jsonObject;
    }


    /**
     * This function is used to invoke Nets-Pim and from its response fetching the Response and Response message
     *
     * @param url          http://localhost:9026/api/v1/full/
     * @param contextPath  endpoints : transactions
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     reversalPayload.json
     * @return responseEntity
     */
    public ResponseEntity<ResponseData> invokeDeleteAPIForInvalidData(final String url,
                                                                      final String contextPath,
                                                                      final String jsonFilePath,
                                                                      final String fileName,
                                                                      final String authorisationCode,final String invalidMerchantId,
                                                                      final String acquiringInstitutionCode,
                                                                      final String totalAmountValue,final String currencyCode,
                                                                      final String invalidCreatedDateTime,
                                                                      final String invalidInitiatorTraceId)  {
        ResponseEntity<ResponseData> responseEntity = null;
        try {
            HttpEntity<Object> request = new HttpEntity<>(writeJsonForInvalidData(jsonFilePath, fileName,
                    authorisationCode,invalidMerchantId, acquiringInstitutionCode, totalAmountValue, currencyCode,
                    invalidCreatedDateTime, invalidInitiatorTraceId), headers);
            responseEntity = restTemplate.exchange(url.concat(contextPath), DELETE, request, ResponseData.class);
            String jsonResponse =responseEntity.getBody().getResponse().getPaymentData().getOutcome().getResponse();
            String jsonResponseMessage =responseEntity.getBody().getResponse().getPaymentData()
                    .getOutcome().getResponseMessage();
            log.info(": Json Response : "+jsonResponse);
            log.info(": Json Response Message : "+jsonResponseMessage);
        }
        catch (Exception e) {
            log.error("Delete API failed. {}", e.getMessage());
        }
        return responseEntity;
    }


    /**
     * This function is used to update the required parameters in the respective json file
     *
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     reversalPayload.json
     * @return jsonObject
     */
    public JSONObject writeJsonForInvalidData(final String jsonFilePath, final String fileName,
                                              final String authorisationCode, final String invalidMerchantId,
                                              final String acquiringInstitutionCode,final String totalAmountValue,
                                              final String currencyCode, final String invalidCreatedDateTime,
                                              final String invalidInitiatorTraceId){
        JSONObject jsonObject;
        jsonObject = initiateProcessForReversalInvalidData(jsonFilePath, fileName,invalidMerchantId,
                acquiringInstitutionCode, totalAmountValue, currencyCode,
                invalidCreatedDateTime, invalidInitiatorTraceId);
        JSONObject cancelTransactionValues = (JSONObject) jsonObject.get(CANCEL_TRANSACTION);
        cancelTransactionValues.put(AUTHORISATION_CODE, authorisationCode);
        cancelTransactionValues.put(INITIATOR_TRACE_ID, reversalProperties.getSaleProperties()
                .getSaleInitiatorId());
        cancelTransactionValues.put(CREATED_DATE_TIME, reversalProperties.getSaleProperties()
                .getSaleCurrentNorwayDateTime());
        return jsonObject;
    }

    /**
     * This function is used to update the required parameters(valid and Invalid) in the respective json file
     *
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     reversalPayload.json
     * @return jsonObject
     */
    private JSONObject initiateProcessForReversalInvalidData(final String jsonFilePath, final String fileName,
                                                             final String invalidMerchantId,
                                                             final String acquiringInstitutionCode,
                                                             final String totalAmountValue,final String currencyCode,
                                                             final String invalidCreatedDateTime,
                                                             final String invalidInitiatorTraceId){
        JSONObject jsonObject =null;
        try {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(new FileReader(jsonFilePath.concat(fileName)));
            JSONObject transactionValues = (JSONObject) jsonObject.get(TRANSACTION);
            JSONObject transactionMerchantValues = (JSONObject)transactionValues.get(MERCHANT);
            JSONObject transactionTotalAmountValues = (JSONObject)transactionValues.get(TOTAL_AMOUNT);

            if(!invalidInitiatorTraceId.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, invalidInitiatorTraceId);
                transactionValues.put(CREATED_DATE_TIME, getCurrentNorwayDateTime());
            }
            if(!invalidCreatedDateTime.isEmpty()) {
                transactionValues.put(CREATED_DATE_TIME, invalidCreatedDateTime);
                transactionValues.put(INITIATOR_TRACE_ID, generateRandomSixDigits());
            }
            if(!invalidMerchantId.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, generateRandomSixDigits());
                transactionValues.put(CREATED_DATE_TIME, getCurrentNorwayDateTime());
                transactionMerchantValues.put(MERCHANT_ID, invalidMerchantId);
            }
            if(!acquiringInstitutionCode.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, generateRandomSixDigits());
                transactionValues.put(CREATED_DATE_TIME, getCurrentNorwayDateTime());
                transactionMerchantValues.put(ACQUIRING_INSTITUTION_CODE, acquiringInstitutionCode);
            }
            if(!currencyCode.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, generateRandomSixDigits());
                transactionValues.put(CREATED_DATE_TIME, getCurrentNorwayDateTime());
                transactionTotalAmountValues.put(CURRENCY_CODE, currencyCode);
            }
            if(!totalAmountValue.isEmpty()) {
                transactionValues.put(INITIATOR_TRACE_ID, generateRandomSixDigits());
                transactionValues.put(CREATED_DATE_TIME, getCurrentNorwayDateTime());
                transactionTotalAmountValues.put(TOTAL_AMOUNT_VALUE, totalAmountValue);
            }
        }
        catch (IOException | ParseException e) {
            log.error("Failed to read json fileReader file. {}", e.getMessage());
        }
        return jsonObject;
    }

}
