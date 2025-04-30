package org.techenriqueluna.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class TarefaDTO {
    public Long id;            
    public String titulo;
    public String descricao;
    public LocalDate prazo;
    public String departamento;
    public Integer duracao;
    public Long pessoaId;      
    public Boolean finalizado; 
}