error id: file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/Lectura%20Sana%20xd/LecturaSana/src/main/java/com/example/LecturaSana/controller/CarritoController.java:_empty_/RequestParam#
file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/Lectura%20Sana%20xd/LecturaSana/src/main/java/com/example/LecturaSana/controller/CarritoController.java
empty definition using pc, found symbol in pc: _empty_/RequestParam#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 10094
uri: file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/Lectura%20Sana%20xd/LecturaSana/src/main/java/com/example/LecturaSana/controller/CarritoController.java
text:
```scala
package com.example.LecturaSana.controller;

import java.math.BigDecimal;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CarritoController {

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final LibroService libroService;

    public CarritoController(CarritoService cs, PedidoService ps, UsuarioService us, LibroService ls) {
        this.carritoService = cs;
        this.pedidoService = ps;
        this.usuarioService = us;
        this.libroService = ls;
    }

    /**
     * Muestra la página del carrito de compras
     */
    @GetMapping("/carrito")
    public String mostrarCarrito(HttpSession session, Model model) {
        try {
            String sessionId = session.getId();
            List<CarritoItem> carrito = carritoService.obtenerCarritoPorSession(sessionId);

            model.addAttribute("carrito", carrito);

            if (!carrito.isEmpty()) {
                BigDecimal subtotal = carritoService.calcularSubtotal(sessionId);
                BigDecimal igv = carritoService.calcularIgv(sessionId);
                BigDecimal total = carritoService.calcularTotal(sessionId);

                model.addAttribute("subtotal", subtotal);
                model.addAttribute("igv", igv);
                model.addAttribute("total", total);
            } else {
                model.addAttribute("subtotal", BigDecimal.ZERO);
                model.addAttribute("igv", BigDecimal.ZERO);
                model.addAttribute("total", BigDecimal.ZERO);
            }

            if (!model.containsAttribute("pedidoForm")) {
                model.addAttribute("pedidoForm", new Pedido());
            }

            return "carrito";
        } catch (Exception e) {
            logger.error("Error al mostrar carrito", e);
            model.addAttribute("error", "Error al cargar el carrito: " + e.getMessage());
            return "carrito";
        }
    }

    /**
     * Agrega un libro al carrito (endpoint AJAX)
     */
    @PostMapping("/carrito/agregar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarAlCarrito(
            @RequestParam Long libroId,
            @RequestParam String titulo,
            @RequestParam BigDecimal precio,
            @RequestParam String imagen,
            HttpSession session) {
        try {
            String sessionId = session.getId();
            carritoService.agregarAlCarrito(sessionId, libroId, titulo, precio, imagen);

            int cantidadTotal = carritoService.contarItemsEnCarrito(sessionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "¡Libro añadido al carrito!");
            response.put("cantidadTotal", cantidadTotal);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al agregar al carrito: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Error de estado al agregar al carrito: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al agregar al carrito", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al agregar el libro al carrito"));
        }
    }

    /**
     * Obtiene el contador de items en el carrito (endpoint AJAX)
     */
    @GetMapping("/api/carrito/count")
    @ResponseBody
    public Map<String, Object> obtenerContadorCarrito(HttpSession session) {
        try {
            String sessionId = session.getId();
            int cantidadTotal = carritoService.contarItemsEnCarrito(sessionId);
            return Map.of("count", cantidadTotal, "success", true);
        } catch (Exception e) {
            logger.error("Error al obtener contador del carrito", e);
            return Map.of("count", 0, "success", false);
        }
    }

    /**
     * Procesa el pago y crea el pedido
     */
    @PostMapping("/carrito/procesar")
    public String procesarPedido(
            @Valid @ModelAttribute("pedidoForm") Pedido pedidoForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String sessionId = session.getId();

        // Validación de fecha de vencimiento de tarjeta
        try {
            if (YearMonth.parse(pedidoForm.getFechaVencimiento()).isBefore(YearMonth.now())) {
                bindingResult.rejectValue("fechaVencimiento", "error", "La tarjeta está vencida.");
            }
        } catch (Exception e) {
            bindingResult.rejectValue("fechaVencimiento", "error", "Formato de fecha inválido (usar MM/yyyy).");
        }

        // Si hay errores de validación, volver al carrito
        if (bindingResult.hasErrors()) {
            try {
                List<CarritoItem> carrito = carritoService.obtenerCarritoPorSession(sessionId);
                model.addAttribute("carrito", carrito);

                BigDecimal subtotal = carritoService.calcularSubtotal(sessionId);
                BigDecimal igv = carritoService.calcularIgv(sessionId);
                BigDecimal total = carritoService.calcularTotal(sessionId);

                model.addAttribute("subtotal", subtotal);
                model.addAttribute("igv", igv);
                model.addAttribute("total", total);
            } catch (Exception e) {
                logger.error("Error al recargar carrito después de validación", e);
            }
            return "carrito";
        }

        // Procesar el pedido
        try {
            // Validar stock disponible
            carritoService.validarStockCarritoCompleto(sessionId);

            // Calcular totales
            BigDecimal subtotal = carritoService.calcularSubtotal(sessionId);
            BigDecimal igv = carritoService.calcularIgv(sessionId);
            BigDecimal total = carritoService.calcularTotal(sessionId);

            // Configurar pedido
            pedidoForm.setFecha(LocalDateTime.now());
            pedidoForm.setSubtotal(subtotal.doubleValue());
            pedidoForm.setIgv(igv.doubleValue());
            pedidoForm.setTotal(total.doubleValue());
            pedidoForm.setSessionId(sessionId);

            // Copiar items del carrito al pedido
            List<CarritoItem> itemsPedido = new ArrayList<>();
            for (CarritoItem item : carritoService.obtenerCarritoPorSession(sessionId)) {
                CarritoItem itemPedido = new CarritoItem(
                    item.getLibroId(),
                    item.getTitulo(),
                    item.getPrecio(),
                    item.getCantidad(),
                    item.getImagen(),
                    "PEDIDO"
                );
                itemsPedido.add(itemPedido);
            }
            pedidoForm.setItems(itemsPedido);

            // Asociar usuario si está autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
                usuarioService.buscarPorEmail(authentication.getName())
                        .ifPresent(pedidoForm::setUsuario);
            }

            // Guardar pedido y limpiar carrito
            pedidoService.guardarPedido(pedidoForm);
            carritoService.limpiarCarrito(sessionId);

            logger.info("Pedido procesado exitosamente. ID: {}", pedidoForm.getId());
            redirectAttributes.addFlashAttribute("mensaje",
                    "¡Compra realizada exitosamente! Número de pedido: " + pedidoForm.getId());

        } catch (IllegalStateException e) {
            logger.error("Error de estado al procesar pedido", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al procesar pedido", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al procesar pedido", e);
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error al procesar tu compra. Por favor, intenta nuevamente.");
        }

        return "redirect:/carrito";
    }

    /**
     * Actualiza la cantidad de un item en el carrito
     */
    @PostMapping("/carrito/actualizar-cantidad")
    public String actualizarCantidad(
            @RequestParam Long itemId,
            @Reque@@stParam Integer cantidad,
            RedirectAttributes redirectAttributes) {
        try {
            carritoService.actualizarCantidad(itemId, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada correctamente");
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al actualizar cantidad: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            logger.warn("Error de estado al actualizar cantidad: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar cantidad", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cantidad");
        }
        return "redirect:/carrito";
    }

    /**
     * Elimina un item específico del carrito
     */
    @PostMapping("/carrito/eliminar/{id}")
    public String eliminarItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            carritoService.eliminarItemPorId(id);
            redirectAttributes.addFlashAttribute("mensaje", "Item eliminado del carrito");
        } catch (IllegalArgumentException e) {
            logger.warn("Error al eliminar item: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar item", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el item del carrito");
        }
        return "redirect:/carrito";
    }

    /**
     * Limpia completamente el carrito
     */
    @PostMapping("/carrito/limpiar")
    public String limpiarCarrito(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            String sessionId = session.getId();
            carritoService.limpiarCarrito(sessionId);
            redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado correctamente");
        } catch (Exception e) {
            logger.error("Error al limpiar carrito", e);
            redirectAttributes.addFlashAttribute("error", "Error al limpiar el carrito");
        }
        return "redirect:/carrito";
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/RequestParam#