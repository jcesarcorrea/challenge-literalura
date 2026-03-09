package com.aluracursos.challenge_literalura.dto;

import com.aluracursos.challenge_literalura.model.Autor;
import com.aluracursos.challenge_literalura.model.DatosAutor;
import com.aluracursos.challenge_literalura.model.Libro;

import java.util.List;

public record LibroDTO(
        Long id,
        String title,
        AutorDTO autor,
        List <String> idiomas,
        Integer download_count
) {
    public LibroDTO(Libro libro) {
        this(
                libro.getId(),
                libro.getTitulo(),
                new AutorDTO(libro.getAutor()),
                libro.getIdiomas(),
                libro.getNumeroDescargas()
        );
    }
}
