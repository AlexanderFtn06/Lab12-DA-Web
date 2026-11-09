package com.tecsup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
}