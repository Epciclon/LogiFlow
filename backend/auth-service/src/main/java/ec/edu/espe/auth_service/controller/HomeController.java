package ec.edu.espe.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//Controlador home para mostrar información del servicio
@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "LogiFlow - Auth Service");
        info.put("version", "1.0.0");
        info.put("description", "Servicio de autenticación y autorización con JWT");
        info.put("port", "8082");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("POST /api/auth/register", "Registro de nuevos usuarios (rol CLIENTE por defecto)");
        endpoints.put("POST /api/auth/login", "Login de usuarios existentes");
        endpoints.put("POST /api/auth/refresh", "Renovar access token usando refresh token");
        
        info.put("endpoints", endpoints);
        
        Map<String, String> roles = new HashMap<>();
        roles.put("CLIENTE", "Clientes que solicitan entregas");
        roles.put("REPARTIDOR", "Repartidores (motorizado, vehículo liviano, camión)");
        roles.put("SUPERVISOR", "Supervisores de operaciones");
        roles.put("GERENTE", "Gerentes de la empresa");
        roles.put("ADMINISTRADOR", "Administradores del sistema");
        
        info.put("roles", roles);
        
        return ResponseEntity.ok(info);
    }
}
