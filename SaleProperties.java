package com.verifone.svc.global.ms.cucumber.commonClass;

import lombok.Getter;
import lombok.Setter;

/**
 *  these private variable is passing the required value in the respective json
 */
@Getter
@Setter
public class SaleProperties {

    private Integer saleInitiatorId;

    private String saleCurrentNorwayDateTime;

    public SaleProperties() {
    }
}
