package ec.edu.espe.fleet_service.dto;

import ec.edu.espe.fleet_service.model.TipoVehiculo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

//DTO para creación de vehículos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehiculoRequest {

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 10)
    private String placa;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 100)
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1990, message = "El año debe ser mayor a 1990")
    @Max(value = 2030, message = "El año no puede ser mayor a 2030")
    private Integer anio;

    @Size(max = 50)
    private String color;

    @DecimalMin(value = "0.0", message = "La capacidad de carga debe ser positiva")
    private BigDecimal capacidadCargaKg;

    @DecimalMin(value = "0.0", message = "La capacidad de volumen debe ser positiva")
    private BigDecimal capacidadVolumenM3;

    private LocalDate fechaUltimoMantenimiento;

    private LocalDate fechaProximoMantenimiento;

    @Min(value = 0, message = "El kilometraje debe ser positivo")
    private Integer kilometraje;

    @Size(max = 50)
    private String numeroPolizaSeguro;

    @Future(message = "La fecha de vencimiento del seguro debe ser futura")
    private LocalDate fechaVencimientoSeguro;

    @Future(message = "La fecha de vencimiento de matrícula debe ser futura")
    private LocalDate fechaVencimientoMatricula;

    @Future(message = "La fecha de vencimiento de revisión técnica debe ser futura")
    private LocalDate fechaVencimientoRevisionTecnica;

    @Size(max = 500)
    private String observaciones;
}
