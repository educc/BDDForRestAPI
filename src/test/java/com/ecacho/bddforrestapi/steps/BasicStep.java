package com.ecacho.bddforrestapi.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicStep {

    @Given("^los datos entrada obtenidos de \"(.*)\"")
    public void loadData(String file){
        log.info("load data: " + file);
    }
    @When("^se llame al endpoint \"(.*)\" por el metodo \"(.*)\"$")
    public void executeEndpoint(String endpoint, String method){
        log.info(String.format("prepare http client: %s - %s", endpoint, method));
    }

    @And("^enviando los datos \"(.*)\"$")
    public void sendingDataToEndpoint(String cols){
        log.info(String.format("sending data: %s", cols));
    }

    @Then("^el codigo http del endpoint es \"(.*)\"$")
    public void verifyHttpStatus(int statusCode){
        log.info(String.format("verify status code: %d", statusCode));
    }

    @And("^el codigo de la respuesta del endpoint es \"(.*)\"$")
    public void verifyResponseCode(int responseCode){
        log.info(String.format("verify response code: %d", responseCode));
    }
}
