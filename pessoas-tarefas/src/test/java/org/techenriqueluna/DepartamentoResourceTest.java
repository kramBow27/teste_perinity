// src/test/java/org/techenriqueluna/DepartamentoResourceTest.java
package org.techenriqueluna;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

@QuarkusTest
public class DepartamentoResourceTest {

    @Test
    public void testListagemResumoDepartamentos() {
        Long p1 = given()
                .contentType(ContentType.JSON)
                .body("{\"nome\":\"A1\",\"departamento\":\"Dept1\"}")
                .when().post("/pessoas")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");

        Long t1 = given()
                .contentType(ContentType.JSON)
                .body("{\"titulo\":\"T1\",\"descricao\":\"x\",\"prazo\":\"2025-08-01\",\"departamento\":\"Dept1\",\"duracao\":3}")
                .when().post("/tarefas")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");

        given()
                .when().put("/tarefas/alocar/{id}?pessoa={p}", t1, p1)
                .then().statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .body("{\"nome\":\"B1\",\"departamento\":\"Dept1\"}")
                .when().post("/pessoas")
                .then().statusCode(201);

        Long p2 = given()
                .contentType(ContentType.JSON)
                .body("{\"nome\":\"C2\",\"departamento\":\"Dept2\"}")
                .when().post("/pessoas")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");

        for (int i = 1; i <= 2; i++) {
            Long t = given()
                    .contentType(ContentType.JSON)
                    .body(String.format(
                            "{\"titulo\":\"TD2-%d\",\"descricao\":\"x\",\"prazo\":\"2025-09-0%d\",\"departamento\":\"Dept2\",\"duracao\":%d}",
                            i, i, i))
                    .when().post("/tarefas")
                    .then().statusCode(201)
                    .extract().jsonPath().getLong("id");

            given()
                    .when().put("/tarefas/alocar/{id}?pessoa={p}", t, p2)
                    .then().statusCode(200);
        }

        when()
                .get("/departamentos")
                .then()
                .statusCode(200)
                .body("find { it.departamento=='Dept1' }.totalPessoas", Matchers.equalTo(2))
                .body("find { it.departamento=='Dept1' }.totalTarefas", Matchers.equalTo(1))
                .body("find { it.departamento=='Dept2' }.totalPessoas", Matchers.equalTo(1))
                .body("find { it.departamento=='Dept2' }.totalTarefas", Matchers.equalTo(2));
    }
}
