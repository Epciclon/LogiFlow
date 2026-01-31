package ec.edu.espe.auth_service.dto;

import ec.edu.espe.auth_service.model.RolEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

//DTO para respuesta de autenticación con tokens JWT
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String username;
    private String email;
    private Set<RolEnum> roles;
}
