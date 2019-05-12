package com.ecacho.bddforrestapi.service;

import com.ecacho.bddforrestapi.model.ClientModel;

import java.util.Optional;

public interface IClientService {

    boolean save(ClientModel model);
    Optional<ClientModel> findById(int idClient);
}
