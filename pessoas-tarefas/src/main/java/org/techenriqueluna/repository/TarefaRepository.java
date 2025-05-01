package org.techenriqueluna.repository;

import org.techenriqueluna.entity.Tarefa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TarefaRepository implements PanacheRepository<Tarefa> {
    public List<Tarefa> pendentesMaisAntigas(int limit) {
        return find("pessoa is null order by prazo asc")
                .page(Page.of(0, limit))
                .list();
    }

    /**
     * Retorna todas as tarefas sem pessoa alocada,
     * ordenadas por prazo ascendente.
     */
    public List<Tarefa> todasPendentes() {
        return find("pessoa is null order by prazo asc")
                .list();
    }
}