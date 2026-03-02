package com.example.LecturaSana.controller;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.LecturaSana.model.CarritoItem;
import com.example.LecturaSana.repository.UsuarioRepository;
import com.example.LecturaSana.service.CarritoService;
import com.example.LecturaSana.service.PayPalService;
import com.example.LecturaSana.service.PedidoService;
import com.example.LecturaSana.service.UsuarioService;

/**
 * Tests para PayPalController – cubre create-order y capture-order.
 * Se mockea PayPalService para no llamar a la API real de PayPal.
 */
@WebMvcTest(PayPalController.class)
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
    @WithMockUser
    void createOrder_carritoVacio_devuelveBadRequest() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(carritoService.obtenerCarritoPorSession(anyString()))
                .thenReturn(List.of());

        mockMvc.perform(post("/paypal/create-order")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compradorNombre\":\"Test\",\"direccionEnvio\":\"Calle 1\",\"compradorTelefono\":\"912345678\"}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("El carrito está vacío"));
    }

    @Test
    @WithMockUser
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
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compradorNombre\":\"Test\",\"direccionEnvio\":\"Calle 1\",\"compradorTelefono\":\"912345678\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("PAYPAL-ORDER-123"));
    }

    @Test
    @WithMockUser
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
        doNothing().when(pedidoService).guardarPedido(any());
        doNothing().when(carritoService).limpiarCarrito(anyString());

        mockMvc.perform(post("/paypal/capture-order/ORDER-456")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser
    void captureOrder_noCompletado_devuelveError() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(payPalService.captureOrder("ORDER-789"))
                .thenReturn(Map.of("status", "PENDING"));

        mockMvc.perform(post("/paypal/capture-order/ORDER-789")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").exists());
    }
}
