package clinica_backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Redirige a la página de inicio correctamente
        return "redirect:/paginainicio.html";
    }
}
