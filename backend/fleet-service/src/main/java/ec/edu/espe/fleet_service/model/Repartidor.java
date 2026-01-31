package ec.edu.espe.fleet_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//Entidad Repartidor - Personal de entregas
@Entity
@Table(name = "repartidor")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigoEmpleado;

    @Column(nullable = false, length = 200)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 500)
    private String direccion;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private LocalDate fechaContratacion;

    @ElementCollection(targetClass = TipoLicencia.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "repartidor_licencias", joinColumns = @JoinColumn(name = "repartidor_id"))
    @Column(name = "tipo_licencia")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<TipoLicencia> licenciasConducir = new HashSet<>();

    @Column(length = 30)
    private String numeroLicencia;

    @Column
    private LocalDate fechaVencimientoLicencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoRepartidor estado = EstadoRepartidor.DISPONIBLE;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    //Relación con vehículo asignado
    @OneToOne
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculoAsignado;

    //Estadísticas del repartidor
    @Column
    @Builder.Default
    private Integer entregasCompletadas = 0;

    @Column
    @Builder.Default
    private Integer entregasCanceladas = 0;

    @Column
    @Builder.Default
    private Double calificacionPromedio = 0.0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    //Método para verificar si el repartidor está disponible
    public Boolean estaDisponible() {
        return activo && estado == EstadoRepartidor.DISPONIBLE && vehiculoAsignado != null;
    }

    //Método para verificar si tiene licencia válida
    public Boolean tieneLicenciaValida() {
        return fechaVencimientoLicencia != null && 
               fechaVencimientoLicencia.isAfter(LocalDate.now());
    }
}
