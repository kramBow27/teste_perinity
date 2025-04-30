package com.example.service;

import com.example.dto.*;
import com.example.model.Pessoa;
import com.example.repository.PessoaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PessoaService {

    @Inject
    PessoaRepository repo;

    @Transactional
    public Pessoa add(PessoaDTO dto) {
        Pessoa p = new Pessoa();
        p.setNome(dto.getNome());
        p.setDepartamento(dto.getDepartamento());
        repo.persist(p);
        return p;
    }

    @Transactional
    public Pessoa update(Long id, PessoaDTO dto) {
        Pessoa p = repo.findById(id);
        if (p == null) return null;
        p.setNome(dto.getNome());
        p.setDepartamento(dto.getDepartamento());
        return p;
    }

    @Transactional
    public boolean delete(Long id) {
        return repo.deleteById(id);
    }

    public List<PessoaHorasDTO> listarComHoras() {
        return repo.findAll().stream().map(p -> {
            long total = p.getTarefas() == null ? 0 : p.getTarefas().stream().mapToLong(t -> t.getDuracao() == null ? 0 : t.getDuracao()).sum();
            return new PessoaHorasDTO(p.getNome(), p.getDepartamento(), total);
        }).collect(Collectors.toList());
    }

    public MediaGastosDTO mediaHoras(GastoFiltroDTO filtro) {
        List<Pessoa> pessoas = repo.find("nome like ?1", "%" + filtro.getNome() + "%").list();
        if (pessoas.isEmpty()) return new MediaGastosDTO(filtro.getNome(), 0);
        LocalDate inicio = filtro.getInicio();
        LocalDate fim = filtro.getFim();
        long soma = 0;
        long totalTarefas = 0;
        for (Pessoa p : pessoas) {
            if (p.getTarefas() == null) continue;
            for (var t : p.getTarefas()) {
                if (!t.isFinalizado()) continue;
                if (inicio != null && t.getPrazo().isBefore(inicio)) continue;
                if (fim != null && t.getPrazo().isAfter(fim)) continue;
                soma += t.getDuracao();
                totalTarefas++;
            }
        }
        double media = totalTarefas == 0 ? 0 : ((double) soma) / totalTarefas;
        return new MediaGastosDTO(filtro.getNome(), media);
    }
}