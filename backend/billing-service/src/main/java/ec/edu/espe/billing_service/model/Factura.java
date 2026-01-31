package ec.edu.espe.billing_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

//Entidad Factura - Facturación de servicios de entrega
@Entity
@Table(name = "factura")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroFactura;

    //Referencias a otros servicios
    @Column(nullable = false)
    private UUID pedidoId;

    @Column(nullable = false, length = 50)
    private String numeroPedido;

    @Column(nullable = false)
    private UUID clienteId;

    @Column(nullable = false, length = 200)
    private String clienteNombre;

    //Detalles de facturación
    @Column(nullable = false, length = 20)
    private String tipoEntrega;

    @Column(precision = 10, scale = 2)
    private BigDecimal distanciaKm;

    @Column(precision = 10, scale = 2)
    private BigDecimal pesoKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaBase;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cargoDistancia = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal cargoPeso = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal recargoPrioridad = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal impuestoIVA = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoFactura estado = EstadoFactura.BORRADOR;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column
    private LocalDate fechaVencimiento;

    @Column
    private LocalDate fechaPago;

    @Column(length = 50)
    private String metodoPago;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    //Método para calcular el total
    public void calcularTotal() {
        this.subtotal = tarifaBase
                .add(cargoDistancia)
                .add(cargoPeso)
                .add(recargoPrioridad)
                .subtract(descuento);
        
        this.impuestoIVA = subtotal.multiply(new BigDecimal("0.15")); //IVA 15%
        this.total = subtotal.add(impuestoIVA);
    }

    //Método para verificar si está vencida
    public Boolean estaVencida() {
        return estado == EstadoFactura.PENDIENTE && 
               fechaVencimiento != null && 
               fechaVencimiento.isBefore(LocalDate.now());
    }
}
