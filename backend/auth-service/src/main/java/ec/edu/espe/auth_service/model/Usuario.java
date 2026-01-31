package ec.edu.espe.auth_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//Entidad Usuario para autenticación y autorización
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 200)
    private String nombreCompleto;

    @Column(length = 20)
    private String telefono;

    @ElementCollection(targetClass = RolEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Set<RolEnum> roles;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean cuentaBloqueada = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    private LocalDateTime ultimoAcceso;

    //Métodos de utilidad para gestionar roles
    public void addRol(RolEnum rol) {
        this.roles.add(rol);
    }

    public void removeRol(RolEnum rol) {
        this.roles.remove(rol);
    }

    public boolean hasRol(RolEnum rol) {
        return this.roles.contains(rol);
    }
}
