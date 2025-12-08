package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.dto.AiProductRequestDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiDescriptionService {

    private final WebClient aiWebClient; // WebClientConfig에서 만든 Bean 자동 주입

    public AiResponse generateDescription(AiProductRequestDto req) {

        return aiWebClient.post()
                .uri("/ai/description")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AiResponse.class)
                .block();
    }

    @Data
    public static class AiResponse {
        private String description;   // FastAPI에서 온 설명
        private List<Block> blocks;   // 이미지/텍스트 블록

        @Data
        public static class Block {
            private String type;
            private String content;
            private String url;
        }
    }
}