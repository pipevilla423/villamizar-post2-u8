
package com.universidad.catalogo.controller;

import com.universidad.catalogo.model.Categoria;
import com.universidad.catalogo.service.CategoriaService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    // Listar todas las categorías
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.listarTodas());
        return "categorias/lista";
    }

    // Mostrar formulario para crear una categoría
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("titulo", "Nueva Categoría");
        return "categorias/formulario";
    }

    // Guardar una categoría nueva o editada
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("categoria") Categoria categoria,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute(
                "titulo",
                categoria.getId() == null
                    ? "Nueva Categoría"
                    : "Editar Categoría"
            );

            return "categorias/formulario";
        }

        try {
            service.guardar(categoria);

        } catch (IllegalStateException e) {

            result.rejectValue(
                "nombre",
                "duplicado",
                e.getMessage()
            );

            model.addAttribute(
                "titulo",
                categoria.getId() == null
                    ? "Nueva Categoría"
                    : "Editar Categoría"
            );

            return "categorias/formulario";
        }

        return "redirect:/categorias";
    }

    // Mostrar formulario para editar una categoría
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
            "categoria",
            service.buscarPorId(id)
        );

        model.addAttribute("titulo", "Editar Categoría");

        return "categorias/formulario";
    }

    // Mostrar confirmación antes de eliminar
    @GetMapping("/eliminar/{id}")
    public String confirmarEliminar(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
            "categoria",
            service.buscarPorId(id)
        );

        return "categorias/confirmar-eliminar";
    }

    // Eliminar categoría después de confirmar
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return "redirect:/categorias";
    }
}
