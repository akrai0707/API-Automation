package com.verifone.svc.global.ms.cucumber.stepDefinations;

import com.verifone.svc.global.ms.cucumber.BaseIntegration;
import com.verifone.svc.global.ms.cucumber.commonClass.PropertyFile;
import com.verifone.svc.global.ms.cucumber.commonClass.ReversalProperties;
import com.verifone.svc.global.ms.cucumber.commonClass.SaleProperties;
import com.verifone.svc.global.ms.cucumber.util.PayloadDataGenerator;
import com.verifone.svc.global.ms.cucumber.util.PayloadProcessor;
import com.verifone.svc.global.ms.cucumber.util.ReadJsonPayload;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import javax.annotation.PostConstruct;
import java.util.Map;
import static com.verifone.svc.global.ms.cucumber.util.LogValidation.initiateValidateLogs;
import static com.verifone.svc.global.ms.cucumber.util.PayloadDataGenerator.generateRandomSixDigits;
import static com.verifone.svc.global.ms.cucumber.util.PayloadDataGenerator.getCurrentNorwayDateTime;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.LOG_FILE_PATH;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.SERVER_FILE_NAME;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.JSON_FILE_PATH;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.URL;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.STATUS_URL;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.LOG_STAN_VALUE;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.UP_PIM_STATUS;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.LOG_ECHO_RESPONSE210;
import static com.verifone.svc.global.ms.cucumber.constant.AutomationConstants.LOG_ECHO_RESPONSE430;

/**
 * @author AbhishekR2
 */
@Log4j2
public class StepDefLogValidation extends BaseIntegration {

    @Autowired
    private SaleProperties saleProperties;

    @Autowired
    private ReversalProperties reversalProperties;

    @Autowired
    private PropertyFile propertyFile;

    @Autowired
    private ReadJsonPayload readJsonPayload;

    @Autowired
    private PayloadProcessor payloadProcessor;

    @Autowired
    private PayloadDataGenerator payloadDataGenerator;

    private String combine_login_initiatorId;
    private String url;
    private String logFilePath;
    private String serverFileName;
    private String jsonFilePath;
    private String statusUrl;

    @PostConstruct
    public void setUpPropertiesValue() {
        Map<String, Object> metadata = propertyFile.getMetadata();
        logFilePath = (String) metadata.get(LOG_FILE_PATH);
        serverFileName = (String) metadata.get(SERVER_FILE_NAME);
        jsonFilePath = (String) metadata.get(JSON_FILE_PATH);
        url = (String) metadata.get(URL);
        statusUrl = (String) metadata.get(STATUS_URL);
        Integer saleInitiatorId = generateRandomSixDigits();
        combine_login_initiatorId = LOG_STAN_VALUE + saleInitiatorId;
        saleProperties.setSaleInitiatorId(saleInitiatorId);
        saleProperties.setSaleCurrentNorwayDateTime(getCurrentNorwayDateTime());
    }

    @Given("^Verify Nets_SDI Pim is UP and running$")
    public void checkNetsPimStatus() {
        ResponseEntity<String> responseEntity = readJsonPayload.appStatus(statusUrl);
        if (nonNull(responseEntity) && isNotBlank(responseEntity.getBody())) {
            assertTrue(responseEntity.getBody().contains(UP_PIM_STATUS));
            log.info("NetsPim is UP and running.");
        } else {
            log.warn("NetsPim is DOWN.");
        }
    }

    @When("Nets PIM API gets invoked with fileName {string} and contextPath {string}")
    public void invokingNetsPimApi(String fileName, String contextPath){
        assertNotNull(readJsonPayload.initiateInvokingNetsPim(url, contextPath, jsonFilePath, fileName));
    }

    @When("Nets PIM API gets invoked for Reversal transaction with SaleFileName {string} and " +
            "SaleContextPath {string} " + "and ReversalFileName {string}  and ReversalContextPath {string}")
    public void invokingNetsPimApiForReversal(String saleFileName, String saleContextPath, String fileName,
                                              String contextPath){
        readJsonPayload.invokeSaleAndAddReversalParameters(url, saleContextPath, jsonFilePath, saleFileName);
        assertNotNull(readJsonPayload.invokeDeleteAPI(url, contextPath, jsonFilePath, fileName, reversalProperties
                .getAuthorisationCode()));
    }
    @When("Nets PIM API gets invoked with invalid merchantID {string} with fileName {string} and contextPath {string}")
    public void invokePimInvalidMerchantID(String invalidMerchantId, String fileName, String contextPath) {
        assertNotNull(payloadProcessor.invokingNetsPimForInvalidData(url,contextPath,jsonFilePath,fileName,
                invalidMerchantId, StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY,
                StringUtils.EMPTY,StringUtils.EMPTY));
    }

