package org.techenriqueluna;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TarefaResourceTest {

    private Long pessoaId;

    @BeforeEach
    public void setupPessoa() {
        // cria uma pessoa e garante status 201, extrai o id como Long
        String body = "{\"nome\":\"Bob\",\"departamento\":\"TI\"}";
        pessoaId = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/pessoas")
                .then()
                .statusCode(201)
                .body("id", Matchers.notNullValue())
                .extract()
                .jsonPath()
                .getLong("id");
    }

    @Test
    public void testAlocarEFinalizar() {
        // cria a tarefa e extrai o id como Long
        String tarefaBody = "{\"titulo\":\"Feat A\",\"descricao\":\"desc\",\"prazo\":\"2025-05-01\",\"departamento\":\"TI\",\"duracao\":4}";
        Long tarefaId = RestAssured.given()
                .contentType("application/json")
                .body(tarefaBody)
                .when()
                .post("/tarefas")
                .then()
                .statusCode(201)
                .body("id", Matchers.notNullValue())
                .extract()
                .jsonPath()
                .getLong("id");

        // aloca no endpoint e verifica que veio o mesmo pessoaId (como int no JSON)
        RestAssured.given()
                .when()
                .put("/tarefas/alocar/{tarefaId}?pessoa={pessoaId}", tarefaId, pessoaId)
                .then()
                .statusCode(200)
                .body("pessoa.id", Matchers.equalTo(pessoaId.intValue()));

        // finaliza e verifica finalizado == true
        RestAssured.given()
                .when()
                .put("/tarefas/finalizar/{tarefaId}", tarefaId)
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("finalizado", Matchers.equalTo(true));
    }
}
