package org.techenriqueluna;

import io.quarkus.test.junit.QuarkusTest;
import io.rest-assured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PessoaResourceTest {

    @Test
    public void testAdicionarEListar() {
        String body = "{\"nome\":\"Ana\",\"departamento\":\"TI\"}";
        RestAssured.given().contentType("application/json").body(body)
            .when().post("/pessoas").then().statusCode(201)
            .body("id", Matchers.notNullValue());

        RestAssured.get("/pessoas").then().statusCode(200)
            .body("size()", Matchers.greaterThanOrEqualTo(1));
    }
}