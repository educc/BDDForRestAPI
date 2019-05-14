package com.ecacho.bddforrestapi.service.impl;

import com.ecacho.bddforrestapi.model.ClientModel;
import com.ecacho.bddforrestapi.service.IClientService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IClientServiceImpl implements IClientService {

    private List<ClientModel> clients;

    public IClientServiceImpl() {

        this.clients = new ArrayList<>();
        this.clients.add(new ClientModel(1, "edu", "dev", "Internet"));
        this.clients.add(new ClientModel(2, "Steve", "dev", null));
    }

    @Override
    public boolean save(ClientModel model) {
        model.setIdClient(clients.size()+1);
        clients.add(model);
        return true;
    }

    @Override
    public Optional<ClientModel> findById(int idClient) {
        return clients.stream().filter(it -> it.getIdClient() == idClient)
                .findFirst();
    }
}
