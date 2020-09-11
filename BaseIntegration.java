package com.verifone.svc.global.ms.cucumber;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * @author AbhishekR2
 * This class is instantiating the required bean
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RestTemplate.class,ApplicationTestConfiguration.class})
public class BaseIntegration {
}