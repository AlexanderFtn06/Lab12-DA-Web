package com.tecsup.services;

import com.tecsup.client.ClienteClient;
import com.tecsup.client.ProductoClient;
import com.tecsup.dto.ClienteDTO;
import com.tecsup.dto.PedidoDTO;
import com.tecsup.dto.PedidoDetalleDTO;
import com.tecsup.dto.ProductoDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Pedido;
import com.tecsup.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repo;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;

    public List<Pedido> listar() {
        return repo.findAll();
    }

    public Pedido obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }

    public PedidoDetalleDTO obtenerDetalle(Long id) {
        Pedido pedido = obtener(id);

        ClienteDTO cliente = clienteClient.obtenerCliente(pedido.getClienteId());
        ProductoDTO producto = productoClient.obtenerProducto(pedido.getProductoId());

        PedidoDetalleDTO detalle = new PedidoDetalleDTO();
        detalle.setId(pedido.getId());
        detalle.setCantidad(pedido.getCantidad());
        detalle.setCliente(cliente);
        detalle.setProducto(producto);
        return detalle;
    }

    public Pedido guardar(PedidoDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setClienteId(dto.getClienteId());
        pedido.setProductoId(dto.getProductoId());
        pedido.setCantidad(dto.getCantidad());
        pedido.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        return repo.save(pedido);
    }

    public Pedido actualizar(Long id, PedidoDTO dto) {
        Pedido existente = obtener(id);
        existente.setClienteId(dto.getClienteId());
        existente.setProductoId(dto.getProductoId());
        existente.setCantidad(dto.getCantidad());
        if (dto.getEstado() != null) {
            existente.setEstado(dto.getEstado());
        }
        return repo.save(existente);
    }

    public Pedido anular(Long id) {
        Pedido pedido = obtener(id);
        pedido.setEstado("ANULADO");
        return repo.save(pedido);
    }
}