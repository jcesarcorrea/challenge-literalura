package com.aluracursos.challenge_literalura.service;

import com.aluracursos.challenge_literalura.dto.LibroDTO;
import com.aluracursos.challenge_literalura.model.Libro;
import com.aluracursos.challenge_literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibroService {
    @Autowired
    private LibroRepository libroRepository;

    public List<LibroDTO> obtenerLibros() {
        return convierteDatos(libroRepository.findAll());
    }

    public List<LibroDTO> convierteDatos(List<Libro> libros) {
        return libros.stream()
                .map(LibroDTO::new)
                .collect(Collectors.toList());
    }

    public List<LibroDTO> buscarPorIdioma(String idioma) {
        List<Libro> libros = libroRepository.findByIdiomas(idioma);
        return convierteDatos(libros);
    }


}
