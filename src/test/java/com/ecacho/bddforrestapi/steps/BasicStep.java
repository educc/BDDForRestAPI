package com.ecacho.bddforrestapi.steps;

import com.ecacho.bddforrestapi.parsers.DataInputReader;
import com.ecacho.bddforrestapi.parsers.DataInputReaderFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BasicStep {

    private List<Map<String, Object>> dataForTest;
    private DataInputReaderFactory readerFactory;

    public BasicStep() {
        this.readerFactory = DataInputReaderFactory.getInstance();
    }

    @Given("^los datos entrada obtenidos de \"(.*)\" con la estructura \"(.*)\"$")
    public void loadData(String file, String structure) throws Exception {
        log.info("load data: " + file);
        dataForTest = new LinkedList<>();

        Optional<DataInputReader> reader = this.readerFactory.getReaderByFilename(file);
        if(!reader.isPresent()){
            throw new Exception("Doesn't reader exists for this type of file: " + file);
        }

        try {
            reader.get().open(file);

            Optional<Map<String, Object>> objects = reader.get().readline();
            if(objects.isPresent()){
                this.dataForTest.add(objects.get());
            }
        }finally {
            reader.get().close();
        }

        if(dataForTest.isEmpty()){
            throw new Exception("Doesn't fond data on that file, musk 2 lines as minimum: " + file);
        }
        log.info(String.format("Load {%d} objects for test", this.dataForTest.size()));
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
