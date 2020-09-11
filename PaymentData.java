package com.verifone.svc.global.ms.cucumber.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentData {

    private Outcome outcome;

    private PaymentData()
    {}
}

