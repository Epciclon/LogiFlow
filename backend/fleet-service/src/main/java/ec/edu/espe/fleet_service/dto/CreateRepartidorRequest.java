package ec.edu.espe.fleet_service.dto;

import ec.edu.espe.fleet_service.model.TipoLicencia;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

//DTO para creación de repartidores
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRepartidorRequest {

    @NotBlank(message = "El código de empleado es obligatorio")
    @Size(max = 20)
    private String codigoEmpleado;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 200)
    private String nombreCompleto;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(max = 20)
    private String cedula;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    private String telefono;

    @Size(max = 500)
    private String direccion;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La fecha de contratación es obligatoria")
    private LocalDate fechaContratacion;

    @NotEmpty(message = "Debe tener al menos una licencia de conducir")
    private Set<TipoLicencia> licenciasConducir;

    @Size(max = 30)
    private String numeroLicencia;

    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimientoLicencia;

    private UUID vehiculoId;

    @Size(max = 500)
    private String observaciones;
}
