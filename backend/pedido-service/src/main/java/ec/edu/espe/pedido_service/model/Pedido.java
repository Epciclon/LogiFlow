package ec.edu.espe.pedido_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

//Entidad Pedido para gestión de entregas
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true, length = 50)
    private String numeroPedido;

    //Cliente que solicita la entrega (referencia al auth-service)
    @Column(nullable = false)
    private UUID clienteId;

    @Column(nullable = false, length = 200)
    private String clienteNombre;

    //Repartidor asignado (referencia al fleet-service)
    private UUID repartidorId;

    private String repartidorNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEntrega tipoEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.RECIBIDO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PrioridadPedido prioridad = PrioridadPedido.NORMAL;

    //Dirección de origen
    @Column(nullable = false, length = 500)
    private String direccionOrigen;

    private Double latitudOrigen;

    private Double longitudOrigen;

    //Dirección de destino
    @Column(nullable = false, length = 500)
    private String direccionDestino;

    private Double latitudDestino;

    private Double longitudDestino;

    //Detalles del paquete
    @Column(nullable = false, length = 500)
    private String descripcionPaquete;

    @Column(precision = 10, scale = 2)
    private BigDecimal pesoKg;

    @Column(length = 100)
    private String dimensiones;

    //Tarifas y costos
    @Column(precision = 10, scale = 2)
    private BigDecimal tarifaBase;

    @Column(precision = 10, scale = 2)
    private BigDecimal tarifaTotal;

    //Fechas y tiempos
    private LocalDateTime fechaEstimadaEntrega;

    private LocalDateTime fechaEntregaReal;

    @Column(length = 1000)
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

    //Validación de cobertura geográfica según tipo de entrega
    public boolean validarCobertura() {
        if (latitudOrigen == null || longitudOrigen == null || 
            latitudDestino == null || longitudDestino == null) {
            return false;
        }

        double distanciaKm = calcularDistancia(latitudOrigen, longitudOrigen, 
                                                latitudDestino, longitudDestino);

        return switch (tipoEntrega) {
            case URBANA_RAPIDA -> distanciaKm <= 20;  //Máximo 20 km en ciudad
            case INTERMUNICIPAL -> distanciaKm <= 150; //Máximo 150 km dentro de provincia
            case NACIONAL -> true;  //Sin restricción de distancia
        };
    }

    //Cálculo de distancia usando fórmula de Haversine
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; //Radio de la Tierra en km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
