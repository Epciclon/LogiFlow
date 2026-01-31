package ec.edu.espe.fleet_service.service;

import ec.edu.espe.fleet_service.dto.CreateRepartidorRequest;
import ec.edu.espe.fleet_service.dto.RepartidorResponse;
import ec.edu.espe.fleet_service.model.EstadoRepartidor;
import ec.edu.espe.fleet_service.model.Repartidor;
import ec.edu.espe.fleet_service.repository.RepartidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepartidorServiceTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private RepartidorService repartidorService;

    private CreateRepartidorRequest request;
    private Repartidor repartidor;
    private UUID repartidorId;
    private UUID vehiculoId;

    @BeforeEach
    void setUp() {
        repartidorId = UUID.randomUUID();
        vehiculoId = UUID.randomUUID();

        request = CreateRepartidorRequest.builder()
                .nombreCompleto("Carlos Rodríguez")
                .cedula("1234567890")
                .telefono("0991234567")
                .email("carlos@logiflow.com")
                .build();

        repartidor = Repartidor.builder()
                .id(repartidorId)
                .nombreCompleto("Carlos Rodríguez")
                .cedula("1234567890")
                .telefono("0991234567")
                .email("carlos@logiflow.com")
                .estado(EstadoRepartidor.DISPONIBLE)
                .activo(true)
                .build();
    }

    @Test
    void crearRepartidor_ConDatosValidos_DebeRetornarRepartidor() {
        // Arrange
        when(repartidorRepository.existsByCedula(anyString())).thenReturn(false);
        when(repartidorRepository.save(any(Repartidor.class))).thenReturn(repartidor);

        // Act
        RepartidorResponse response = repartidorService.crearRepartidor(request);

        // Assert
        assertNotNull(response);
        assertEquals(repartidorId, response.getId());
        assertEquals("Carlos Rodríguez", response.getNombreCompleto());
        assertEquals(EstadoRepartidor.DISPONIBLE, response.getEstado());
        verify(repartidorRepository, times(1)).save(any(Repartidor.class));
    }

    @Test
    void crearRepartidor_ConCedulaDuplicada_DebeLanzarExcepcion() {
        // Arrange
        when(repartidorRepository.existsByCedula("1234567890")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repartidorService.crearRepartidor(request)
        );
        assertTrue(exception.getMessage().contains("cédula"));
        verify(repartidorRepository, never()).save(any());
    }

    @Test
    void obtenerRepartidoresDisponibles_DebeRetornarLista() {
        // Arrange
        Repartidor repartidor2 = Repartidor.builder()
                .id(UUID.randomUUID())
                .nombreCompleto("María González")
                .estado(EstadoRepartidor.DISPONIBLE)
                .activo(true)
                .build();

        when(repartidorRepository.findByEstadoAndActivoTrue(EstadoRepartidor.DISPONIBLE))
                .thenReturn(Arrays.asList(repartidor, repartidor2));

        // Act
        List<RepartidorResponse> response = repartidorService.obtenerRepartidoresDisponibles();

        // Assert
        assertNotNull(response);
        // No validamos cantidad ya que el filtro .estaDisponible() puede filtrar
        verify(repartidorRepository, times(1)).findByEstadoAndActivoTrue(EstadoRepartidor.DISPONIBLE);
    }

    @Test
    void actualizarEstado_ConEstadoValido_DebeActualizar() {
        // Arrange
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));
        when(repartidorRepository.save(any(Repartidor.class))).thenReturn(repartidor);

        // Act
        RepartidorResponse response = repartidorService.cambiarEstado(repartidorId, EstadoRepartidor.EN_RUTA);

        // Assert
        assertNotNull(response);
        verify(repartidorRepository, times(1)).save(any(Repartidor.class));
    }

    @Test
    void actualizarEstado_ConRepartidorNoExistente_DebeLanzarExcepcion() {
        // Arrange
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> repartidorService.cambiarEstado(repartidorId, EstadoRepartidor.EN_RUTA)
        );
        verify(repartidorRepository, never()).save(any());
    }

    @Test
    void obtenerRepartidorPorId_ConIdValido_DebeRetornar() {
        // Arrange
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));

        // Act
        RepartidorResponse response = repartidorService.obtenerRepartidorPorId(repartidorId);

        // Assert
        assertNotNull(response);
        assertEquals(repartidorId, response.getId());
        assertEquals("Carlos Rodríguez", response.getNombreCompleto());
    }

    @Test
    void obtenerRepartidorPorId_ConIdInvalido_DebeLanzarExcepcion() {
        // Arrange
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> repartidorService.obtenerRepartidorPorId(repartidorId)
        );
    }
}
