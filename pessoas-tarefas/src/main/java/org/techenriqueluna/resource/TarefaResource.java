package org.techenriqueluna.resource;

import org.techenriqueluna.dto.TarefaDTO;
import org.techenriqueluna.model.Tarefa;
import org.techenriqueluna.service.TarefaService;
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


    @PUT
    @Path("/alocar/{id}")
    public Tarefa alocar(@PathParam("id") Long id,
                         @QueryParam("pessoa") Long idPessoa) {
        return service.alocar(id, idPessoa);
    }

    @PUT
    @Path("/finalizar/{id}")
    public Tarefa finalizar(@PathParam("id") Long id) {
        return service.finalizar(id);
    }

    @GET
    @Path("/pendentes")
    public List<Tarefa> pendentes() {
        return service.pendentes();
    }
}