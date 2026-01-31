package ec.edu.espe.pedido_service.dto;

import ec.edu.espe.pedido_service.model.EstadoPedido;
import ec.edu.espe.pedido_service.model.PrioridadPedido;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//DTO para actualización parcial de pedidos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePedidoRequest {

    private EstadoPedido estado;

    private UUID repartidorId;

    @Size(max = 200)
    private String repartidorNombre;

    private PrioridadPedido prioridad;

    private BigDecimal tarifaBase;

    private BigDecimal tarifaTotal;

    private LocalDateTime fechaEstimadaEntrega;

    private LocalDateTime fechaEntregaReal;

    @Size(max = 1000)
    private String observaciones;

    private Boolean activo;
}
