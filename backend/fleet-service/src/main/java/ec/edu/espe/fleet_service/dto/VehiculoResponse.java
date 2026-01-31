package ec.edu.espe.fleet_service.dto;

import ec.edu.espe.fleet_service.model.EstadoVehiculo;
import ec.edu.espe.fleet_service.model.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

//DTO de respuesta para vehículos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponse {

    private UUID id;
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;
    private BigDecimal capacidadCargaKg;
    private BigDecimal capacidadVolumenM3;
    private EstadoVehiculo estado;
    
    private LocalDate fechaUltimoMantenimiento;
    private LocalDate fechaProximoMantenimiento;
    private Integer kilometraje;
    
    private String numeroPolizaSeguro;
    private LocalDate fechaVencimientoSeguro;
    private LocalDate fechaVencimientoMatricula;
    private LocalDate fechaVencimientoRevisionTecnica;
    
    private String observaciones;
    private Boolean activo;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
