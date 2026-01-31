package ec.edu.espe.auth_service.controller;

import ec.edu.espe.auth_service.dto.AuthResponse;
import ec.edu.espe.auth_service.dto.LoginRequest;
import ec.edu.espe.auth_service.dto.RefreshTokenRequest;
import ec.edu.espe.auth_service.dto.RegisterRequest;
import ec.edu.espe.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//Controlador REST para operaciones de autenticación
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //POST /api/auth/register - Registro de nuevos usuarios
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Solicitud de registro recibida para username: {}", request.getUsername());
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error en registro: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error interno en registro", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    //POST /api/auth/login - Login de usuarios
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Solicitud de login recibida para username: {}", request.getUsername());
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error en login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //POST /api/auth/refresh - Renovar access token con refresh token
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            logger.info("Solicitud de refresh token recibida");
            AuthResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Error en refresh token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error interno en refresh token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    //GET / - Endpoint de información del servicio
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "LogiFlow - Auth Service");
        info.put("version", "1.0.0");
        info.put("description", "Servicio de autenticación y autorización con JWT");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("POST /api/auth/register", "Registro de nuevos usuarios");
        endpoints.put("POST /api/auth/login", "Login de usuarios");
        endpoints.put("POST /api/auth/refresh", "Renovar access token");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }
}
