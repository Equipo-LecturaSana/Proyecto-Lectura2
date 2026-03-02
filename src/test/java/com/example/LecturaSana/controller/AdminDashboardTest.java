package com.example.LecturaSana.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.LecturaSana.repository.UsuarioRepository;

/**
 * Test para AdminDashboard – cubre los 4 endpoints del controlador.
 */
@WebMvcTest(AdminDashboard.class)
class AdminDashboardTest {

    @Autowired
    private MockMvc mockMvc;

    // SecurityConfig necesita este bean
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(roles = "PUBLICADOR")
    void panel_devuelveVistaAdminPanel() throws Exception {
        mockMvc.perform(get("/admin/panel"))
               .andExpect(status().isOk())
               .andExpect(view().name("adminPanel"));
    }

    @Test
    @WithMockUser(roles = "PUBLICADOR")
    void libros_redireccionaALibros() throws Exception {
        mockMvc.perform(get("/admin/libros"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/libros"));
    }

    @Test
    @WithMockUser(roles = "PUBLICADOR")
    void novedades_redireccionaANovedades() throws Exception {
        mockMvc.perform(get("/admin/novedades"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/novedades"));
    }

    @Test
    @WithMockUser(roles = "PUBLICADOR")
    void noticias_redireccionaANoticias() throws Exception {
        mockMvc.perform(get("/admin/noticias"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/noticias"));
    }
}
