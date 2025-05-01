package org.techenriqueluna.repository;

import org.techenriqueluna.entity.Pessoa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PessoaRepository implements PanacheRepository<Pessoa> { }