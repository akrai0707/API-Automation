package com.verifone.svc.global.ms.cucumber;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
/**
 * @author AbhishekR2
 * To run the feature file giving its path in features
 * Plugin is use to generate reports that contains scenarios pass/fail result
 * Glue is locating the stepDefinition file
 * tags is defining the tags that needs to be executed
 */
//{"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"}
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/featuresFiles",
        plugin = {"pretty", "html:target/cucumber-html-report"},
        glue = "com.verifone.svc.global.ms",
        tags = {"@RegressionTest"}
)
public class TestRunner {
}