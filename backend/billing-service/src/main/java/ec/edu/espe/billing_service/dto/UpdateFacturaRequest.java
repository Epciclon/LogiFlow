package ec.edu.espe.billing_service.dto;

import ec.edu.espe.billing_service.model.EstadoFactura;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

//DTO para actualización de facturas
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFacturaRequest {

    private EstadoFactura estado;

    private BigDecimal descuento;

    private LocalDate fechaVencimiento;

    private LocalDate fechaPago;

    @Size(max = 50)
    private String metodoPago;

    @Size(max = 500)
    private String observaciones;

    private Boolean activo;
}
