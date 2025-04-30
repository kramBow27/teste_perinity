package com.example.dto;

import lombok.Data;

@Data
public class PessoaDTO {
    public Long id;            // opcional para PUT
    public String nome;
    public String departamento;
}