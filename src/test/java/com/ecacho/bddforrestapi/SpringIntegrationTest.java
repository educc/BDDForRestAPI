package com.ecacho.bddforrestapi;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract  class SpringIntegrationTest {

    @LocalServerPort
    protected int port;

    public String getRelativePath(String path){
        String sep = path.startsWith("/")? "" : "/";
        return "http://localhost:" + port + sep + path;
    }

}