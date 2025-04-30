package org.techenriqueluna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class PessoaHorasDTO {
    private String nome;
    private String departamento;
    private long totalHoras;
}