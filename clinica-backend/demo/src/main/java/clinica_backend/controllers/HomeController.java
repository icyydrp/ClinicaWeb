package clinica_backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador encargado de gestionar la redirección hacia la página de inicio de la aplicación.
 */
@Controller
public class HomeController {

    /**
     * Redirige la solicitud raíz ("/") hacia la página de inicio del sitio web.
     *
     * @return Una cadena que indica la redirección hacia "paginainicio.html".
     */
    @GetMapping("/")
    public String home() {
        // Redirige a la página de inicio correctamente
        return "redirect:/paginainicio.html";
    }
}
