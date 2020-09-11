package com.verifone.svc.global.ms.cucumber.commonClass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReversalProperties {

    private SaleProperties saleProperties;

    private String authorisationCode;

    public ReversalProperties() {
    }
}
