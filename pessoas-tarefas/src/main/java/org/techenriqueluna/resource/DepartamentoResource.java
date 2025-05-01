package org.techenriqueluna.resource;

import org.techenriqueluna.dto.DepartamentoResumoDTO;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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