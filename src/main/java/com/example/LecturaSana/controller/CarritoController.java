package com.example.LecturaSana.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.LecturaSana.config.PayPalConfig;
import com.example.LecturaSana.model.CarritoItem;
import com.example.LecturaSana.model.Pedido;
import com.example.LecturaSana.service.CarritoService;
import com.example.LecturaSana.service.LibroService;
import com.example.LecturaSana.service.PedidoService;
import com.example.LecturaSana.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CarritoController {

    private final CarritoService cs;
    private final PedidoService ps;
    private final UsuarioService us;
    private final LibroService ls;
    private final PayPalConfig payPalConfig;

    public CarritoController(CarritoService cs, PedidoService ps, UsuarioService us, LibroService ls, PayPalConfig payPalConfig) {
        this.cs = cs;
        this.ps = ps;
        this.us = us;
        this.ls = ls;
        this.payPalConfig = payPalConfig;
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
        m.addAttribute("paypalClientId", payPalConfig.getClientId());
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
    public String procesar(@ModelAttribute("pedidoForm") Pedido form, BindingResult res, HttpSession s, Model m, RedirectAttributes attr) {
        String sid = s.getId();
        
        // Validación manual de datos de envío
        if (form.getCompradorNombre() == null || form.getCompradorNombre().isBlank()) {
            res.rejectValue("compradorNombre", "error", "El nombre es obligatorio.");
        }
        if (form.getDireccionEnvio() == null || form.getDireccionEnvio().isBlank()) {
            res.rejectValue("direccionEnvio", "error", "La dirección es obligatoria.");
        }
        if (form.getCompradorTelefono() == null || form.getCompradorTelefono().isBlank()) {
            res.rejectValue("compradorTelefono", "error", "El teléfono es obligatorio.");
        } else if (!form.getCompradorTelefono().matches("^9[0-9]{8}$")) {
            res.rejectValue("compradorTelefono", "error", "El teléfono debe tener 9 dígitos y empezar con 9.");
        }
        
        // Validación manual de campos de tarjeta (ya que son @Transient sin @NotBlank)
        if (form.getNumeroTarjeta() == null || form.getNumeroTarjeta().isBlank()) {
            res.rejectValue("numeroTarjeta", "error", "El número de tarjeta es obligatorio.");
        } else if (!form.getNumeroTarjeta().matches("^[0-9]{16}$")) {
            res.rejectValue("numeroTarjeta", "error", "Debe ser un número de 16 dígitos.");
        }
        
        if (form.getCvv() == null || form.getCvv().isBlank()) {
            res.rejectValue("cvv", "error", "El CVV es obligatorio.");
        } else if (!form.getCvv().matches("^[0-9]{3,4}$")) {
            res.rejectValue("cvv", "error", "CVV inválido.");
        }
        
        // Validar fecha de vencimiento
        if (form.getFechaVencimiento() == null || form.getFechaVencimiento().isBlank()) {
            res.rejectValue("fechaVencimiento", "error", "La fecha de vencimiento es obligatoria.");
        } else {
            try {
                if (YearMonth.parse(form.getFechaVencimiento()).isBefore(YearMonth.now())) {
                    res.rejectValue("fechaVencimiento", "error", "Tarjeta vencida.");
                }
            } catch (Exception e) {
                res.rejectValue("fechaVencimiento", "error", "Fecha inválida.");
            }
        }

        if (res.hasErrors()) {
            List<CarritoItem> c = cs.obtenerCarritoPorSession(sid);
            m.addAttribute("carrito", c);
            double sub = cs.calcularTotal(sid);
            m.addAttribute("subtotal", sub);
            m.addAttribute("igv", sub * 0.18);
            m.addAttribute("total", sub * 1.18);
            m.addAttribute("paypalClientId", payPalConfig.getClientId());
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

            form.setMetodoPago("TARJETA");
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