    @When("Nets PIM API gets invoked with invalid AcquiringInstitutionCode {string} with fileName {string} " +
            "and contextPath {string}")
    public void invokePimWithInvalidAcqId(String acquiringInstitutionCode, String fileName, String contextPath) {
        assertNotNull(payloadProcessor.invokingNetsPimForInvalidData(url,contextPath,jsonFilePath,fileName,
                StringUtils.EMPTY,acquiringInstitutionCode,StringUtils.EMPTY,StringUtils.EMPTY,
                StringUtils.EMPTY,StringUtils.EMPTY));
    }

    @When("Nets PIM API gets invoked with invalid totalAmountValue {string} with fileName {string} " +
            "and contextPath {string}")
    public void invokePimWithInvalidAmount(String totalAmountValue, String fileName, String contextPath) {
        assertNotNull(payloadProcessor.invokingNetsPimForInvalidData(url,contextPath,jsonFilePath,fileName,
                StringUtils.EMPTY,StringUtils.EMPTY,totalAmountValue,StringUtils.EMPTY,
                StringUtils.EMPTY,StringUtils.EMPTY));
    }

    @When("Nets PIM API gets invoked with invalid currencyCode {string} with fileName {string} " +
            "and contextPath {string}")
    public void invokePimWithInvalidCurrencyCode(String currencyCode, String fileName, String contextPath) {
        assertNotNull(payloadProcessor.invokingNetsPimForInvalidData(url,contextPath,jsonFilePath,fileName,
                StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY,currencyCode,
                StringUtils.EMPTY,StringUtils.EMPTY));
    }

    @When("Nets PIM API gets invoked with invalid createdDateTime {string} with fileName {string} and " +
            "contextPath {string}")
    public void invokePimWithInvalidCreatedDateTime(String invalidCreatedDateTime,String fileName,String contextPath){
        assertNotNull(payloadProcessor.invokingNetsPimForInvalidData(url,contextPath,jsonFilePath,fileName,
                StringUtils.EMPTY, StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY,invalidCreatedDateTime,
                StringUtils.EMPTY));
    }

    @When("Nets PIM API gets invoked with invalid initiatorTraceId {string} with fileName {string} " +
            "and contextPath {string}")
    public void invokePimWithInvalidStan(String invalidInitiatorTraceId, String fileName, String contextPath) {
        assertNotNull(payloadProcessor.invokingNetsPimForInvalidData(url,contextPath,jsonFilePath,fileName,
                StringUtils.EMPTY, StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY,
                invalidInitiatorTraceId));
    }

    @When("Nets PIM API gets invoked for Reversal transaction with invalid merchantID {string} with SaleFileName " +
            "{string} and SaleContextPath {string} and ReversalFileName {string}  and ReversalContextPath {string}")
    public void PimInvokedForReversalTransWithInvalidMerchantID(String invalidMerchantId,String saleFileName,
                                       String saleContextPath, String fileName, String contextPath) {
        readJsonPayload.invokeSaleAndAddReversalParameters(url, saleContextPath, jsonFilePath, saleFileName);
        assertNotNull(payloadProcessor.invokeDeleteAPIForInvalidData(url, contextPath, jsonFilePath, fileName,
                reversalProperties.getAuthorisationCode(),invalidMerchantId,StringUtils.EMPTY,
                StringUtils.EMPTY, StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY));

    }

    @When("Nets PIM API gets invoked for Reversal transaction with invalid AcquiringInstitutionCode {string} and " +
            "SaleFileName {string} and SaleContextPath {string} and ReversalFileName {string}" +
            " and ReversalContextPath {string}")
    public void PimInvokedForReversalTransWithInvalidAcqId(String invalidAcqId, String saleFileName,
                                         String saleContextPath, String fileName, String contextPath) {
        readJsonPayload.invokeSaleAndAddReversalParameters(url, saleContextPath, jsonFilePath, saleFileName);
        assertNotNull(payloadProcessor.invokeDeleteAPIForInvalidData(url, contextPath, jsonFilePath, fileName,
                reversalProperties.getAuthorisationCode(),StringUtils.EMPTY,
                invalidAcqId,StringUtils.EMPTY, StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY));

    }

