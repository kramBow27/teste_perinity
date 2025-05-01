package org.techenriqueluna.repository;

import org.techenriqueluna.entity.Tarefa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TarefaRepository implements PanacheRepository<Tarefa> {
    public List<Tarefa> pendentesMaisAntigas(int limite) {
        return find("pessoa is null and finalizado = false order by prazo asc").page(0, limite).list();
    }
}