package com.ecacho.bddforrestapi;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


//@RunWith(SpringRunner.class)
@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"pretty"},
		features={"./features/"})
@SpringBootTest
public class BddforrestapiApplicationTests {

	@Test
	public void contextLoads() {
	}

}
