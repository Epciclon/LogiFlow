package ec.edu.espe.fleet_service.dto;

import ec.edu.espe.fleet_service.model.EstadoVehiculo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

//DTO para actualización de vehículos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehiculoRequest {

    @Size(max = 50)
    private String color;

    private BigDecimal capacidadCargaKg;

    private BigDecimal capacidadVolumenM3;

    private EstadoVehiculo estado;

    private LocalDate fechaUltimoMantenimiento;

    private LocalDate fechaProximoMantenimiento;

    @Min(value = 0)
    private Integer kilometraje;

    @Size(max = 50)
    private String numeroPolizaSeguro;

    private LocalDate fechaVencimientoSeguro;

    private LocalDate fechaVencimientoMatricula;

    private LocalDate fechaVencimientoRevisionTecnica;

    @Size(max = 500)
    private String observaciones;

    private Boolean activo;
}
