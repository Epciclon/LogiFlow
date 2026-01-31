package ec.edu.espe.fleet_service.controller;

import ec.edu.espe.fleet_service.dto.CreateVehiculoRequest;
import ec.edu.espe.fleet_service.dto.UpdateVehiculoRequest;
import ec.edu.espe.fleet_service.dto.VehiculoResponse;
import ec.edu.espe.fleet_service.model.EstadoVehiculo;
import ec.edu.espe.fleet_service.model.TipoVehiculo;
import ec.edu.espe.fleet_service.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

//Controlador REST para gestión de vehículos
@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    //Crear nuevo vehículo
    @PostMapping
    public ResponseEntity<VehiculoResponse> crearVehiculo(@Valid @RequestBody CreateVehiculoRequest request) {
        try {
            VehiculoResponse vehiculo = vehiculoService.crearVehiculo(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(vehiculo);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error al crear vehículo: " + e.getMessage());
        }
    }

    //Obtener todos los vehículos
    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> obtenerTodosLosVehiculos() {
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerTodosLosVehiculos();
        return ResponseEntity.ok(vehiculos);
    }

    //Obtener vehículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponse> obtenerVehiculoPorId(@PathVariable UUID id) {
        try {
            VehiculoResponse vehiculo = vehiculoService.obtenerVehiculoPorId(id);
            return ResponseEntity.ok(vehiculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener vehículo por placa
    @GetMapping("/placa/{placa}")
    public ResponseEntity<VehiculoResponse> obtenerVehiculoPorPlaca(@PathVariable String placa) {
        try {
            VehiculoResponse vehiculo = vehiculoService.obtenerVehiculoPorPlaca(placa);
            return ResponseEntity.ok(vehiculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Obtener vehículos por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<VehiculoResponse>> obtenerVehiculosPorTipo(@PathVariable TipoVehiculo tipo) {
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerVehiculosPorTipo(tipo);
        return ResponseEntity.ok(vehiculos);
    }

    //Obtener vehículos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<VehiculoResponse>> obtenerVehiculosPorEstado(@PathVariable EstadoVehiculo estado) {
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerVehiculosPorEstado(estado);
        return ResponseEntity.ok(vehiculos);
    }

    //Obtener vehículos disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<VehiculoResponse>> obtenerVehiculosDisponibles() {
        List<VehiculoResponse> vehiculos = vehiculoService.obtenerVehiculosDisponibles();
        return ResponseEntity.ok(vehiculos);
    }

    //Actualizar vehículo
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoResponse> actualizarVehiculo(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehiculoRequest request) {
        try {
            VehiculoResponse vehiculo = vehiculoService.actualizarVehiculo(id, request);
            return ResponseEntity.ok(vehiculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Cambiar estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<VehiculoResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        try {
            EstadoVehiculo nuevoEstado = EstadoVehiculo.valueOf(request.get("estado"));
            VehiculoResponse vehiculo = vehiculoService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(vehiculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Eliminar vehículo (lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable UUID id) {
        try {
            vehiculoService.eliminarVehiculo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
