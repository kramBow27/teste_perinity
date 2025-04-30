package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.rest-assured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TarefaResourceTest {

    private Long pessoaId;

    @BeforeEach
    public void setupPessoa() {
        String body = "{\"nome\":\"Bob\",\"departamento\":\"TI\"}";
        pessoaId = RestAssured.given().contentType("application/json").body(body)
                .post("/pessoas").then().extract().path("id");
    }

    @Test
    public void testAlocarEFinalizar() {
        Long tarefaId = RestAssured.given().contentType("application/json")
            .body("{\"titulo\":\"Feat A\",\"descricao\":\"desc\",\"prazo\":\"2025-05-01\",\"departamento\":\"TI\",\"duracao\":4}")
            .post("/tarefas").then().extract().path("id");

        RestAssured.put("/tarefas/alocar/" + tarefaId + "?pessoa=" + pessoaId)
            .then().statusCode(200).body("pessoa.id", Matchers.equalTo(pessoaId.intValue()));

        RestAssured.put("/tarefas/finalizar/" + tarefaId)
            .then().statusCode(200).body("finalizado", Matchers.equalTo(true));
    }
}