package com.example.resource;

import com.example.dto.TarefaDTO;
import com.example.model.Tarefa;
import com.example.service.TarefaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/tarefas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TarefaResource {
    @Inject
    TarefaService service;

    @POST
    public Response add(TarefaDTO dto) {
        Tarefa t = service.add(dto);
        return Response.status(Response.Status.CREATED).entity(t).build();
    }

    // PUT /tarefas/alocar/{id}?pessoa=123
    @PUT
    @Path("/alocar/{id}")
    public Tarefa alocar(@PathParam("id") Long id,
                         @QueryParam("pessoa") Long idPessoa) {
        return service.alocar(id, idPessoa);
    }

    // PUT /tarefas/finalizar/{id}
    @PUT
    @Path("/finalizar/{id}")
    public Tarefa finalizar(@PathParam("id") Long id) {
        return service.finalizar(id);
    }

    // GET /tarefas/pendentes
    @GET
    @Path("/pendentes")
    public List<Tarefa> pendentes() {
        return service.pendentes();
    }
}