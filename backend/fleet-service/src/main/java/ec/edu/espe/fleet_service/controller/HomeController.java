package ec.edu.espe.fleet_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//Controlador de inicio
@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("servicio", "LogiFlow - Fleet Service");
        info.put("version", "1.0.0");
        info.put("descripcion", "Microservicio para gestión de repartidores y vehículos de la flota");
        info.put("puerto", 8084);
        info.put("endpoints", Map.of(
            "repartidores", Map.of(
                "crear", "POST /api/repartidores",
                "listar", "GET /api/repartidores",
                "obtenerPorId", "GET /api/repartidores/{id}",
                "obtenerPorCodigo", "GET /api/repartidores/codigo/{codigo}",
                "obtenerPorEstado", "GET /api/repartidores/estado/{estado}",
                "disponibles", "GET /api/repartidores/disponibles",
                "actualizar", "PUT /api/repartidores/{id}",
                "cambiarEstado", "PATCH /api/repartidores/{id}/estado",
                "asignarVehiculo", "PATCH /api/repartidores/{repartidorId}/asignar-vehiculo/{vehiculoId}",
                "eliminar", "DELETE /api/repartidores/{id}"
            ),
            "vehiculos", Map.of(
                "crear", "POST /api/vehiculos",
                "listar", "GET /api/vehiculos",
                "obtenerPorId", "GET /api/vehiculos/{id}",
                "obtenerPorPlaca", "GET /api/vehiculos/placa/{placa}",
                "obtenerPorTipo", "GET /api/vehiculos/tipo/{tipo}",
                "obtenerPorEstado", "GET /api/vehiculos/estado/{estado}",
                "disponibles", "GET /api/vehiculos/disponibles",
                "actualizar", "PUT /api/vehiculos/{id}",
                "cambiarEstado", "PATCH /api/vehiculos/{id}/estado",
                "eliminar", "DELETE /api/vehiculos/{id}"
            )
        ));
        return ResponseEntity.ok(info);
    }
}
