package com.verifone.svc.global.ms.cucumber.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Outcome
{
    private String authorisationCode;
    private String response;
    private String responseMessage;
    private Outcome()
    {}

}
