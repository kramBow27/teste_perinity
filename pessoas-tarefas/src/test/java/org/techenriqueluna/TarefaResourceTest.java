// src/test/java/org/techenriqueluna/TarefaResourceTest.java
package org.techenriqueluna;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

@QuarkusTest
public class TarefaResourceTest {

    private Long pessoaId;

    @BeforeEach
    public void seedPessoa() {
        var body = """
            {"nome":"DepTest","departamento":"DevOps"}
            """;
        pessoaId = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/pessoas")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test
    @Order(1)
    public void testCreateAndPendentes() {
        for (int i = 1; i <= 2; i++) {
            var t = String.format("""
                {"titulo":"T%d","descricao":"desc","prazo":"2025-06-0%d","departamento":"DevOps","duracao":%d}
                """, i, i, i * 2);
            given()
                    .contentType(ContentType.JSON)
                    .body(t)
                    .when()
                    .post("/tarefas")
                    .then()
                    .statusCode(201)
                    .body("id", Matchers.notNullValue());
        }

        when()
                .get("/tarefas/pendentes")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(2));
    }

    @Test
    @Order(2)
    public void testAlocarAndFinalizeAndPendentesUpdate() {
        var tarefa = """
            {"titulo":"Final Test","descricao":"d","prazo":"2025-07-01","departamento":"DevOps","duracao":5}
            """;
        Long tarefaId = given()
                .contentType(ContentType.JSON)
                .body(tarefa)
                .when()
                .post("/tarefas")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        given()
                .when()
                .put("/tarefas/alocar/{id}?pessoa={p}", tarefaId, pessoaId)
                .then()
                .statusCode(200)
                .body("pessoa.id", Matchers.equalTo(pessoaId.intValue()));

        given()
                .when()
                .put("/tarefas/finalizar/{id}", tarefaId)
                .then()
                .statusCode(200)
                .body("finalizado", Matchers.equalTo(true));

        // usa concatenação em vez de placeholder
        when()
                .get("/tarefas/pendentes")
                .then()
                .statusCode(200)
                .body("find { it.id == " + tarefaId.intValue() + " }", Matchers.nullValue());
    }
}
