package com.tecsup.dto;

import lombok.Data;

@Data
public class PedidoDetalleDTO {
    private Long id;
    private Integer cantidad;
    private ClienteDTO cliente;
    private ProductoDTO producto;
}