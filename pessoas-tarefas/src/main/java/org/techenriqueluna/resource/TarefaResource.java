// src/main/java/org/techenriqueluna/resource/TarefaResource.java
package org.techenriqueluna.resource;

import org.techenriqueluna.dto.TarefaDTO;
import org.techenriqueluna.entity.Tarefa;
import org.techenriqueluna.service.TarefaService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/tarefas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional // garante que a sessão do Hibernate permaneça até a serialização do response
public class TarefaResource {

    @Inject
    TarefaService service;

    @POST
    public Response add(TarefaDTO dto) {
        Tarefa t = service.add(dto);
        return Response.status(Response.Status.CREATED)
                .entity(t)
                .build();
    }

    @PUT
    @Path("/alocar/{id}")
    public Response alocar(
            @PathParam("id") Long id,
            @QueryParam("pessoa") Long idPessoa) {
        Tarefa t = service.alocar(id, idPessoa);
        return Response.ok(t).build();
    }

    @PUT
    @Path("/finalizar/{id}")
    public Response finalizar(@PathParam("id") Long id) {
        Tarefa t = service.finalizar(id);
        return Response.ok(t).build();
    }

    @GET
    @Path("/pendentes")
    public List<Tarefa> pendentes() {
        return service.pendentes();
    }
}
