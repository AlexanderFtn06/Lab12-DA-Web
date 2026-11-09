package com.tecsup.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PedidoDTO {

    @NotNull(message = "clienteId es obligatorio")
    private Long clienteId;

    @NotNull(message = "productoId es obligatorio")
    private Long productoId;

    @NotNull(message = "cantidad es obligatoria")
    @Min(value = 1, message = "cantidad debe ser mayor a cero")
    private Integer cantidad;

    @Pattern(regexp = "PENDIENTE|ATENDIDO|ANULADO", message = "estado debe ser PENDIENTE, ATENDIDO o ANULADO")
    private String estado;
}