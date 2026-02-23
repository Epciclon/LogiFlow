package ec.edu.espe.pedido_service.dto;

import ec.edu.espe.pedido_service.model.PrioridadPedido;
import ec.edu.espe.pedido_service.model.TipoEntrega;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//DTO para creación de pedidos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePedidoRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID clienteId;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 200)
    private String clienteNombre;

    @NotNull(message = "El tipo de entrega es obligatorio")
    private TipoEntrega tipoEntrega;

    @Builder.Default
    private PrioridadPedido prioridad = PrioridadPedido.NORMAL;

    @NotBlank(message = "La dirección de origen es obligatoria")
    @Size(max = 500)
    private String direccionOrigen;

    private Double latitudOrigen;

    private Double longitudOrigen;

    @NotBlank(message = "La dirección de destino es obligatoria")
    @Size(max = 500)
    private String direccionDestino;

    private Double latitudDestino;

    private Double longitudDestino;

<<<<<<< HEAD
    @NotBlank(message = "El código postal de la zona es obligatorio")
    @Size(max = 10)
    private String zonaId;

=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
    @NotBlank(message = "La descripción del paquete es obligatoria")
    @Size(max = 500)
    private String descripcionPaquete;

    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
    private BigDecimal pesoKg;

    @Size(max = 100)
    private String dimensiones;

    private LocalDateTime fechaEstimadaEntrega;

    @Size(max = 1000)
    private String observaciones;
}
