package org.techenriqueluna.resource;

import org.techenriqueluna.dto.*;
import org.techenriqueluna.entity.Pessoa;
import org.techenriqueluna.service.PessoaService;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/pessoas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaResource {

    @Inject
    PessoaService service;

    @POST
    public Response add(PessoaDTO dto) {
        Pessoa p = service.add(dto);
        return Response.status(Response.Status.CREATED).entity(p).build();
    }

    @PUT
    @Path("/{id}")
    public Pessoa update(@PathParam("id") Long id, PessoaDTO dto) {
        return service.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id) ? Response.noContent().build() : Response.status(404).build();
    }

    // GET /pessoas
    @GET
    public List<PessoaHorasDTO> list() {
        return service.listarComHoras();
    }

    // GET /pessoas/gastos?nome=ana&inicio=2025-01-01&fim=2025-04-30
    @GET
    @Path("/gastos")
    public MediaGastosDTO gastos(@QueryParam("nome") String nome,
                                 @QueryParam("inicio") String inicio,
                                 @QueryParam("fim") String fim) {
        GastoFiltroDTO filtro = new GastoFiltroDTO();
        filtro.setNome(nome);
        filtro.setInicio(inicio == null ? null : java.time.LocalDate.parse(inicio));
        filtro.setFim(fim == null ? null : java.time.LocalDate.parse(fim));
        return service.mediaHoras(filtro);
    }
}