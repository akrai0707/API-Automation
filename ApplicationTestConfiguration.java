package com.verifone.svc.global.ms.cucumber;

import com.verifone.svc.global.ms.cucumber.commonClass.PropertyFile;
import com.verifone.svc.global.ms.cucumber.commonClass.ReversalProperties;
import com.verifone.svc.global.ms.cucumber.commonClass.SaleProperties;
import com.verifone.svc.global.ms.cucumber.util.PayloadDataGenerator;
import com.verifone.svc.global.ms.cucumber.util.PayloadProcessor;
import com.verifone.svc.global.ms.cucumber.util.ReadJsonPayload;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
/**
 * @author AbhishekR2
 */

/**
 * It is fetching the data from applicationTest.properties for prefix:se and creating the bean for different classes
 *
 * @return propertyFile
 */

@EnableConfigurationProperties
@PropertySource("classpath:applicationTest.properties")
public class ApplicationTestConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "se")
    public PropertyFile propertyFile() {
        return new PropertyFile();
    }

    @Bean
    public SaleProperties saleProperties() {
        return new SaleProperties();
    }

    @Bean
    public ReadJsonPayload readJsonPayload() {
        return new ReadJsonPayload();
    }

    @Bean
    public ReversalProperties reversalProperties() { return new ReversalProperties(); }

    @Bean
    public PayloadProcessor payloadProcessor()
    {
        return new PayloadProcessor();
    }

    @Bean
    public PayloadDataGenerator payloadDataGenerator() { return new PayloadDataGenerator(); }
}