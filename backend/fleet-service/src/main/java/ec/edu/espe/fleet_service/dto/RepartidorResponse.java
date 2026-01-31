package ec.edu.espe.fleet_service.dto;

import ec.edu.espe.fleet_service.model.EstadoRepartidor;
import ec.edu.espe.fleet_service.model.TipoLicencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

//DTO de respuesta para repartidores
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorResponse {

    private UUID id;
    private String codigoEmpleado;
    private String nombreCompleto;
    private String cedula;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private LocalDate fechaContratacion;
    private Set<TipoLicencia> licenciasConducir;
    private String numeroLicencia;
    private LocalDate fechaVencimientoLicencia;
    private EstadoRepartidor estado;
    private String observaciones;
    private Boolean activo;
    
    private UUID vehiculoId;
    private String vehiculoPlaca;
    
    private Integer entregasCompletadas;
    private Integer entregasCanceladas;
    private Double calificacionPromedio;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
