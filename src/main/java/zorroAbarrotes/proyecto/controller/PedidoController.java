package zorroAbarrotes.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.PedidoEntity;
import zorroAbarrotes.proyecto.service.pedido.PedidoService;

import java.util.List;


@Controller
@RequestMapping("pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("lista")
    public String listar(Model model) {
        List<PedidoEntity> pedidos = pedidoService.findAll();
        System.out.println(pedidos);
        model.addAttribute("pedidos", pedidos);
        return "pedido/lista-pedido";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        PedidoEntity pedido = pedidoService.findById(id);
        if (pedido != null) {
            try {
                pedidoService.deleteById(pedido.getId());
                flash.addFlashAttribute("success", "Se elimino el pedido con exito");
                return "redirect:/pedido/lista";
            }catch (Exception e) {
                flash.addFlashAttribute("errorMessage", "Problemas con eliminar pedido: " + e.getMessage());
                return "redirect:/pedido/lista";
            }

        }else{
            flash.addFlashAttribute("errorMessage", "El pedido no existe");
            return "redirect:/pedido/lista";
        }
    }
}
