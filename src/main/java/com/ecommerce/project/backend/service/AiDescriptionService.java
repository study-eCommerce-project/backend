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

    public Response generateDescription(AiProductRequestDto req) {

        Response res = aiWebClient.post()
                .uri("/ai/description")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Response.class)
                .block();

        return res;
    }

    @Data
    public static class Response {
        private List<Block> blocks;

        @Data
        public static class Block {
            private String type;    // "text" or "image"
            private String content; // 텍스트일 때
            private String url;     // 이미지일 때
        }
    }

}
