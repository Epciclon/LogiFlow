package ec.edu.espe.pedido_service.service;

import ec.edu.espe.pedido_service.dto.CreatePedidoRequest;
import ec.edu.espe.pedido_service.dto.PedidoResponse;
import ec.edu.espe.pedido_service.model.EstadoPedido;
import ec.edu.espe.pedido_service.model.Pedido;
import ec.edu.espe.pedido_service.model.PrioridadPedido;
import ec.edu.espe.pedido_service.model.TipoEntrega;
import ec.edu.espe.pedido_service.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private CreatePedidoRequest request;
    private Pedido pedido;
    private UUID pedidoId;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        pedidoId = UUID.randomUUID();
        clienteId = UUID.randomUUID();

        request = CreatePedidoRequest.builder()
                .clienteId(clienteId)
                .clienteNombre("Juan Pérez")
                .tipoEntrega(TipoEntrega.URBANA_RAPIDA)
                .prioridad(PrioridadPedido.NORMAL)
                .direccionOrigen("Av. Amazonas N24-03")
                .latitudOrigen(-0.1807)
                .longitudOrigen(-78.4678)
                .direccionDestino("Av. 6 de Diciembre N36-15")
                .latitudDestino(-0.1650)
                .longitudDestino(-78.4822)
                .pesoKg(BigDecimal.valueOf(2.5))
                .descripcionPaquete("Documentos urgentes")
                .build();

        pedido = Pedido.builder()
                .id(pedidoId)
                .numeroPedido("PED-20240116-000001")
                .clienteId(clienteId)
                .clienteNombre("Juan Pérez")
                .tipoEntrega(TipoEntrega.URBANA_RAPIDA)
                .estado(EstadoPedido.RECIBIDO)
                .prioridad(PrioridadPedido.NORMAL)
                .direccionOrigen("Av. Amazonas N24-03")
                .latitudOrigen(-0.1807)
                .longitudOrigen(-78.4678)
                .direccionDestino("Av. 6 de Diciembre N36-15")
                .latitudDestino(-0.1650)
                .longitudDestino(-78.4822)
                .pesoKg(BigDecimal.valueOf(2.5))
                .tarifaBase(BigDecimal.ZERO)
                .tarifaTotal(BigDecimal.ZERO)
                .activo(true)
                .build();
    }

    @Test
    void crearPedido_ConDatosValidos_DebeRetornarPedido() {
        // Arrange
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        PedidoResponse response = pedidoService.crearPedido(request);

        // Assert
        assertNotNull(response);
        assertEquals(pedidoId, response.getId());
        assertEquals(TipoEntrega.URBANA_RAPIDA, response.getTipoEntrega());
        assertEquals(EstadoPedido.RECIBIDO, response.getEstado());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void crearPedido_ConTipoEntregaInvalido_DebeLanzarExcepcion() {
        // Arrange - Distancia > 50km para URBANA_RAPIDA
        request.setLatitudDestino(-1.0);
        request.setLongitudDestino(-79.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.crearPedido(request)
        );
        assertTrue(exception.getMessage().contains("distancia"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crearPedido_SinCoordenadasOrigen_DebeLanzarExcepcion() {
        // Arrange
        request.setLatitudOrigen(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.crearPedido(request)
        );
        assertTrue(exception.getMessage().contains("coordenadas"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void asignarRepartidor_ConRepartidorDisponible_DebeAsignar() {
        // Arrange
        UUID repartidorId = UUID.randomUUID();
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        PedidoResponse response = pedidoService.asignarRepartidor(pedidoId, repartidorId, "Carlos López");

        // Assert
        assertNotNull(response);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void asignarRepartidor_ConPedidoNoExistente_DebeLanzarExcepcion() {
        // Arrange
        UUID repartidorId = UUID.randomUUID();
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.asignarRepartidor(pedidoId, repartidorId, "Carlos López")
        );
        assertTrue(exception.getMessage().contains("no encontrado"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void obtenerPedidoPorId_ConIdValido_DebeRetornarPedido() {
        // Arrange
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        // Act
        PedidoResponse response = pedidoService.obtenerPedidoPorId(pedidoId);

        // Assert
        assertNotNull(response);
        assertEquals(pedidoId, response.getId());
        assertEquals("PED-20240116-000001", response.getNumeroPedido());
    }

    @Test
    void obtenerPedidoPorId_ConIdInvalido_DebeLanzarExcepcion() {
        // Arrange
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.obtenerPedidoPorId(pedidoId)
        );
    }

    @Test
    void actualizarEstado_ConEstadoValido_DebeActualizar() {
        // Arrange
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        PedidoResponse response = pedidoService.cambiarEstado(pedidoId, EstadoPedido.EN_RUTA);

        // Assert
        assertNotNull(response);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }
}
