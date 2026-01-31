package ec.edu.espe.auth_service.service;

import ec.edu.espe.auth_service.dto.AuthResponse;
import ec.edu.espe.auth_service.dto.LoginRequest;
import ec.edu.espe.auth_service.dto.RegisterRequest;
import ec.edu.espe.auth_service.model.RolEnum;
import ec.edu.espe.auth_service.model.Usuario;
import ec.edu.espe.auth_service.repository.UsuarioRepository;
import ec.edu.espe.auth_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private Usuario usuario;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@logiflow.com")
                .password("password123")
                .nombreCompleto("Test User")
                .telefono("0991234567")
                .build();

        usuario = Usuario.builder()
                .username("testuser")
                .email("test@logiflow.com")
                .password("$2a$10$encodedPassword")
                .nombreCompleto("Test User")
                .activo(true)
                .cuentaBloqueada(false)
                .intentosFallidos(0)
                .roles(new HashSet<>())  // Inicializar roles
                .build();
        usuario.addRol(RolEnum.CLIENTE);

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Test
    void register_ConDatosValidos_DebeCrearUsuario() {
        // Arrange
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtUtil.generateAccessToken(anyString(), any())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refreshToken");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("testuser", response.getUsername());
        verify(usuarioRepository, times(2)).save(any(Usuario.class)); // Guardado en dos fases
    }

    @Test
    void register_ConUsernameDuplicado_DebeLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(registerRequest)
        );
        assertTrue(exception.getMessage().contains("username ya existe"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_ConEmailDuplicado_DebeLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail("test@logiflow.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(registerRequest)
        );
        assertTrue(exception.getMessage().contains("email ya existe"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void login_ConCredencialesValidas_DebeRetornarToken() {
        // Arrange
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtUtil.generateAccessToken(anyString(), any())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refreshToken");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("testuser", response.getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class)); // Reset intentos fallidos
    }

    @Test
    void login_ConCredencialesInvalidas_DebeLanzar401() {
        // Arrange
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedPassword")).thenReturn(false);

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(LoginRequest.builder()
                        .username("testuser")
                        .password("wrongpassword")
                        .build())
        );
        assertTrue(exception.getMessage().contains("Credenciales inválidas"));
    }

    @Test
    void login_ConCuentaBloqueada_DebeLanzarExcepcion() {
        // Arrange
        usuario.setCuentaBloqueada(true);
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThrows(
                org.springframework.security.authentication.LockedException.class,
                () -> authService.login(loginRequest)
        );
    }

    @Test
    void login_ConCuentaInactiva_DebeLanzarExcepcion() {
        // Arrange
        usuario.setActivo(false);
        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(loginRequest)
        );
    }
}
