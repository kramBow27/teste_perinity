package org.techenriqueluna.resource;

import org.techenriqueluna.dto.PessoaDTO;
import org.techenriqueluna.dto.PessoaHorasDTO;
import org.techenriqueluna.dto.GastoFiltroDTO;
import org.techenriqueluna.dto.MediaGastosDTO;
import org.techenriqueluna.entity.Pessoa;
import org.techenriqueluna.service.PessoaService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("/pessoas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaResource {

    @Inject
    PessoaService service;

    @POST
    @Transactional
    public Response add(PessoaDTO dto) {
        Pessoa p = service.add(dto);
        return Response.status(Response.Status.CREATED).entity(p).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Pessoa update(@PathParam("id") Long id, PessoaDTO dto) {
        return service.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    public List<PessoaHorasDTO> list() {
        return service.listarComHoras();
    }

    @GET
    @Path("/gastos")
    public MediaGastosDTO gastos(@QueryParam("nome") String nome,
                                 @QueryParam("inicio") String inicio,
                                 @QueryParam("fim") String fim) {
        GastoFiltroDTO filtro = new GastoFiltroDTO();
        filtro.setNome(nome);
        filtro.setInicio(inicio == null ? null : LocalDate.parse(inicio));
        filtro.setFim(fim == null ? null : LocalDate.parse(fim));
        return service.mediaHoras(filtro);
    }
}
