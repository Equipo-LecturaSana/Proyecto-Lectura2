package com.example.LecturaSana.controller;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.LecturaSana.config.SecurityConfig;
import com.example.LecturaSana.model.CarritoItem;
import com.example.LecturaSana.model.Pedido;
import com.example.LecturaSana.repository.UsuarioRepository;
import com.example.LecturaSana.service.CarritoService;
import com.example.LecturaSana.service.PayPalService;
import com.example.LecturaSana.service.PedidoService;
import com.example.LecturaSana.service.UsuarioService;

/**
 * Tests para PayPalController – cubre create-order y capture-order.
 * Se mockea PayPalService para no llamar a la API real de PayPal.
 *
 * FIX:
 * - Importa SecurityConfig (si no, Spring usa seguridad por defecto y CSRF activa -> 403)
 * - Agrega .with(csrf()) en todos los POST
 */
@WebMvcTest(PayPalController.class)
@Import(SecurityConfig.class)
class PayPalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayPalService payPalService;

    @MockitoBean
    private CarritoService carritoService;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private UsuarioService usuarioService;

    // SecurityConfig necesita este bean
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_carritoVacio_devuelveBadRequest() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(carritoService.obtenerCarritoPorSession(anyString()))
                .thenReturn(List.of());

        mockMvc.perform(post("/paypal/create-order")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compradorNombre\":\"Test\",\"direccionEnvio\":\"Calle 1\",\"compradorTelefono\":\"912345678\"}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("El carrito está vacío"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_exitoso_devuelveOrderId() throws Exception {
        MockHttpSession session = new MockHttpSession();

        CarritoItem item = new CarritoItem(1L, "Libro X", 50.0, 1, "img.jpg", session.getId());

        when(carritoService.obtenerCarritoPorSession(anyString()))
                .thenReturn(List.of(item));
        doNothing().when(carritoService).validarStockCarritoCompleto(anyString());
        when(carritoService.calcularTotal(anyString()))
                .thenReturn(50.0);
        when(payPalService.createOrder(anyDouble(), eq("USD"), anyString()))
                .thenReturn("PAYPAL-ORDER-123");

        mockMvc.perform(post("/paypal/create-order")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compradorNombre\":\"Test\",\"direccionEnvio\":\"Calle 1\",\"compradorTelefono\":\"912345678\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("PAYPAL-ORDER-123"));
    }

    @Test
@WithMockUser(roles = "USER")
void captureOrder_exitoso_devuelveCompleted() throws Exception {
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("paypal_compradorNombre", "Test User");
    session.setAttribute("paypal_direccionEnvio", "Calle 123");
    session.setAttribute("paypal_compradorTelefono", "912345678");

    when(payPalService.captureOrder("ORDER-456"))
            .thenReturn(Map.of("status", "COMPLETED"));

    when(carritoService.calcularTotal(anyString()))
            .thenReturn(100.0);

    when(carritoService.obtenerCarritoPorSession(anyString()))
            .thenReturn(List.of(
                    new CarritoItem(1L, "Libro Y", 100.0, 1, "img.jpg", session.getId())
            ));

    // Simular que JPA asigna un ID al persistir el pedido (Map.of no admite nulls)
    doAnswer(invocation -> {
        Pedido pedido = invocation.getArgument(0);
        pedido.setId(1L);
        return null;
    }).when(pedidoService).guardarPedido(any(Pedido.class));

    doNothing().when(carritoService).limpiarCarrito(anyString());

    // 👇 Si tu controller usa usuarioService:
    when(usuarioService.buscarPorEmail(anyString()))
            .thenReturn(java.util.Optional.empty());

    mockMvc.perform(post("/paypal/capture-order/ORDER-456")
                    .with(csrf())
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
}

    @Test
    @WithMockUser(roles = "USER")
    void captureOrder_noCompletado_devuelveError() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(payPalService.captureOrder("ORDER-789"))
                .thenReturn(Map.of("status", "PENDING"));

        mockMvc.perform(post("/paypal/capture-order/ORDER-789")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").exists());
    }
}
