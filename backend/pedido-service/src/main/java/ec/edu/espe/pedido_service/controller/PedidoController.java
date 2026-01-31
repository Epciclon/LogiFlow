package ec.edu.espe.pedido_service.controller;

import ec.edu.espe.pedido_service.dto.CreatePedidoRequest;
import ec.edu.espe.pedido_service.dto.PedidoResponse;
import ec.edu.espe.pedido_service.dto.UpdatePedidoRequest;
import ec.edu.espe.pedido_service.model.EstadoPedido;
import ec.edu.espe.pedido_service.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

//Controlador REST para gestión de pedidos
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    //Crear nuevo pedido
    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody CreatePedidoRequest request) {
        try {
            PedidoResponse pedido = pedidoService.crearPedido(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al crear pedido: " + e.getMessage());
        }
    }

    //Obtener todos los pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> obtenerTodosLosPedidos() {
        List<PedidoResponse> pedidos = pedidoService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    //Obtener pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorId(@PathVariable UUID id) {
        try {
            PedidoResponse pedido = pedidoService.obtenerPedidoPorId(id);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener pedido por número
    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorNumero(@PathVariable String numeroPedido) {
        try {
            PedidoResponse pedido = pedidoService.obtenerPedidoPorNumero(numeroPedido);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener pedidos por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosPorCliente(@PathVariable UUID clienteId) {
        List<PedidoResponse> pedidos = pedidoService.obtenerPedidosPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    //Obtener pedidos por repartidor
    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosPorRepartidor(@PathVariable UUID repartidorId) {
        List<PedidoResponse> pedidos = pedidoService.obtenerPedidosPorRepartidor(repartidorId);
        return ResponseEntity.ok(pedidos);
    }

    //Obtener pedidos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosPorEstado(@PathVariable EstadoPedido estado) {
        List<PedidoResponse> pedidos = pedidoService.obtenerPedidosPorEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    //Actualizar pedido
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> actualizarPedido(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePedidoRequest request) {
        try {
            PedidoResponse pedido = pedidoService.actualizarPedido(id, request);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Asignar repartidor
    @PatchMapping("/{pedidoId}/asignar-repartidor")
    public ResponseEntity<PedidoResponse> asignarRepartidor(
            @PathVariable UUID pedidoId,
            @RequestBody Map<String, Object> request) {
        try {
            UUID repartidorId = UUID.fromString((String) request.get("repartidorId"));
            String repartidorNombre = (String) request.get("repartidorNombre");
            PedidoResponse pedido = pedidoService.asignarRepartidor(pedidoId, repartidorId, repartidorNombre);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Cambiar estado
    @PatchMapping("/{pedidoId}/estado")
    public ResponseEntity<PedidoResponse> cambiarEstado(
            @PathVariable UUID pedidoId,
            @RequestBody Map<String, String> request) {
        try {
            EstadoPedido nuevoEstado = EstadoPedido.valueOf(request.get("estado"));
            PedidoResponse pedido = pedidoService.cambiarEstado(pedidoId, nuevoEstado);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Cancelar pedido
    @PatchMapping("/{pedidoId}/cancelar")
    public ResponseEntity<PedidoResponse> cancelarPedido(
            @PathVariable UUID pedidoId,
            @RequestBody Map<String, String> request) {
        try {
            String motivo = request.getOrDefault("motivo", "Sin motivo especificado");
            PedidoResponse pedido = pedidoService.cancelarPedido(pedidoId, motivo);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Eliminar pedido (lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable UUID id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
