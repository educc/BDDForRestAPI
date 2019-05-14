package com.ecacho.bddforrestapi.controllers;

import com.ecacho.bddforrestapi.model.BaseResponse;
import com.ecacho.bddforrestapi.model.ClientModel;
import com.ecacho.bddforrestapi.service.IClientService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/client")
public class ClientController {

    private IClientService clientService;

    public ClientController(IClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/by-id")
    public BaseResponse getClient(@RequestParam("idClient") int idClient){
        Optional<ClientModel> client = this.clientService.findById(idClient);
        if(client.isPresent()){
            return new BaseResponse(0, client);
        }
        return new BaseResponse(-1, null);
    }

    @PostMapping("/save")
    public BaseResponse saveClient(@RequestBody ClientModel client){
        boolean result = this.clientService.save(client);
        if(result){
            return new BaseResponse(0, null);
        }
        return new BaseResponse(-1, null);
    }

}
