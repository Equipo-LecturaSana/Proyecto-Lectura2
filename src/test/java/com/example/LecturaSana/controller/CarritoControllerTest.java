package com.example.LecturaSana.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.LecturaSana.config.PayPalConfig;
import com.example.LecturaSana.model.CarritoItem;
import com.example.LecturaSana.repository.UsuarioRepository;
import com.example.LecturaSana.service.CarritoService;
import com.example.LecturaSana.service.LibroService;
import com.example.LecturaSana.service.PedidoService;
import com.example.LecturaSana.service.UsuarioService;

/**
 * Tests para CarritoController – cubre GET /carrito y POST /carrito/agregar.
 */
@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarritoService carritoService;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private LibroService libroService;

    @MockitoBean
    private PayPalConfig payPalConfig;

    // SecurityConfig necesita este bean
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser
    void getCarrito_devuelveVistaCarrito() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Mock del servicio: carrito vacío
        when(carritoService.obtenerCarritoPorSession(anyString()))
                .thenReturn(Collections.emptyList());
        when(carritoService.calcularTotal(anyString()))
                .thenReturn(0.0);
        when(payPalConfig.getClientId())
                .thenReturn("test-client-id");

        mockMvc.perform(get("/carrito").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("carrito"))
               .andExpect(model().attributeExists("carrito", "subtotal", "igv", "total"));
    }

    @Test
    @WithMockUser
    void getCarrito_conItems_calculaTotales() throws Exception {
        MockHttpSession session = new MockHttpSession();

        CarritoItem item = new CarritoItem(1L, "Libro Test", 50.0, 2, "img.jpg", session.getId());

        when(carritoService.obtenerCarritoPorSession(anyString()))
                .thenReturn(List.of(item));
        when(carritoService.calcularTotal(anyString()))
                .thenReturn(100.0);
        when(payPalConfig.getClientId())
                .thenReturn("test-client-id");

        mockMvc.perform(get("/carrito").session(session))
               .andExpect(status().isOk())
               .andExpect(model().attribute("subtotal", 100.0))
               .andExpect(model().attribute("igv", 100.0 * 0.18))
               .andExpect(model().attribute("total", 100.0 * 1.18));
    }

    @Test
    @WithMockUser
    void agregarAlCarrito_exitoso() throws Exception {
        MockHttpSession session = new MockHttpSession();

        doNothing().when(carritoService)
                   .agregarAlCarrito(anyString(), anyLong(), anyString(), anyDouble(), anyString());

        mockMvc.perform(post("/carrito/agregar")
                        .session(session)
                        .param("libroId", "1")
                        .param("titulo", "Libro Test")
                        .param("precio", "25.00")
                        .param("imagen", "img.jpg"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void agregarAlCarrito_stockInsuficiente_devuelve400() throws Exception {
        MockHttpSession session = new MockHttpSession();

        doThrow(new Exception("Stock insuficiente"))
                .when(carritoService)
                .agregarAlCarrito(anyString(), anyLong(), anyString(), anyDouble(), anyString());

        mockMvc.perform(post("/carrito/agregar")
                        .session(session)
                        .param("libroId", "1")
                        .param("titulo", "Libro Test")
                        .param("precio", "25.00")
                        .param("imagen", "img.jpg"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser
    void limpiarCarrito_redireccionaACarrito() throws Exception {
        MockHttpSession session = new MockHttpSession();
        doNothing().when(carritoService).limpiarCarrito(anyString());

        mockMvc.perform(post("/carrito/limpiar").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/carrito"));
    }

    @Test
    @WithMockUser
    void eliminarItem_redireccionaACarrito() throws Exception {
        doNothing().when(carritoService).eliminarItemPorId(anyLong());

        mockMvc.perform(post("/carrito/eliminar/1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/carrito"));
    }
}

