package com.ecacho.bddforrestapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientModel {

    int idClient;
    String name;
    String lastName;
    String address;
}
