package com.aluracursos.challenge_literalura.dto;

import com.aluracursos.challenge_literalura.model.Autor;

public record AutorDTO(
        String nombre,
        Integer fechaNacimiento,
        Integer fechaMuerte
) {
    public AutorDTO(Autor autor) {
        this(autor.getNombre(), autor.getFechaNacimiento(), autor.getFechaMuerte());
    }
}
