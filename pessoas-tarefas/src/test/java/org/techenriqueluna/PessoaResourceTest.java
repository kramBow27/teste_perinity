package org.techenriqueluna;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PessoaResourceTest {

    @Test
    public void testAdicionarEListar() {
        // POST /pessoas deve retornar 201 com id não-nulo
        String body = "{\"nome\":\"Ana\",\"departamento\":\"TI\"}";
        RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/pessoas")
                .then()
                .statusCode(201)
                .body("id", Matchers.notNullValue());

        // GET /pessoas deve retornar lista com pelo menos 1 elemento
        RestAssured.given()
                .when()
                .get("/pessoas")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1));
    }
}
