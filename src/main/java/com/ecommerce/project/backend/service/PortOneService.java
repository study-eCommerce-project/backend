package com.ecommerce.project.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PortOneService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // .env에서 spring-dotenv가 자동으로 environment로 올라간 값 읽기
    @Value("${STORE_ID}")
    private String storeId;

    @Value("${SECRET}")
    private String secret;

    // PortOne 결제 검증 API (V2)
    public JsonNode verifyPayment(String paymentId) throws Exception {

        String url = "https://api.portone.io/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "PortOne " + secret); // V2 인증 방식
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("PortOne 결제 조회 실패: " + response.getBody());
        }

        return objectMapper.readTree(response.getBody());
    }
}
