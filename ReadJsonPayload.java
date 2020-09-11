package com.verifone.svc.global.ms.cucumber.util;

import com.verifone.svc.global.ms.cucumber.commonClass.ReversalProperties;
import com.verifone.svc.global.ms.cucumber.commonClass.SaleProperties;
import com.verifone.svc.global.ms.cucumber.pojo.ResponseData;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import java.io.FileReader;
import java.io.IOException;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.SALE_FILE_NAME;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.REFUND_FILE_NAME;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.CANCEL_TRANSACTION;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.AUTHORISATION_CODE;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.INITIATOR_TRACE_ID;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.CREATED_DATE_TIME;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.TRANSACTION;
import static com.verifone.svc.global.ms.cucumber.util.PayloadDataGenerator.generateRandomSixDigits;
import static com.verifone.svc.global.ms.cucumber.util.PayloadDataGenerator.getCurrentNorwayDateTime;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpMethod.DELETE;

/**
 * @author AbhishekR2
 */
@Log4j2
public class ReadJsonPayload {

    private RestTemplate restTemplate;

    private HttpHeaders headers;

    @Autowired
    private SaleProperties saleProperties;

    @Autowired
    private ReversalProperties reversalProperties;

    public ReadJsonPayload() {
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * This function is used to update the required parameters in the respective json file
     *
     * @param jsonFilePath src/test/resources/
     * @param fileName     salePayload.json/refundPayload.json/reversalPayload.json
     * @return json object
     */
    public JSONObject readJson(final String jsonFilePath,
                               final String fileName,
                               final String authorisationCode){
        JSONObject jsonObject;
        String[] array = {SALE_FILE_NAME, REFUND_FILE_NAME};
        if (asList(array).contains(fileName)) {
            jsonObject = initiateProcess(jsonFilePath, fileName);
        } else {
            jsonObject = initiateProcessForReversal(jsonFilePath, fileName);
            JSONObject cancelTransactionValues = (JSONObject) jsonObject.get(CANCEL_TRANSACTION);
            cancelTransactionValues.put(AUTHORISATION_CODE, authorisationCode);
            cancelTransactionValues.put(INITIATOR_TRACE_ID, reversalProperties.getSaleProperties()
                    .getSaleInitiatorId());
            cancelTransactionValues.put(CREATED_DATE_TIME, reversalProperties.getSaleProperties()
                    .getSaleCurrentNorwayDateTime());
        }
        return jsonObject;
    }

    /**
     * This function is used to update the required parameters in the respective json file
     *
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     salePayload.json/refundPayload.json
     * @return jsonObject
     */
    private JSONObject initiateProcess(String jsonFilePath, String fileName){
        JSONObject jsonObject =null;
        try {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(new FileReader(jsonFilePath.concat(fileName)));
            JSONObject transactionValues = (JSONObject) jsonObject.get(TRANSACTION);
            transactionValues.put(INITIATOR_TRACE_ID, saleProperties.getSaleInitiatorId());
            transactionValues.put(CREATED_DATE_TIME, saleProperties.getSaleCurrentNorwayDateTime());
        }
        catch (IOException | ParseException e)
        {
            log.error("Failed to read json fileReader file. {}", e.getMessage());
        }
        return jsonObject;
    }

    /**
     * This function is used to initiate the reversal transactions process with passing required parameters
     *
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     reversalPayload.json
     * @return jsonObject
     */
    private JSONObject initiateProcessForReversal(String jsonFilePath, String fileName){
        JSONObject jsonObject =null;
        try {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(new FileReader(jsonFilePath.concat(fileName)));
            JSONObject transactionValues = (JSONObject) jsonObject.get(TRANSACTION);
            transactionValues.put(INITIATOR_TRACE_ID, generateRandomSixDigits());
            transactionValues.put(CREATED_DATE_TIME, getCurrentNorwayDateTime());
        }
        catch (IOException | ParseException e) {
            log.error("Failed to read json fileReader file. {}", e.getMessage());
        }
        return jsonObject;
    }

    /**
     * This function is used to invoke Nets-Pim
     *
     * @param url          http://localhost:9026/api/v1/full/
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     salePayload.json/refundPayload.json
     * @return responsePostEntity
     */
    public ResponseEntity<ResponseData> invokingNetsPim(String url, String contextPath, String jsonFilePath,
                                                        String fileName){
        ResponseEntity<ResponseData> responseEntity = null;
        try {
            HttpEntity<Object> request = new HttpEntity<>(readJson(jsonFilePath, fileName,
                    null), headers);
            responseEntity = restTemplate.postForEntity(url.concat(contextPath), request, ResponseData.class);
        }
        catch (Exception e) {
            log.error("Post API failed. {}", e.getMessage());
        }
        return responseEntity;
    }

    /**
     * This function is used to initiate the invokingNetsPim method for sale and Refund transactions and fetching
     * the statusCode from response
     *
     * @param url          http://localhost:9026/api/v1/full/
     * @param contextPath  endpoints : sales/refunds
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName     salePayload.json/refundPayload.json
     * @return              responseEntity
     */
    public ResponseEntity<ResponseData> initiateInvokingNetsPim(String url, String contextPath, String jsonFilePath,
                                                                String fileName) {
        ResponseEntity<ResponseData> responseEntity = null;
        try{
            responseEntity = invokingNetsPim(url, contextPath, jsonFilePath, fileName);
            log.info("Status while Invoking NetsPim :{}", responseEntity.getStatusCode());
        }
        catch (Exception e){
            log.error("Invoking NetsPim is failed. {}", e.getMessage());
        }
        return responseEntity;
    }

    /**
     * This function is used to get the Pim status
     *
     * @param status Pim LbStatus
     * @return responseEntity
     */
    public ResponseEntity<String> appStatus(String status) {
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(status, String.class);
        } catch (Exception e) {
            log.warn("Nets pim LBstatus service fail :{}", e.getMessage());
        }
        return responseEntity;
    }

    /**
     * This function is used to invoke the reversal of transactions.
     *
     * @param url            localhost:9026/api/v1/full/
     * @param contextPath    transactions
     * @param jsonFilePath   src/test/resources/Payloads
     * @param fileName       reversalPayload.json
     * @return               responseEntity
     */
    public ResponseEntity<String> invokeDeleteAPI(final String url,
                                                  final String contextPath,
                                                  final String jsonFilePath,
                                                  final String fileName,
                                                  final String authorisationCode)  {
        ResponseEntity<String> responseEntity = null;
        try {
            HttpEntity<Object> request = new HttpEntity<>(readJson(jsonFilePath, fileName, authorisationCode), headers);
            responseEntity = restTemplate.exchange(url.concat(contextPath), DELETE, request, String.class);
        }
        catch (Exception e) {
            log.error("Delete API failed. {}", e.getMessage());
        }
        return responseEntity;
    }


    /**
     * this function is use to invoke sales transactions and Setting its parameters to Reversal transactions
     *
     * @param url localhost:9026/api/v1/full/
     * @param contextPath sales/transactions
     * @param jsonFilePath src/test/resources/Payloads
     * @param fileName reversalPayload.json
     */
    public void invokeSaleAndAddReversalParameters(String url, String contextPath, String jsonFilePath,
                                                   String fileName){
        ResponseEntity<ResponseData> responseEntity = initiateInvokingNetsPim(url, contextPath, jsonFilePath, fileName);
        reversalProperties.setSaleProperties(saleProperties);
        reversalProperties.setAuthorisationCode(responseEntity.getBody().getResponse().getPaymentData()
                .getOutcome().getAuthorisationCode());
    }
}