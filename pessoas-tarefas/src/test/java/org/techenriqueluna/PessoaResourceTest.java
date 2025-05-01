// src/test/java/org/techenriqueluna/PessoaResourceTest.java
package org.techenriqueluna;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class PessoaResourceTest {

    @Test
    @Order(1)
    public void testAddAndList() {
        var body = """
            {"nome":"Ana","departamento":"TI"}
            """;
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/pessoas")
                .then()
                .statusCode(201)
                .body("id", Matchers.notNullValue());

        given()
                .when()
                .get("/pessoas")
                .then()
                .statusCode(200)
                .body("find { it.nome == 'Ana' }", Matchers.notNullValue());
    }

    @Test
    @Order(2)
    public void testUpdateAndDelete() {
        var body = """
            {"nome":"Bruno","departamento":"RH"}
            """;
        Long id = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/pessoas")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        var upd = """
            {"nome":"Bruno","departamento":"Marketing"}
            """;
        given()
                .contentType(ContentType.JSON)
                .body(upd)
                .when()
                .put("/pessoas/{id}", id)
                .then()
                .statusCode(200)
                .body("departamento", Matchers.is("Marketing"));

        given()
                .when()
                .delete("/pessoas/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/pessoas")
                .then()
                .statusCode(200)
                .body("find { it.id == " + id.intValue() + " }", Matchers.nullValue());
    }

    @Test
    @Order(3)
    public void testGastosWithoutTasks() {
        var body = """
            {"nome":"Clara","departamento":"Financeiro"}
            """;
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/pessoas")
                .then()
                .statusCode(201);

        given()
                .queryParam("nome", "Clara")
                .when()
                .get("/pessoas/gastos")
                .then()
                .statusCode(200)
                .body("nome", Matchers.is("Clara"))
                .body("mediaHorasPorTarefa", Matchers.is(0.0f));
    }
}
