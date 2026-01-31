package ec.edu.espe.billing_service.dto;

import ec.edu.espe.billing_service.model.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

//DTO de respuesta para facturas
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {

    private UUID id;
    private String numeroFactura;
    
    private UUID pedidoId;
    private String numeroPedido;
    
    private UUID clienteId;
    private String clienteNombre;
    
    private String tipoEntrega;
    private BigDecimal distanciaKm;
    private BigDecimal pesoKg;
    
    private BigDecimal tarifaBase;
    private BigDecimal cargoDistancia;
    private BigDecimal cargoPeso;
    private BigDecimal recargoPrioridad;
    private BigDecimal descuento;
    
    private BigDecimal subtotal;
    private BigDecimal impuestoIVA;
    private BigDecimal total;
    
    private EstadoFactura estado;
    
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private LocalDate fechaPago;
    private String metodoPago;
    
    private String observaciones;
    private Boolean activo;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
