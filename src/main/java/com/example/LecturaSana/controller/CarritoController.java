package com.example.LecturaSana.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.LecturaSana.model.*;
import com.example.LecturaSana.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class CarritoController {

    private final CarritoService cs;
    private final PedidoService ps;
    private final UsuarioService us;
    private final LibroService ls;

    public CarritoController(CarritoService cs, PedidoService ps, UsuarioService us, LibroService ls) {
        this.cs = cs;
        this.ps = ps;
        this.us = us;
        this.ls = ls;
    }

    @GetMapping("/carrito")
    public String carrito(HttpSession s, Model m) {
        List<CarritoItem> c = cs.obtenerCarritoPorSession(s.getId());
        m.addAttribute("carrito", c);
        double sub = cs.calcularTotal(s.getId());
        m.addAttribute("subtotal", sub);
        m.addAttribute("igv", sub * 0.18);
        m.addAttribute("total", sub * 1.18);
        if (!m.containsAttribute("pedidoForm")) {
            m.addAttribute("pedidoForm", new Pedido());
        }
        return "carrito";
    }

    @PostMapping("/carrito/agregar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregar(@RequestParam Long libroId, @RequestParam String titulo, @RequestParam Double precio, @RequestParam String imagen, HttpSession s) {
        try {
            cs.agregarAlCarrito(s.getId(), libroId, titulo, precio, imagen);
            return ResponseEntity.ok(Map.of("success", true, "message", "¡Añadido!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/api/carrito/count")
    @ResponseBody
    public Map<String, Object> count(HttpSession s) {
        return Map.of("count", cs.obtenerCarritoPorSession(s.getId()).stream().mapToInt(CarritoItem::getCantidad).sum());
    }

    @PostMapping("/carrito/procesar")
    public String procesar(@Valid @ModelAttribute("pedidoForm") Pedido form, BindingResult res, HttpSession s, Model m, RedirectAttributes attr) {
        String sid = s.getId();
        try {
            if (YearMonth.parse(form.getFechaVencimiento()).isBefore(YearMonth.now())) {
                res.rejectValue("fechaVencimiento", "error", "Tarjeta vencida.");
            }
        } catch (Exception e) {
            res.rejectValue("fechaVencimiento", "error", "Fecha inválida.");
        }

        if (res.hasErrors()) {
            List<CarritoItem> c = cs.obtenerCarritoPorSession(sid);
            m.addAttribute("carrito", c);
            double sub = cs.calcularTotal(sid);
            m.addAttribute("subtotal", sub);
            m.addAttribute("igv", sub * 0.18);
            m.addAttribute("total", sub * 1.18);
            return "carrito";
        }

        try {
            cs.validarStockCarritoCompleto(sid);
            double sub = cs.calcularTotal(sid);
            form.setFecha(LocalDateTime.now());
            form.setSubtotal(sub);
            form.setIgv(sub * 0.18);
            form.setTotal(sub * 1.18);
            form.setSessionId(sid);

            List<CarritoItem> items = new ArrayList<>();
            for (CarritoItem i : cs.obtenerCarritoPorSession(sid)) {
                items.add(new CarritoItem(i.getLibroId(), i.getTitulo(), i.getPrecio(), i.getCantidad(), i.getImagen(), "PEDIDO"));
            }
            form.setItems(items);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                us.buscarPorEmail(auth.getName()).ifPresent(form::setUsuario);
            }

            ps.guardarPedido(form);
            cs.limpiarCarrito(sid);
            attr.addFlashAttribute("mensaje", "¡Compra exitosa! ID: " + form.getId());
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/actualizar-cantidad")
    public String actualizar(@RequestParam Long itemId, @RequestParam Integer cantidad, RedirectAttributes attr) {
        try {
            cs.actualizarCantidad(itemId, cantidad);
        } catch (Exception e) {
            attr.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        cs.eliminarItemPorId(id);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/limpiar")
    public String limpiar(HttpSession s) {
        cs.limpiarCarrito(s.getId());
        return "redirect:/carrito";
    }
}
