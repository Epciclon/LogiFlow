package ec.edu.espe.billing_service.controller;

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
        info.put("servicio", "LogiFlow - Billing Service");
        info.put("version", "1.0.0");
        info.put("descripcion", "Microservicio para cálculo de tarifas y generación de facturas");
        info.put("puerto", 8085);
        info.put("endpoints", Map.ofEntries(
            Map.entry("crear", "POST /api/facturas"),
            Map.entry("listar", "GET /api/facturas"),
            Map.entry("obtenerPorId", "GET /api/facturas/{id}"),
            Map.entry("obtenerPorNumero", "GET /api/facturas/numero/{numeroFactura}"),
            Map.entry("obtenerPorPedido", "GET /api/facturas/pedido/{pedidoId}"),
            Map.entry("obtenerPorCliente", "GET /api/facturas/cliente/{clienteId}"),
            Map.entry("obtenerPorEstado", "GET /api/facturas/estado/{estado}"),
            Map.entry("actualizar", "PUT /api/facturas/{id}"),
            Map.entry("cambiarEstado", "PATCH /api/facturas/{id}/estado"),
            Map.entry("registrarPago", "PATCH /api/facturas/{id}/pagar"),
            Map.entry("eliminar", "DELETE /api/facturas/{id}")
        ));
        info.put("tarifas", Map.of(
            "urbana", "Base: $3.00 + $0.50/km + $0.20/kg",
            "intermunicipal", "Base: $10.00 + $0.80/km + $0.30/kg",
            "nacional", "Base: $50.00 + $1.20/km + $0.50/kg",
            "recargos", "Urgente: x2.0, Alta: x1.5, Normal/Baja: x1.0",
            "impuestos", "IVA: 15%"
        ));
        return ResponseEntity.ok(info);
    }
}
