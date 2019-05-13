package com.ecacho.bddforrestapi.steps;

import com.ecacho.bddforrestapi.SpringIntegrationTest;
import com.ecacho.bddforrestapi.model.BasicResponse;
import com.ecacho.bddforrestapi.parsers.DataInputReader;
import com.ecacho.bddforrestapi.parsers.DataInputReaderFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Ignore;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Ignore
public class BasicStep  extends SpringIntegrationTest {

    private List<Map<String, Object>> dataForTest;
    private List<BasicResponse> dataResponse;
    private DataInputReaderFactory readerFactory = DataInputReaderFactory.getInstance();
    private OkHttpClient httpClient  = new OkHttpClient();
    private String endpoint;
    private String method;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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
        this.dataResponse = new LinkedList<>();
        this.endpoint = endpoint;
        this.method = method;
    }

    @And("^enviando los datos \"(.*)\"$")
    public void sendingDataToEndpoint(String cols) throws IOException {
        log.info(String.format("sending data: %s", cols));

        //TODO: specify method
        Request request = new Request.Builder()
                .url(getRelativePath(this.endpoint) + "?idClient=1")
                .build();

        //TODO: change for every item in 'dataForTest'
        try (Response response = this.httpClient.newCall(request).execute()) {
            BasicResponse rs = new BasicResponse();

            rs.setStatus(response.code());
            rs.setRow(2);
            rs.setBodyRaw(response.body().string());
            this.dataResponse.add(rs);
        }
    }

    @Then("^el codigo http del endpoint es \"(.*)\"$")
    public void verifyHttpStatus(int statusCode){
        log.info(String.format("verify status code: %d", statusCode));
        for(BasicResponse item: this.dataResponse){
            String msg = String.format("Status code doesn't equals: {%d} <> {%d} at row {%d}", item.getStatus(), statusCode, item.getRow());
            Assert.assertEquals(msg, statusCode, item.getStatus());
        }
    }

    @And("^el codigo de la respuesta del endpoint es \"(.*)\"$")
    public void verifyResponseCode(int responseCode){
        log.info(String.format("verify response code: %d", responseCode));
    }
}
