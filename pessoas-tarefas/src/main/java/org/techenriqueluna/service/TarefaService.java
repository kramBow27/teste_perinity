package org.techenriqueluna.service;

import org.techenriqueluna.dto.TarefaDTO;
import org.techenriqueluna.entity.Pessoa;
import org.techenriqueluna.entity.Tarefa;
import org.techenriqueluna.repository.PessoaRepository;
import org.techenriqueluna.repository.TarefaRepository;
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
        if (dto.getPessoaId() != null) {
            Pessoa p = pessoaRepo.findById(dto.getPessoaId());
            if (p != null && p.getDepartamento().equals(dto.getDepartamento())) t.setPessoa(p);
        }
        repo.persist(t);
        return t;
    }

    @Transactional
    public Tarefa alocar(Long idTarefa, Long idPessoa) {
        Tarefa tarefa = repo.findById(idTarefa);
        Pessoa pessoa = pessoaRepo.findById(idPessoa);
        if (tarefa == null || pessoa == null) return null;
        if (!tarefa.getDepartamento().equals(pessoa.getDepartamento())) return null;
        tarefa.setPessoa(pessoa);
        return tarefa;
    }

    @Transactional
    public Tarefa finalizar(Long idTarefa) {
        Tarefa tarefa = repo.findById(idTarefa);
        if (tarefa == null) return null;
        tarefa.setFinalizado(true);
        return tarefa;
    }

    public List<Tarefa> pendentes() {
        return repo.pendentesMaisAntigas(3);
    }
}