package noah.bompard.coopcycle.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import noah.bompard.coopcycle.IntegrationTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@IntegrationTest
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
