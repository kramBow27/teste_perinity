package org.techenriqueluna.resource;

import org.techenriqueluna.dto.DepartamentoResumoDTO;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/departamentos")
@Produces(MediaType.APPLICATION_JSON)
public class DepartamentoResource {
    @Inject
    EntityManager em;

    @GET
    public List<DepartamentoResumoDTO> list() {
        return em.createQuery("select new org.techenriqueluna.dto.DepartamentoResumoDTO(p.departamento, count(distinct p.id), count(t.id)) " +
                              "from Pessoa p left join p.tarefas t group by p.departamento", DepartamentoResumoDTO.class)
                 .getResultList();
    }
}