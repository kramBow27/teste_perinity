// src/main/java/org/techenriqueluna/service/TarefaService.java
package org.techenriqueluna.service;

import org.techenriqueluna.dto.TarefaDTO;
import org.techenriqueluna.entity.Pessoa;
import org.techenriqueluna.entity.Tarefa;
import org.techenriqueluna.repository.PessoaRepository;
import org.techenriqueluna.repository.TarefaRepository;
import io.quarkus.panache.common.Page;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TarefaService {

    @Inject
    TarefaRepository repo;

    @Inject
    PessoaRepository pessoaRepo;

    @Transactional
    public Tarefa add(TarefaDTO dto) {
        Tarefa t = new Tarefa();
        t.setTitulo(dto.getTitulo());
        t.setDescricao(dto.getDescricao());
        t.setPrazo(dto.getPrazo());
        t.setDepartamento(dto.getDepartamento());
        t.setDuracao(dto.getDuracao());
        // opcionalmente já aloca se vier id de pessoa no DTO
        if (dto.getPessoaId() != null) {
            Pessoa p = pessoaRepo.findById(dto.getPessoaId());
            if (p != null && p.getDepartamento().equals(dto.getDepartamento())) {
                t.setPessoa(p);
            }
        }
        repo.persist(t);
        return t;
    }

    @Transactional
    public Tarefa alocar(Long tarefaId, Long pessoaId) {
        Tarefa t = repo.findById(tarefaId);
        Pessoa p = pessoaRepo.findById(pessoaId);
        if (t == null || p == null) {
            return null;
        }
        if (!t.getDepartamento().equals(p.getDepartamento())) {
            return null;
        }
        t.setPessoa(p);
        return t;
    }

    @Transactional
    public Tarefa finalizar(Long tarefaId) {
        Tarefa t = repo.findById(tarefaId);
        if (t == null) {
            return null;
        }
        t.setFinalizado(true);
        return t;
    }

    /** Apenas as 3 tarefas pendentes mais antigas */
    public List<Tarefa> pendentesMaisAntigas(int maximo) {
        return repo.pendentesMaisAntigas(maximo);
    }

    /** Todas as tarefas pendentes (sem limite) */
    public List<Tarefa> todasPendentes() {
        return repo.todasPendentes();
    }
}
