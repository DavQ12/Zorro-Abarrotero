package zorroAbarrotes.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/index")
    public String testIndex(Model model) {
        model.addAttribute("contenido", "Página de prueba");
        return "index";
    }

    @GetMapping("/login")
    public String testLogin(Model model) {
        model.addAttribute("contenido", "Página de prueba");
        return "login";
    }
}