    @When("Nets PIM API gets invoked for Reversal transaction with invalid totalAmountValue {string} and " +
            "SaleFileName {string} and SaleContextPath {string} and ReversalFileName {string}  " +
            "and ReversalContextPath {string}")
    public void PimInvokedForReversalTransWithInvalidAmount(String invalidAmount, String saleFileName,
                                         String saleContextPath, String fileName, String contextPath) {
        readJsonPayload.invokeSaleAndAddReversalParameters(url, saleContextPath, jsonFilePath, saleFileName);
        assertNotNull(payloadProcessor.invokeDeleteAPIForInvalidData(url, contextPath, jsonFilePath, fileName,
                reversalProperties.getAuthorisationCode(),StringUtils.EMPTY,StringUtils.EMPTY,invalidAmount,
                StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY));

    }

    @When("Nets PIM API gets invoked for Reversal transaction with invalid currencyCode {string} " +
            "SaleFileName {string} and SaleContextPath {string} and ReversalFileName {string}  " +
            "and ReversalContextPath {string}")
    public void PimInvokedForReversalTransWithInvalidCurrencyCode(String invalidCurrencyCode,String saleFileName,
                                           String saleContextPath, String fileName, String contextPath) {
        readJsonPayload.invokeSaleAndAddReversalParameters(url, saleContextPath, jsonFilePath, saleFileName);
        assertNotNull(payloadProcessor.invokeDeleteAPIForInvalidData(url, contextPath, jsonFilePath, fileName,
                reversalProperties.getAuthorisationCode(),StringUtils.EMPTY,StringUtils.EMPTY,StringUtils.EMPTY,
                invalidCurrencyCode,StringUtils.EMPTY,StringUtils.EMPTY));
    }

    @When("Nets PIM API gets invoked for Reversal transaction with invalid createdDateTime {string} " +
            "SaleFileName {string} and SaleContextPath {string} and ReversalFileName {string}  " +
            "and ReversalContextPath {string}")
    public void PimInvokedForReversalTransWithInvalidDateTime(String invalidDateTime,String saleFileName,
                                             String saleContextPath, String fileName, String contextPath) {
        readJsonPayload.invokeSaleAndAddReversalParameters(url, saleContextPath, jsonFilePath, saleFileName);
        assertNotNull(payloadProcessor.invokeDeleteAPIForInvalidData(url, contextPath, jsonFilePath,
                fileName, reversalProperties.getAuthorisationCode(),StringUtils.EMPTY,StringUtils.EMPTY,
                StringUtils.EMPTY,StringUtils.EMPTY,invalidDateTime,StringUtils.EMPTY));
    }

    @When("Nets PIM API gets invoked for Reversal transaction with invalid initiatorTraceId {string} " +
            "SaleFileName {string} and SaleContextPath {string} and ReversalFileName {string}" +
            "  and ReversalContextPath {string}")
    public void PimInvokedForReversalTransWithInvalidStan(String invalidStan, String saleFileName,
                                            String saleContextPath, String fileName, String contextPath) {
        readJsonPayload.invokeSaleAndAddReversalParameters(url, saleContextPath, jsonFilePath, saleFileName);
        assertNotNull(payloadProcessor.invokeDeleteAPIForInvalidData(url, contextPath, jsonFilePath, fileName,
                reversalProperties.getAuthorisationCode(),StringUtils.EMPTY,StringUtils.EMPTY,
                StringUtils.EMPTY, StringUtils.EMPTY,StringUtils.EMPTY,invalidStan));
    }

    @Then("Verifying transactions are successful and logs are valid for Reversal transactions")
    public void logValidationReversal(){
        initiateValidateLogs(logFilePath.concat(serverFileName), LOG_ECHO_RESPONSE430,
                PayloadDataGenerator.combine_login_initiatorId_for_Reversal);
    }

    @Then("Verifying transactions are successful and logs are valid")
    public void logValidation(){
        initiateValidateLogs(logFilePath.concat(serverFileName), LOG_ECHO_RESPONSE210, combine_login_initiatorId);
    }
}
