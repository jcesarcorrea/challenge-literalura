package com.aluracursos.challenge_literalura.principal;

import com.aluracursos.challenge_literalura.dto.DatosRespuesta;
import com.aluracursos.challenge_literalura.model.Autor;
import com.aluracursos.challenge_literalura.model.DatosAutor;
import com.aluracursos.challenge_literalura.model.DatosLibro;
import com.aluracursos.challenge_literalura.model.Libro;
import com.aluracursos.challenge_literalura.repository.AutorRepository;
import com.aluracursos.challenge_literalura.repository.LibroRepository;
import com.aluracursos.challenge_literalura.service.ConsumoAPI;
import com.aluracursos.challenge_literalura.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> datosLibros = new ArrayList<>();
    private LibroRepository libroRepositorio;
    private AutorRepository autorRepositorio;
    private List<Libro> libros;
    private Optional<Libro> libroBuscado;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepositorio = libroRepository;
        this.autorRepositorio = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    Elija la opción a través de su número:
                    1- buscar libro por titulo
                    2- listar libros registrados
                    3- listar autores registrados
                    4- listar autores vivos en determinado año
                    5- listar libros por idioma
                    0- salir                    
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosPorAño();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private DatosRespuesta getDatosLibro() {
        System.out.println("Introduce el titulo del libro que desea buscar: ");
        var busquedaPorTitulo = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + busquedaPorTitulo.replace(" ", "%20"));
        DatosRespuesta datos = conversor.obtenerDatos(json, DatosRespuesta.class);
        return datos;
    }

    private void buscarLibroPorTitulo() {
        DatosRespuesta respuesta = getDatosLibro();

        if (respuesta.results().isEmpty()) {
            System.out.println("Libro no encontrado");
            return;
        }

        DatosLibro datosLibro = respuesta.results().get(0);
        Optional<Libro> libroExistente =
                libroRepositorio.findByTituloIgnoreCase(datosLibro.titulo());

        if (libroExistente.isPresent()) {
            System.out.println("Libro ya existente en la base de datos");
            System.out.println(libroExistente.get());
            return;
        }
        DatosAutor datosAutor = datosLibro.autores().get(0);

        Autor autor = autorRepositorio
                .findByNombre(datosAutor.nombre())
                .orElseGet(() -> autorRepositorio.save(new Autor(datosAutor)));

        Libro libro = new Libro(datosLibro);
        libro.setAutor(autor);


        libroRepositorio.save(libro);

        System.out.println(libro);
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepositorio.findAll();

        libros.forEach(System.out::println);
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepositorio.findAllWithLibros();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        for (Autor autor : autores) {
            System.out.println("Autor: " + autor.getNombre());
            System.out.println("Fecha de Nacimiento: " + (autor.getFechaNacimiento() != null ? autor.getFechaNacimiento() : "Desconocido"));
            System.out.println("Fecha de Fallecimiento: " + (autor.getFechaMuerte() != null ? autor.getFechaMuerte() : "Vivo"));

            List<Libro> librosDelAutor = autor.getLibros();
            if (librosDelAutor != null && !librosDelAutor.isEmpty()) {
                List<String> titulos = librosDelAutor.stream()
                        .map(Libro::getTitulo)
                        .collect(Collectors.toList());
                System.out.println("Libros: [" + String.join(", ", titulos) + "]");
            } else {
                System.out.println("Libros: No hay libros registrados");
            }
            System.out.println();
        }
    }

    private void listarAutoresVivosPorAño() {
        Integer año = null;

        while (año == null) {
            System.out.println("Ingrese el año vivo de autor(es) que desea buscar:");
            String entrada = teclado.nextLine();

            try {
                año = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Información no válida. Debe ingresar un año numérico (ej: 1890).");
            }
        }

        List<Autor> autores = autorRepositorio.autoresVivosEnAño(año);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + año);
            return;
        }

        System.out.println("\nAutores vivos en el año " + año);
        System.out.println("==========================================\n");

        for (Autor autor : autores) {
            System.out.println("Autor: " + autor.getNombre());
            System.out.println("Fecha de Nacimiento: " + autor.getFechaNacimiento());

            String muerte = autor.getFechaMuerte() != null ?
                    String.valueOf(autor.getFechaMuerte()) : "Vivo";
            System.out.println("Fecha de Fallecimiento: " + muerte);

            List<Libro> librosDelAutor = autor.getLibros();
            if (librosDelAutor != null && !librosDelAutor.isEmpty()) {
                List<String> titulos = librosDelAutor.stream()
                        .map(Libro::getTitulo)
                        .collect(Collectors.toList());
                System.out.println("Libros: " + titulos);
            } else {
                System.out.println("Libros: []");
            }
            System.out.println();
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para buscar:
                es - español
                en - ingles
                fr - frances
                pt - portugues
                """);

        String idioma = teclado.nextLine().trim().toLowerCase();

        List<Libro> libros = libroRepositorio.findByIdiomas(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
        } else {
            for (Libro libro : libros) {
                System.out.println("----- LIBRO -----");
                System.out.println("  Título: " + libro.getTitulo());
                System.out.println("  Autor: " + libro.getAutor().getNombre());

                // Formatear la lista de idiomas
                String idiomasStr = libro.getIdiomas() != null ?
                        String.join(", ", libro.getIdiomas()) : "No especificado";
                System.out.println("  Idiomas: " + idiomasStr);

                System.out.println("  Número de descargas: " + libro.getNumeroDescargas());
                System.out.println("-----------------");
                System.out.println();
            }
            System.out.println("Total de libros encontrados: " + libros.size());
        }
    }
}
