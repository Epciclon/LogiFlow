package ec.edu.espe.fleet_service.controller;

import ec.edu.espe.fleet_service.dto.CreateRepartidorRequest;
import ec.edu.espe.fleet_service.dto.RepartidorResponse;
import ec.edu.espe.fleet_service.dto.UpdateRepartidorRequest;
import ec.edu.espe.fleet_service.model.EstadoRepartidor;
import ec.edu.espe.fleet_service.service.RepartidorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

//Controlador REST para gestión de repartidores
@RestController
@RequestMapping("/api/repartidores")
@RequiredArgsConstructor
public class RepartidorController {

    private final RepartidorService repartidorService;

    //Crear nuevo repartidor
    @PostMapping
    public ResponseEntity<RepartidorResponse> crearRepartidor(@Valid @RequestBody CreateRepartidorRequest request) {
        try {
            RepartidorResponse repartidor = repartidorService.crearRepartidor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(repartidor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al crear repartidor: " + e.getMessage());
        }
    }

    //Obtener todos los repartidores
    @GetMapping
    public ResponseEntity<List<RepartidorResponse>> obtenerTodosLosRepartidores() {
        List<RepartidorResponse> repartidores = repartidorService.obtenerTodosLosRepartidores();
        return ResponseEntity.ok(repartidores);
    }

    //Obtener repartidor por ID
    @GetMapping("/{id}")
    public ResponseEntity<RepartidorResponse> obtenerRepartidorPorId(@PathVariable UUID id) {
        try {
            RepartidorResponse repartidor = repartidorService.obtenerRepartidorPorId(id);
            return ResponseEntity.ok(repartidor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener repartidor por código
    @GetMapping("/codigo/{codigoEmpleado}")
    public ResponseEntity<RepartidorResponse> obtenerRepartidorPorCodigo(@PathVariable String codigoEmpleado) {
        try {
            RepartidorResponse repartidor = repartidorService.obtenerRepartidorPorCodigo(codigoEmpleado);
            return ResponseEntity.ok(repartidor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener repartidores por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RepartidorResponse>> obtenerRepartidoresPorEstado(@PathVariable EstadoRepartidor estado) {
        List<RepartidorResponse> repartidores = repartidorService.obtenerRepartidoresPorEstado(estado);
        return ResponseEntity.ok(repartidores);
    }

    //Obtener repartidores disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<RepartidorResponse>> obtenerRepartidoresDisponibles() {
        List<RepartidorResponse> repartidores = repartidorService.obtenerRepartidoresDisponibles();
        return ResponseEntity.ok(repartidores);
    }

    //Actualizar repartidor
    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponse> actualizarRepartidor(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRepartidorRequest request) {
        try {
            RepartidorResponse repartidor = repartidorService.actualizarRepartidor(id, request);
            return ResponseEntity.ok(repartidor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Cambiar estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<RepartidorResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        try {
            EstadoRepartidor nuevoEstado = EstadoRepartidor.valueOf(request.get("estado"));
            RepartidorResponse repartidor = repartidorService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(repartidor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Asignar vehículo
    @PatchMapping("/{repartidorId}/asignar-vehiculo/{vehiculoId}")
    public ResponseEntity<RepartidorResponse> asignarVehiculo(
            @PathVariable UUID repartidorId,
            @PathVariable UUID vehiculoId) {
        try {
            RepartidorResponse repartidor = repartidorService.asignarVehiculo(repartidorId, vehiculoId);
            return ResponseEntity.ok(repartidor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Eliminar repartidor (lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRepartidor(@PathVariable UUID id) {
        try {
            repartidorService.eliminarRepartidor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
