package com.example.LecturaSana.controller;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.LecturaSana.model.*;
import com.example.LecturaSana.service.*;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador REST para el flujo de pago con PayPal (Sandbox).
 *
 * Flujo:
 * 1. Frontend llama POST /paypal/create-order con datos de envío
 * 2. Backend crea la orden en PayPal y devuelve el orderID
 * 3. El SDK JavaScript de PayPal abre el popup para que el usuario apruebe
 * 4. Frontend llama POST /paypal/capture-order/{orderId}
 * 5. Backend captura el pago, crea el Pedido, reduce stock, limpia carrito
 */
@RestController
@RequestMapping("/paypal")
public class PayPalController {

    private final PayPalService payPalService;
    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public PayPalController(PayPalService payPalService, CarritoService carritoService,
                            PedidoService pedidoService, UsuarioService usuarioService) {
        this.payPalService = payPalService;
        this.carritoService = carritoService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    // ─── Crear Orden PayPal ──────────────────────────────────────────

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Map<String, String> datosEnvio,
            HttpSession session) {
        try {
            String sid = session.getId();
            List<CarritoItem> items = carritoService.obtenerCarritoPorSession(sid);

            if (items.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El carrito está vacío"));
            }

            // Validar stock
            carritoService.validarStockCarritoCompleto(sid);

            // Calcular total
            double subtotal = carritoService.calcularTotal(sid);
            double total = subtotal * 1.18; // +IGV

            // Guardar datos de envío en sesión para usarlos después en capture
            session.setAttribute("paypal_compradorNombre", datosEnvio.get("compradorNombre"));
            session.setAttribute("paypal_direccionEnvio", datosEnvio.get("direccionEnvio"));
            session.setAttribute("paypal_compradorTelefono", datosEnvio.get("compradorTelefono"));

            // Crear orden en PayPal
            String orderId = payPalService.createOrder(total, "USD", "Compra en LecturaSana");

            return ResponseEntity.ok(Map.of("id", orderId));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─── Capturar Orden PayPal ───────────────────────────────────────

    @PostMapping("/capture-order/{orderId}")
    public ResponseEntity<Map<String, Object>> captureOrder(
            @PathVariable String orderId,
            HttpSession session) {
        try {
            String sid = session.getId();

            // Capturar el pago en PayPal
            Map<String, Object> captureResponse = payPalService.captureOrder(orderId);
            String status = (String) captureResponse.get("status");

            if (!"COMPLETED".equals(status)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El pago no fue completado. Estado: " + status));
            }

            // Calcular montos
            double subtotal = carritoService.calcularTotal(sid);
            double igv = subtotal * 0.18;
            double total = subtotal * 1.18;

            // Crear el Pedido
            Pedido pedido = new Pedido();
            pedido.setFecha(LocalDateTime.now());
            pedido.setSubtotal(subtotal);
            pedido.setIgv(igv);
            pedido.setTotal(total);
            pedido.setSessionId(sid);
            pedido.setMetodoPago("PAYPAL");
            pedido.setPaypalOrderId(orderId);

            // Datos de envío guardados en sesión
            pedido.setCompradorNombre((String) session.getAttribute("paypal_compradorNombre"));
            pedido.setDireccionEnvio((String) session.getAttribute("paypal_direccionEnvio"));
            pedido.setCompradorTelefono((String) session.getAttribute("paypal_compradorTelefono"));

            // Clonar items del carrito al pedido
            List<CarritoItem> pedidoItems = new ArrayList<>();
            for (CarritoItem item : carritoService.obtenerCarritoPorSession(sid)) {
                pedidoItems.add(new CarritoItem(
                        item.getLibroId(), item.getTitulo(), item.getPrecio(),
                        item.getCantidad(), item.getImagen(), "PEDIDO"));
            }
            pedido.setItems(pedidoItems);

            // Asociar usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                usuarioService.buscarPorEmail(auth.getName()).ifPresent(pedido::setUsuario);
            }

            // Guardar pedido y reducir stock
            pedidoService.guardarPedido(pedido);

            // Limpiar carrito y datos de sesión de PayPal
            carritoService.limpiarCarrito(sid);
            session.removeAttribute("paypal_compradorNombre");
            session.removeAttribute("paypal_direccionEnvio");
            session.removeAttribute("paypal_compradorTelefono");

            return ResponseEntity.ok(Map.of(
                    "status", "COMPLETED",
                    "pedidoId", pedido.getId(),
                    "message", "¡Compra exitosa con PayPal! Pedido #" + pedido.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar pago PayPal: " + e.getMessage()));
        }
    }
}
