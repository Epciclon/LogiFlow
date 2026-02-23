package ec.edu.espe.auth_service.service;

import ec.edu.espe.auth_service.dto.AuthResponse;
import ec.edu.espe.auth_service.dto.LoginRequest;
import ec.edu.espe.auth_service.dto.RefreshTokenRequest;
import ec.edu.espe.auth_service.dto.RegisterRequest;
import ec.edu.espe.auth_service.model.RolEnum;
import ec.edu.espe.auth_service.model.Usuario;
import ec.edu.espe.auth_service.repository.UsuarioRepository;
import ec.edu.espe.auth_service.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//Servicio de autenticación con lógica de registro, login y refresh de tokens
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_INTENTOS_FALLIDOS = 3;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public AuthService(UsuarioRepository usuarioRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    //Registra un nuevo usuario con rol CLIENTE por defecto
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("Iniciando registro de usuario: {}", request.getUsername());

        //Validar que el username no exista
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El username ya existe: " + request.getUsername());
        }

        //Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya existe: " + request.getEmail());
        }

<<<<<<< HEAD
        //Crear nuevo usuario con constructor - inicializar roles desde el principio
=======
        //Crear nuevo usuario con constructor - dejar roles e ID null inicialmente
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        Usuario usuario = new Usuario();
        usuario.setId(null); // Forzar ID a null para garantizar que Hibernate lo trate como transient
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setTelefono(request.getTelefono());
        usuario.setActivo(true);
        usuario.setCuentaBloqueada(false);
        usuario.setIntentosFallidos(0);
<<<<<<< HEAD
        
        //Inicializar roles con CLIENTE desde el principio
        Set<RolEnum> roles = new HashSet<>();
        roles.add(RolEnum.CLIENTE);
        usuario.setRoles(roles);

        //Guardar usuario con roles ya asignados
        Usuario savedUsuario = usuarioRepository.save(usuario);
=======
        usuario.setRoles(null); // Dejar roles como null inicialmente

        //Guardar usando repository (debería hacer persist porque ID es null)
        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        //AHORA inicializar roles y agregar CLIENTE en la entidad ya persistida
        if (savedUsuario.getRoles() == null) {
            savedUsuario.setRoles(new HashSet<>());
        }
        savedUsuario.getRoles().add(RolEnum.CLIENTE);
        
        //Guardar nuevamente para actualizar roles
        savedUsuario = usuarioRepository.save(savedUsuario);
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        logger.info("Usuario registrado exitosamente: {}", savedUsuario.getUsername());

        //Generar tokens JWT
        return generateAuthResponse(savedUsuario);
    }

    //Realiza login con validación de credenciales y generación de tokens
    @Transactional
    public AuthResponse login(LoginRequest request) {
        logger.info("Intento de login para usuario: {}", request.getUsername());

        //Buscar usuario
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        //Verificar si la cuenta está bloqueada
        if (usuario.getCuentaBloqueada()) {
            logger.warn("Intento de login en cuenta bloqueada: {}", request.getUsername());
            throw new LockedException("Cuenta bloqueada debido a múltiples intentos fallidos");
        }

        //Verificar si la cuenta está activa
        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("Cuenta inactiva");
        }

        //Verificar password
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            handleFailedLogin(usuario);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        //Login exitoso - resetear intentos fallidos
        usuario.setIntentosFallidos(0);
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        logger.info("Login exitoso para usuario: {}", usuario.getUsername());

        //Generar tokens JWT
        return generateAuthResponse(usuario);
    }

    //Genera un nuevo access token usando el refresh token
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        logger.info("Procesando refresh token");

        String refreshToken = request.getRefreshToken();

        //Validar refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token inválido o expirado");
        }

        //Extraer username del refresh token
        String username = jwtUtil.extractUsername(refreshToken);

        //Buscar usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        //Verificar que la cuenta esté activa y no bloqueada
        if (!usuario.getActivo() || usuario.getCuentaBloqueada()) {
            throw new IllegalArgumentException("Cuenta inactiva o bloqueada");
        }

        logger.info("Refresh token exitoso para usuario: {}", username);

        //Generar nuevos tokens
        return generateAuthResponse(usuario);
    }

    //Maneja intentos fallidos de login
    private void handleFailedLogin(Usuario usuario) {
        int intentos = usuario.getIntentosFallidos() + 1;
        usuario.setIntentosFallidos(intentos);

        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            usuario.setCuentaBloqueada(true);
            logger.warn("Cuenta bloqueada por múltiples intentos fallidos: {}", usuario.getUsername());
        }

        usuarioRepository.save(usuario);
        logger.warn("Intento fallido de login para usuario: {} - Intentos: {}", usuario.getUsername(), intentos);
    }

    //Genera la respuesta de autenticación con tokens JWT
    private AuthResponse generateAuthResponse(Usuario usuario) {
        //Preparar claims para el JWT
        Map<String, Object> claims = new HashMap<>();
<<<<<<< HEAD
        
        //Verificar que roles no sea null antes de hacer stream
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            logger.warn("Usuario {} no tiene roles asignados, asignando CLIENTE por defecto", usuario.getUsername());
            usuario.setRoles(new HashSet<>());
            usuario.getRoles().add(RolEnum.CLIENTE);
        }
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        claims.put("roles", usuario.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        claims.put("email", usuario.getEmail());
        claims.put("nombreCompleto", usuario.getNombreCompleto());

        //Generar tokens
        String accessToken = jwtUtil.generateAccessToken(usuario.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(usuario.getRoles())
                .build();
    }
}
