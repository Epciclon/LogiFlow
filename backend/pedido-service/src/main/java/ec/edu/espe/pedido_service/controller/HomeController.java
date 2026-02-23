package ec.edu.espe.pedido_service.controller;

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
        info.put("servicio", "LogiFlow - Pedido Service");
        info.put("version", "1.0.0");
        info.put("descripcion", "Microservicio para gestión de pedidos y seguimiento de entregas");
        info.put("puerto", 8083);
        info.put("endpoints", Map.ofEntries(
            Map.entry("crear", "POST /api/pedidos"),
<<<<<<< HEAD
            Map.entry("listar", "GET /api/pedidos [?zonaId=XXX&estado=XXX]"),
=======
            Map.entry("listar", "GET /api/pedidos"),
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
            Map.entry("obtenerPorId", "GET /api/pedidos/{id}"),
            Map.entry("obtenerPorNumero", "GET /api/pedidos/numero/{numeroPedido}"),
            Map.entry("obtenerPorCliente", "GET /api/pedidos/cliente/{clienteId}"),
            Map.entry("obtenerPorRepartidor", "GET /api/pedidos/repartidor/{repartidorId}"),
            Map.entry("obtenerPorEstado", "GET /api/pedidos/estado/{estado}"),
            Map.entry("actualizar", "PUT /api/pedidos/{id}"),
            Map.entry("asignarRepartidor", "PATCH /api/pedidos/{id}/asignar-repartidor"),
            Map.entry("cambiarEstado", "PATCH /api/pedidos/{id}/estado"),
            Map.entry("cancelar", "PATCH /api/pedidos/{id}/cancelar"),
            Map.entry("eliminar", "DELETE /api/pedidos/{id}")
        ));
<<<<<<< HEAD
        info.put("parametrosOpcionales", Map.of(
            "zonaId", "Código postal (ej: 170150 para Quito Norte, 180101 para Ambato)",
            "estado", "RECIBIDO, EN_PREPARACION, ASIGNADO, EN_RUTA, ENTREGADO, CANCELADO, DEVUELTO"
        ));
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return ResponseEntity.ok(info);
    }
}
