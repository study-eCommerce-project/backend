package com.ecommerce.project.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiProductRequestDto {
    private String name;
    private Integer price;
    private String options;
    private String category_path;
    private List<String> image_urls;
}
