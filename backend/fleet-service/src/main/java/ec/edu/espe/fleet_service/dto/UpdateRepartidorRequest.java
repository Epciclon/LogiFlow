package ec.edu.espe.fleet_service.dto;

import ec.edu.espe.fleet_service.model.EstadoRepartidor;
import ec.edu.espe.fleet_service.model.TipoLicencia;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

//DTO para actualización de repartidores
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRepartidorRequest {

    @Size(max = 200)
    private String nombreCompleto;

    @Email(message = "Email inválido")
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String telefono;

    @Size(max = 500)
    private String direccion;

    private Set<TipoLicencia> licenciasConducir;

    @Size(max = 30)
    private String numeroLicencia;

    private LocalDate fechaVencimientoLicencia;

    private EstadoRepartidor estado;

    private UUID vehiculoId;

    private Integer entregasCompletadas;

    private Integer entregasCanceladas;

    private Double calificacionPromedio;

    @Size(max = 500)
    private String observaciones;

    private Boolean activo;
}
