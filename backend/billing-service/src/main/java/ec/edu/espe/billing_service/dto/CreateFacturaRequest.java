package ec.edu.espe.billing_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

//DTO para creación de facturas
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFacturaRequest {

    @NotNull(message = "El ID del pedido es obligatorio")
    private UUID pedidoId;

    @NotBlank(message = "El número de pedido es obligatorio")
    @Size(max = 50)
    private String numeroPedido;

    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID clienteId;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 200)
    private String clienteNombre;

    @NotBlank(message = "El tipo de entrega es obligatorio")
    @Size(max = 20)
    private String tipoEntrega;

    @DecimalMin(value = "0.0", message = "La distancia debe ser positiva")
    private BigDecimal distanciaKm;

    @DecimalMin(value = "0.0", message = "El peso debe ser positivo")
    private BigDecimal pesoKg;

    private String prioridad;

    private BigDecimal descuento;

    private LocalDate fechaVencimiento;

    @Size(max = 500)
    private String observaciones;
}
