package org.techenriqueluna.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class GastoFiltroDTO {
    private String nome;
    private LocalDate inicio;
    private LocalDate fim;
}