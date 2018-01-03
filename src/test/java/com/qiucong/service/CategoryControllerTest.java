package com.qiucong.service;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.lenovo.constants.SystemConstant;
import com.lenovo.model.Country;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Created by xueqi2 on 2016/12/13.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("devTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/initCategoryData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/initLFOsAndCTOsForGivenCategory.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/dropLFOsForGivenCategory.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "/dropCategoryData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

public class CategoryControllerTest {
    protected MockMvc mvc;
    @Autowired
    Environment env;
    @Value("${local.server.port}")
    int port;
    @Autowired
    private WebApplicationContext webApplicationContext;


    private List<Integer> priceTypeIds;

    @Before
    public void setUp() {
        String scheme = StringUtils.isNotBlank(env.getProperty("server.ssl.key-store")) ? "https" : "http";
        RestAssured.baseURI = scheme + "://localhost";
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.port = port;
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        priceTypeIds = new ArrayList<Integer>();
        priceTypeIds.add(1);
    }

    @Test
    public void should_have_options_category_when_category_is_servers() {
        mockCountryWithUS();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .param("country", "US")
                .param("language", "fr")
                .when()
                .get("categories/category_detail?categoryId=STG@Servers")
                .then()
                .statusCode(200)
                .body("language", is("fr"))
                .body("children.size()", is(1))
                .body("children.get(0).categoryId", is("STG@Servers@Options"));
    }

    @Test
    public void should_have_options_category_when_category_is_a_invalid_category() {
        mockCountryWithUS();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .when()
                .get("categories/category_detail?categoryId=STG@invalidServers&country=US&language=en")
                .then()
                .statusCode(200)
                .content(is(""));
    }

    @Test
    public void should_have_products_when_category_is_a_valid_category() {
        mockCountryWithUS();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .when()
                .get("categories/category_detail?categoryId=STGServerstest&country=US&language=en&mode=" + SystemConstant.IA_DATE_MODE)
                .then()
                .body("language", is("en"))
                .statusCode(200);
    }

    @Test
    public void should_return_homepage_category_without_announcedate_and_withdrawndate() {
        mockCountryWithAE();

        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .when()
                .get("categories/leafCategories?country=AE")
                .then()
                .statusCode(200)
                .body("STGRackandTowerServersSystemx3650M5", hasSize(2))
                .body("STGServersRackAndTowar", hasSize(2))
                .body("STGRackandTowerServersSystemx3650M5[0].code", is("39150001US"))
                .body("STGRackandTowerServersSystemx3650M5[0].announceDate", is("2014-10-30"))
                .body("STGRackandTowerServersSystemx3650M5[0].withdrawnDate", is("2999-12-31"))
                .body("STGRackandTowerServersSystemx3650M5[0].type", is("LFO"))
                .body("STGRackandTowerServersSystemx3650M5[1].code", is("39150003US"))
                .body("STGRackandTowerServersSystemx3650M5[1].announceDate", is("2014-10-30"))
                .body("STGRackandTowerServersSystemx3650M5[1].withdrawnDate", is("2999-12-31"))
                .body("STGRackandTowerServersSystemx3650M5[1].type", is("LFO"))
                .body("STGServersRackAndTowar[1].code", is("9956RC5"))
                .body("STGServersRackAndTowar[1].announceDate", is("2013-04-30"))
                .body("STGServersRackAndTowar[1].withdrawnDate", is("2999-12-31"))
                .body("STGServersRackAndTowar[1].type", is("CTO"))
                .body("STGServersRackAndTowar[0].code", is("9956RC6"))
                .body("STGServersRackAndTowar[0].announceDate", is("2013-04-30"))
                .body("STGServersRackAndTowar[0].withdrawnDate", is("2999-12-31"))
                .body("STGServersRackAndTowar[0].type", is("CTO"));
    }

    @Test
    public void should_return_purley_leaf_products() {
        mockCountryWithUS();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .when()
                .get("categories/leafCategories?country=US&isPurley=true&language=fr")
                .then()
                .statusCode(200)
                .body("STGServersOptionsThinkSystemSR550", hasSize(2))
                .body("STGServersOptionsThinkSystemSR550[0].code", is("7X02CTO1WW"))
                .body("STGServersOptionsThinkSystemSR550[0].description", is("Lenovo System x3650 M5 fr"))
                .body("STGServersOptionsThinkSystemSR550[1].code", is("7X01CTO1WW"))
                .body("STGServersOptionsThinkSystemSR550[1].description", is("Lenovo System x3650 M5 en"));
    }

    @Test
    public void should_return_homepage_category_without_announcedate_and_withdrawndate_when_mode_is_ia_date() {
        mockCountryWithUS();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .when()
                .get("categories/leafCategories?country=US&announceDate=3099-01-01&mode=" + SystemConstant.IA_DATE_MODE)
                .then()
                .statusCode(200)
                .body("STGServerstest", hasSize(2))
                .body("STGServerstest[0].code", is("9956RC7"))
                .body("STGServerstest[0].type", is("CTO"))
                .body("STGServerstest[1].code", is("9956RC9"))
                .body("STGServerstest[1].type", is("LFO"));

    }

    @Test
    public void should_return_homepage_category_with_announcedate_and_withdrawndate() {
        mockCountryWithAE();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .when()
                .get("categories/leafCategories?country=AE&announceDate=2016-12-14&withdrawnDate=2016-12-14")
                .then()
                .statusCode(200)
                .body("STGRackandTowerServersSystemx3650M5", hasSize(2))
                .body("STGServersRackAndTowar", hasSize(2))
                .body("STGRackandTowerServersSystemx3650M5[0].code", is("39150001US"))
                .body("STGRackandTowerServersSystemx3650M5[0].announceDate", is("2014-10-30"))
                .body("STGRackandTowerServersSystemx3650M5[0].withdrawnDate", is("2999-12-31"))
                .body("STGRackandTowerServersSystemx3650M5[0].type", is("LFO"))
                .body("STGRackandTowerServersSystemx3650M5[0].description", is("Lenovo System 39150001US (mock lfo)"))
                .body("STGRackandTowerServersSystemx3650M5[1].code", is("39150003US"))
                .body("STGRackandTowerServersSystemx3650M5[1].announceDate", is("2014-10-30"))
                .body("STGRackandTowerServersSystemx3650M5[1].withdrawnDate", is("2999-12-31"))
                .body("STGRackandTowerServersSystemx3650M5[1].type", is("LFO"))
                .body("STGRackandTowerServersSystemx3650M5[1].description", is("Lenovo System 39150003US (mock lfo)"))
                .body("STGServersRackAndTowar[1].code", is("9956RC5"))
                .body("STGServersRackAndTowar[1].announceDate", is("2013-04-30"))
                .body("STGServersRackAndTowar[1].withdrawnDate", is("2999-12-31"))
                .body("STGServersRackAndTowar[1].type", is("CTO"))
                .body("STGServersRackAndTowar[0].code", is("9956RC6"))
                .body("STGServersRackAndTowar[0].announceDate", is("2013-04-30"))
                .body("STGServersRackAndTowar[0].withdrawnDate", is("2999-12-31"))
                .body("STGServersRackAndTowar[0].type", is("CTO"));
    }

    @Test
    public void should_return_lfos_and_ctos_for_given_country_and_category() {
        mockCountryWithUS();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .param("priceTypeIds", priceTypeIds)
                .when()
                .get("categories/lfos?categoryId=STG@Rack and Tower Servers@System x3650 M5&country=US&language=en&currency=USD&announceDate=2016-12-15&withdrawnDate=2016-12-15")
                .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("[0].lfoCode", is("7X140001US"))
                .body("[0].topSeller", is(false))
                .body("[0].cto.ctoCode", is("7X14CTO1WW"))
                .body("[0].cto.description", is(""))
                .body("[0].lfoAttributeList[0].value", is("Intel Xeon Processor E3-1220 4C/ <STRONG>3.1GHz</STRONG>"))
                .body("[0].lfoAttributeList[0].description", is("Processor/Speed"))
                .body("[0].lfoAttributeList[0].name", is("VAMPROCSPEED"))
                .body("[0].lfoAttributeList[1].value", is("<STRONG>4(4) x6(5)</STRONG>"))
                .body("[0].lfoAttributeList[1].description", is("Slots x Bays Total (Avail)"))
                .body("[0].lfoAttributeList[1].name", is("VAMSLOTBAY"))
                .body("[1].lfoCode", is("39150002US"))
                .body("[1].cto.ctoCode", is("3915CTO1WW"))
                .body("[1].lfoAttributeList[0].value", is("<STRONG>12TB"))
                .body("[1].lfoAttributeList[0].description", is("Internal Hard Disk (Max)"))
                .body("[1].lfoAttributeList[0].name", is("VAMINTHDMAX"));
    }

    @Test
    public void should_return_lfos_and_ctos_for_given_country_and_category_in_iad_mode() {
        mockCountryWithUS();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .param("priceTypeIds", priceTypeIds)
                .when()
                .get("categories/lfos?categoryId=STG@Rack and Tower Servers@System x3650 M5&country=US&language=en&currency=USD&announceDate=2016-12-15&withdrawnDate=2016-12-15&mode=" + SystemConstant.IA_DATE_MODE)
                .then()
                .statusCode(200)
                .body("content", hasSize(3))
                .body("[0].lfoCode", is("7X140002US"))
                .body("[0].iad", is(true));
    }

    @Test
    public void should_return_translated_cto_description() {
        mockCountryWithUS();
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .contentType("application/json")
                .param("priceTypeIds", priceTypeIds)
                .when()
                .get("categories/lfos?categoryId=STG@Rack and Tower Servers@System x3650 M5&country=US&language=fr&currency=EUR&announceDate=2016-12-15&withdrawnDate=2016-12-15")
                .then()
                .statusCode(200)
                .body("content", hasSize(2))
                .body("[1].lfoCode", is("39150002US"))
                .body("[1].topSeller", is(false))
                .body("[1].cto.ctoCode", is("3915CTO1WW"))
                .body("[1].cto.description", is(""))
                .body("[0].lfoCode", is("7X140001US"))
                .body("[0].cto.ctoCode", is("7X14CTO1WW"))
                .body("[0].cto.description", is(""));
    }

    @Test
    public void should_return_cto_list_when_category_and_country_is_exist() {
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .when()
                .get("categories/ctos?categoryId=STG@Rack and Tower Servers@System x3650 M5&country=US&language=fr&announceDate=2016-12-15&withdrawnDate=2016-12-15")
                .then()
                .body("content.size()", is(1))
                .body("[0].ctoCode", is("3915CTO1WW"))
                .body("[0].description", is("Lenovo System x3650 M5 fr"))
                .body("[0].marketInfo", is("3915CTO1WW market info fr"));
    }

    @Test
    public void should_return_cto_list_when_category_and_country_is_exist_and_mode_is_iad() {
        given()
                .header("Accept", ContentType.JSON.withCharset("UTF-8"))
                .header("Content-Type", ContentType.JSON.withCharset("UTF-8"))
                .when()
                .get("categories/ctos?categoryId=STG@Rack and Tower Servers@System x3650 M5&country=US&language=en&announceDate=2016-12-15&withdrawnDate=2016-12-15&mode=" + SystemConstant.IA_DATE_MODE)
                .then()
                .body("content.size()", is(2))
                .body("[0].ctoCode", is("3915CTO1WW"))
                .body("[0].iad", is(false))
                .body("[1].ctoCode", is("7X14CTO2WW"))
                .body("[1].iad", is(true));
    }

    private void mockCountryWithUS() {
        Country country = new Country();
        country.setCurrencyCode("USD");
        Mockito.when(countryRepository.findOne("US"))
                .thenReturn(country);
    }

    private void mockCountryWithAE() {
        Country country = new Country();
        country.setCurrencyCode("USD");
        Mockito.when(countryRepository.findOne("AE"))
                .thenReturn(country);
    }
}
