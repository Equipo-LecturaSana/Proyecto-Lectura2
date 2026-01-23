package com.example.LecturaSana.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.LecturaSana.model.Pedido;
import com.example.LecturaSana.model.Usuario;
import com.example.LecturaSana.repository.PedidoRepository;
import com.example.LecturaSana.repository.UsuarioRepository;
import com.example.LecturaSana.service.UsuarioService;
import java.util.List;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String verPerfil(Authentication authentication, Model model) {
        // Obtenemos el usuario desde la sesión de seguridad
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtenemos historial de compras
        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByFechaDesc(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("pedidos", pedidos);

        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@RequestParam("email") String email,
            @RequestParam("password") String password,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String emailSession = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(emailSession).orElseThrow();

        usuario.setEmail(email);
        if (password != null && !password.isEmpty()) {
            usuarioService.actualizarPassword(usuario, password);
        } else {
            usuarioRepository.save(usuario);
        }

        redirectAttributes.addFlashAttribute("mensajeExito", "¡Perfil actualizado!");
        if (!emailSession.equals(email)) {
            return "redirect:/auth/logout";
        }
        return "redirect:/perfil";
    }
}
