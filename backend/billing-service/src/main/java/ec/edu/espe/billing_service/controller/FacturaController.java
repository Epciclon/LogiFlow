package ec.edu.espe.billing_service.controller;

import ec.edu.espe.billing_service.dto.CreateFacturaRequest;
import ec.edu.espe.billing_service.dto.FacturaResponse;
import ec.edu.espe.billing_service.dto.UpdateFacturaRequest;
import ec.edu.espe.billing_service.model.EstadoFactura;
import ec.edu.espe.billing_service.service.FacturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

//Controlador REST para gestión de facturas
@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    //Crear nueva factura
    @PostMapping
    public ResponseEntity<FacturaResponse> crearFactura(@Valid @RequestBody CreateFacturaRequest request) {
        try {
            FacturaResponse factura = facturaService.crearFactura(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(factura);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al crear factura: " + e.getMessage());
        }
    }

    //Obtener todas las facturas
    @GetMapping
    public ResponseEntity<List<FacturaResponse>> obtenerTodasLasFacturas() {
        List<FacturaResponse> facturas = facturaService.obtenerTodasLasFacturas();
        return ResponseEntity.ok(facturas);
    }

    //Obtener factura por ID
    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponse> obtenerFacturaPorId(@PathVariable UUID id) {
        try {
            FacturaResponse factura = facturaService.obtenerFacturaPorId(id);
            return ResponseEntity.ok(factura);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener factura por número
    @GetMapping("/numero/{numeroFactura}")
    public ResponseEntity<FacturaResponse> obtenerFacturaPorNumero(@PathVariable String numeroFactura) {
        try {
            FacturaResponse factura = facturaService.obtenerFacturaPorNumero(numeroFactura);
            return ResponseEntity.ok(factura);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener factura por pedido
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<FacturaResponse> obtenerFacturaPorPedido(@PathVariable UUID pedidoId) {
        try {
            FacturaResponse factura = facturaService.obtenerFacturaPorPedido(pedidoId);
            return ResponseEntity.ok(factura);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener facturas por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<FacturaResponse>> obtenerFacturasPorCliente(@PathVariable UUID clienteId) {
        List<FacturaResponse> facturas = facturaService.obtenerFacturasPorCliente(clienteId);
        return ResponseEntity.ok(facturas);
    }

    //Obtener facturas por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<FacturaResponse>> obtenerFacturasPorEstado(@PathVariable EstadoFactura estado) {
        List<FacturaResponse> facturas = facturaService.obtenerFacturasPorEstado(estado);
        return ResponseEntity.ok(facturas);
    }

    //Actualizar factura
    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponse> actualizarFactura(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFacturaRequest request) {
        try {
            FacturaResponse factura = facturaService.actualizarFactura(id, request);
            return ResponseEntity.ok(factura);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Cambiar estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<FacturaResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        try {
            EstadoFactura nuevoEstado = EstadoFactura.valueOf(request.get("estado"));
            FacturaResponse factura = facturaService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(factura);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Registrar pago
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<FacturaResponse> registrarPago(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        try {
            String metodoPago = request.getOrDefault("metodoPago", "Efectivo");
            FacturaResponse factura = facturaService.registrarPago(id, metodoPago);
            return ResponseEntity.ok(factura);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Eliminar factura (lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable UUID id) {
        try {
            facturaService.eliminarFactura(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
