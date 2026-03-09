package com.aluracursos.challenge_literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToOne
    @JoinColumn(name="autor_id")
    private Autor autor;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="libro_idiomas",joinColumns = @JoinColumn(name="libro_id"))
    @Column(name="idioma")
    private List<String> idiomas = new ArrayList<>();
    private Integer numeroDescargas;

    public Libro() {}

    public Libro(DatosLibro datos) {
        this.titulo = datos.titulo();
        this.idiomas = datos.idiomas();
        this.numeroDescargas = datos.numeroDescargas();
    }

    public List<String> getIdiomas() {
        return idiomas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Integer numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    @Override
    public String toString() {
        return "----- LIBRO -----" +
                "\nTítulo: " + titulo +
                "\nAutor: " + (autor != null ? autor.getNombre() : "Desconocido") +
                "\nIdioma: " + String.join(", ", idiomas) +
                "\nNúmero de descargas: " + numeroDescargas +
                "\n-----------------";
    }
}
