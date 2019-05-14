package com.ecacho.bddforrestapi.steps;

import com.ecacho.bddforrestapi.SpringIntegrationTest;
import com.ecacho.bddforrestapi.model.BasicResponse;
import com.ecacho.bddforrestapi.parsers.DataInputReader;
import com.ecacho.bddforrestapi.parsers.DataInputReaderFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.Ignore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Ignore
public class BasicStep  extends SpringIntegrationTest {

    private List<Map<String, Object>> dataForTest;
    private List<BasicResponse> dataResponse;
    private DataInputReaderFactory readerFactory = DataInputReaderFactory.getInstance();
    private OkHttpClient httpClient  = new OkHttpClient();
    private String endpoint;
    private String method;

    private static final String CODE_PROPERTY = "code";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String[] METHOD_PERMITED = new String[]{"GET","POST","PUT", "PATCH","DELETE"};

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
    public void executeEndpoint(String endpoint, String method) throws Exception {
        log.info(String.format("prepare http client: %s - %s", endpoint, method));

        if(!Arrays.asList(METHOD_PERMITED).contains(method.toUpperCase())){
            throw new Exception("Method doesn't implemented: " + method);
        }

        this.dataResponse = new LinkedList<>();
        this.endpoint = endpoint;
        this.method = method.toUpperCase();
    }

    @And("^enviando los datos \"(.*)\"$")
    public void sendingDataToEndpoint(String cols) throws Exception {
        log.info(String.format("sending data: %s", cols));

        int row = 1;
        for(Map item: this.dataForTest){

            Request request = null;

            if( "GET".equals(this.method)){
                String url = getRelativePath(this.endpoint)
                        + queryParamUri(item, cols);

                log.debug(url);
                request = new Request.Builder()
                        .url(url)
                        .build();
            }else{
                //TODO: for others methods
            }

            if(request == null){
                throw new Exception("Cannot create the reqeuest object");
            }

            try (Response response = this.httpClient.newCall(request).execute()) {
                BasicResponse rs = new BasicResponse();

                rs.setStatus(response.code());
                rs.setRow(row);
                rs.setBodyRaw(response.body().string());

                this.dataResponse.add(rs);
            }
            row++;
        }
    }

    @Then("^el codigo http del endpoint es \"(.*)\"$")
    public void verifyHttpStatus(int statusCode){
        log.info(String.format("verify status code: %d", statusCode));
        for(BasicResponse item: this.dataResponse){
            String msg = String.format("Status code doesn't equals: expected {%d} but was{%d} at row {%d}",
                    item.getStatus(), statusCode, item.getRow());
            Assert.assertEquals(msg, statusCode, item.getStatus());
        }
    }

    @And("^el codigo de la respuesta del endpoint es \"(.*)\"$")
    public void verifyResponseCode(int responseCode){
        log.info(String.format("verify response code: %d", responseCode));


        int row = 1;
        ObjectMapper mapper = new ObjectMapper();
        for(BasicResponse item: this.dataResponse){
            JsonNode responseObj = null;
            boolean parseOk = true;
            try {
                responseObj = mapper.readTree(item.getBodyRaw());
            } catch (IOException e) {
                e.printStackTrace();
                parseOk = false;
                log.error("At parse response content to json, at row " + row);
            }

            Assert.assertTrue("Json invalid at row " + row, parseOk);
            Assert.assertTrue(String.format("Doesn't have '%s' property in response, at row %d", CODE_PROPERTY,row)
                    ,responseObj.has(CODE_PROPERTY));

            int code = responseObj.get(CODE_PROPERTY).asInt();
            Assert.assertTrue(String.format("Invalid code. Expected {%d} but was {%d}",
                    responseCode, code), code == responseCode);
            row++;
        }
    }

    @And("la respuesta cumple la especificacion \"(.*)\"$")
    public void validateJsonSchema(String fileJsonSchema) throws Exception {
        log.info(String.format("validate JsonSchema: %s", fileJsonSchema));

        Path pathJsonSchema = Paths.get(fileJsonSchema);
        if(!Files.exists(pathJsonSchema)){
            throw new Exception("Schema File not found: " + fileJsonSchema);
        }

        String strJsonSchema = new String(Files.readAllBytes(pathJsonSchema));

        int row = 1;
        for(BasicResponse item: this.dataResponse) {
            boolean schemaValidationOk = true;
            String detailError = "";
            try{
                JSONObject rawSchema = new JSONObject(strJsonSchema);
                Schema schema = SchemaLoader.load(rawSchema);
                schema.validate(new JSONObject(item.getBodyRaw()));
            }catch (ValidationException ex){
                schemaValidationOk = false;
                detailError = String.join("\n", ex.getAllMessages());
            }catch (Exception ex){
                schemaValidationOk = false;
                detailError =ex.getMessage();
            }
            Assert.assertTrue("Schema validation fail, at row " + row + ".\n" + detailError,
                    schemaValidationOk);
            row++;
        }
    }

    private String queryParamUri(Map<String, Object> item, String columns) throws Exception {
        StringBuilder sb = new StringBuilder("?");
        for(String col: columns.split(",")){
            col = col.trim();
            Object value = item.get(col);
            if(value == null){
                String msg = String.format("The attribute {%s} doesn't exists in dataset for test", col);
                throw new Exception(msg);
            }
            sb.append(col);
            sb.append("=");
            sb.append(value.toString());
        }
        return sb.toString();
    }
}
