package com.aluracursos.challenge_literalura.dto;

import com.aluracursos.challenge_literalura.model.DatosLibro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosRespuesta(
        Integer count,
        List<DatosLibro> results
) {
}
