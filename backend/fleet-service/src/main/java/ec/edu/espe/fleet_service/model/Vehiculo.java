package ec.edu.espe.fleet_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

//Entidad Vehículo - Flota de transporte
@Entity
@Table(name = "vehiculo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVehiculo tipoVehiculo;

    @Column(nullable = false, length = 100)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(nullable = false)
    private Integer anio;

    @Column(length = 50)
    private String color;

    @Column(precision = 10, scale = 2)
    private BigDecimal capacidadCargaKg;

    @Column(precision = 10, scale = 2)
    private BigDecimal capacidadVolumenM3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;

    //Información de mantenimiento
    @Column
    private LocalDate fechaUltimoMantenimiento;

    @Column
    private LocalDate fechaProximoMantenimiento;

    @Column
    @Builder.Default
    private Integer kilometraje = 0;

    //Información de seguro y documentación
    @Column(length = 50)
    private String numeroPolizaSeguro;

    @Column
    private LocalDate fechaVencimientoSeguro;

    @Column
    private LocalDate fechaVencimientoMatricula;

    @Column
    private LocalDate fechaVencimientoRevisionTecnica;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    //Método para verificar si el vehículo está operativo
    public Boolean estaOperativo() {
        return activo && 
               estado == EstadoVehiculo.DISPONIBLE &&
               tieneDocumentosVigentes();
    }

    //Método para verificar documentos vigentes
    public Boolean tieneDocumentosVigentes() {
        LocalDate hoy = LocalDate.now();
        return (fechaVencimientoSeguro == null || fechaVencimientoSeguro.isAfter(hoy)) &&
               (fechaVencimientoMatricula == null || fechaVencimientoMatricula.isAfter(hoy)) &&
               (fechaVencimientoRevisionTecnica == null || fechaVencimientoRevisionTecnica.isAfter(hoy));
    }

    //Método para verificar si requiere mantenimiento
    public Boolean requiereMantenimiento() {
        return fechaProximoMantenimiento != null && 
               fechaProximoMantenimiento.isBefore(LocalDate.now().plusDays(7));
    }
}
