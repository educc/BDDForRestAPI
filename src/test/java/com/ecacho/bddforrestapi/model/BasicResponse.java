package com.ecacho.bddforrestapi.model;

import lombok.Data;

@Data
public class BasicResponse {

    int row;
    int status;
    String bodyRaw;
}
