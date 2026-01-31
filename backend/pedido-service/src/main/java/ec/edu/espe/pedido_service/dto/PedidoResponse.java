package ec.edu.espe.pedido_service.dto;

import ec.edu.espe.pedido_service.model.EstadoPedido;
import ec.edu.espe.pedido_service.model.PrioridadPedido;
import ec.edu.espe.pedido_service.model.TipoEntrega;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//DTO de respuesta para pedidos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private UUID id;
    private String numeroPedido;

    private UUID clienteId;
    private String clienteNombre;

    private UUID repartidorId;
    private String repartidorNombre;

    private TipoEntrega tipoEntrega;
    private EstadoPedido estado;
    private PrioridadPedido prioridad;

    private String direccionOrigen;
    private Double latitudOrigen;
    private Double longitudOrigen;

    private String direccionDestino;
    private Double latitudDestino;
    private Double longitudDestino;

    private String descripcionPaquete;
    private BigDecimal pesoKg;
    private String dimensiones;

    private BigDecimal tarifaBase;
    private BigDecimal tarifaTotal;

    private LocalDateTime fechaEstimadaEntrega;
    private LocalDateTime fechaEntregaReal;

    private String observaciones;

    private Boolean activo;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
