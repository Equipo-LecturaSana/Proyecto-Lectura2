package com.example.LecturaSana.service;

import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.LecturaSana.config.PayPalConfig;

/**
 * Servicio que se comunica con la API REST v2 de PayPal
 * para crear y capturar órdenes de pago (sandbox).
 */
@Service
public class PayPalService {

    private final PayPalConfig config;
    private final RestTemplate restTemplate;

    public PayPalService(PayPalConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    // ─── 1. Obtener Access Token ─────────────────────────────────────

    /**
     * Solicita un token OAuth2 a PayPal usando las credenciales de la app.
     */
    public String getAccessToken() {
        String url = config.getBaseUrl() + "/v1/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(config.getClientId(), config.getClientSecret(), StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

        if (response == null || !response.containsKey("access_token")) {
            throw new RuntimeException("No se pudo obtener el access token de PayPal");
        }
        return (String) response.get("access_token");
    }

    // ─── 2. Crear Orden ──────────────────────────────────────────────

    /**
     * Crea una orden de pago en PayPal con el monto indicado.
     * Devuelve el ID de la orden para que el frontend la apruebe.
     *
     * @param totalAmount monto total a cobrar
     * @param currency    código de moneda (ej: "USD")
     * @param description descripción breve del pedido
     * @return el ID de la orden de PayPal
     */
    public String createOrder(double totalAmount, String currency, String description) {
        String url = config.getBaseUrl() + "/v2/checkout/orders";
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // Cuerpo de la petición
        String amount = String.format(Locale.US, "%.2f", totalAmount);

        Map<String, Object> amountMap = new LinkedHashMap<>();
        amountMap.put("currency_code", currency);
        amountMap.put("value", amount);

        Map<String, Object> purchaseUnit = new LinkedHashMap<>();
        purchaseUnit.put("description", description);
        purchaseUnit.put("amount", amountMap);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("intent", "CAPTURE");
        body.put("purchase_units", List.of(purchaseUnit));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

        if (response == null || !response.containsKey("id")) {
            throw new RuntimeException("No se pudo crear la orden en PayPal");
        }
        return (String) response.get("id");
    }

    // ─── 3. Capturar Orden ───────────────────────────────────────────

    /**
     * Captura (cobra) una orden previamente aprobada por el usuario.
     *
     * @param orderId ID de la orden de PayPal
     * @return mapa con la respuesta completa de PayPal
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> captureOrder(String orderId) {
        String url = config.getBaseUrl() + "/v2/checkout/orders/" + orderId + "/capture";
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("No se pudo capturar la orden de PayPal: " + orderId);
    }
}
