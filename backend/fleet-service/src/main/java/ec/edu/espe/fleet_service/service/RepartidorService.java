package ec.edu.espe.fleet_service.service;

import ec.edu.espe.fleet_service.dto.CreateRepartidorRequest;
import ec.edu.espe.fleet_service.dto.RepartidorResponse;
import ec.edu.espe.fleet_service.dto.UpdateRepartidorRequest;
import ec.edu.espe.fleet_service.model.EstadoRepartidor;
import ec.edu.espe.fleet_service.model.Repartidor;
import ec.edu.espe.fleet_service.model.Vehiculo;
import ec.edu.espe.fleet_service.repository.RepartidorRepository;
import ec.edu.espe.fleet_service.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
import java.util.HashMap;
import java.util.List;
import java.util.Map;
=======
import java.util.List;
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
import java.util.UUID;
import java.util.stream.Collectors;

//Servicio de negocio para gestión de repartidores
@Service
@RequiredArgsConstructor
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final VehiculoRepository vehiculoRepository;
<<<<<<< HEAD
    private final NotificationProducer notificationProducer;
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a

    //Crear nuevo repartidor
    @Transactional
    public RepartidorResponse crearRepartidor(CreateRepartidorRequest request) {
        //Validar unicidad
        if (repartidorRepository.existsByCodigoEmpleado(request.getCodigoEmpleado())) {
            throw new IllegalArgumentException("El código de empleado ya existe: " + request.getCodigoEmpleado());
        }
        if (repartidorRepository.existsByCedula(request.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada: " + request.getCedula());
        }
        if (repartidorRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + request.getEmail());
        }

        //Crear entidad
        Repartidor repartidor = Repartidor.builder()
                .codigoEmpleado(request.getCodigoEmpleado())
                .nombreCompleto(request.getNombreCompleto())
                .cedula(request.getCedula())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .fechaNacimiento(request.getFechaNacimiento())
                .fechaContratacion(request.getFechaContratacion())
                .licenciasConducir(request.getLicenciasConducir())
                .numeroLicencia(request.getNumeroLicencia())
                .fechaVencimientoLicencia(request.getFechaVencimientoLicencia())
                .estado(EstadoRepartidor.DISPONIBLE)
                .observaciones(request.getObservaciones())
                .activo(true)
                .build();

        //Asignar vehículo si se proporciona
        if (request.getVehiculoId() != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
            repartidor.setVehiculoAsignado(vehiculo);
        }

        Repartidor savedRepartidor = repartidorRepository.save(repartidor);
<<<<<<< HEAD
        
        // Publicar evento de repartidor creado
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("codigoEmpleado", savedRepartidor.getCodigoEmpleado());
            eventData.put("nombreCompleto", savedRepartidor.getNombreCompleto());
            eventData.put("email", savedRepartidor.getEmail());
            eventData.put("estado", savedRepartidor.getEstado().name());
            
            notificationProducer.publishRepartidorCreado(savedRepartidor.getId().toString(), eventData);
        } catch (Exception e) {
            System.err.println("Error al publicar evento repartidor.creado: " + e.getMessage());
        }
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return convertirAResponse(savedRepartidor);
    }

    //Obtener todos los repartidores activos
    @Transactional(readOnly = true)
    public List<RepartidorResponse> obtenerTodosLosRepartidores() {
        return repartidorRepository.findByActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener repartidor por ID
    @Transactional(readOnly = true)
    public RepartidorResponse obtenerRepartidorPorId(UUID id) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado con ID: " + id));
        return convertirAResponse(repartidor);
    }

    //Obtener repartidor por código
    @Transactional(readOnly = true)
    public RepartidorResponse obtenerRepartidorPorCodigo(String codigoEmpleado) {
        Repartidor repartidor = repartidorRepository.findByCodigoEmpleado(codigoEmpleado)
                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado: " + codigoEmpleado));
        return convertirAResponse(repartidor);
    }

    //Obtener repartidores por estado
    @Transactional(readOnly = true)
    public List<RepartidorResponse> obtenerRepartidoresPorEstado(EstadoRepartidor estado) {
        return repartidorRepository.findByEstadoAndActivoTrue(estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener repartidores disponibles
    @Transactional(readOnly = true)
    public List<RepartidorResponse> obtenerRepartidoresDisponibles() {
        return repartidorRepository.findByEstadoAndActivoTrue(EstadoRepartidor.DISPONIBLE).stream()
                .filter(r -> r.estaDisponible())
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Actualizar repartidor
    @Transactional
    public RepartidorResponse actualizarRepartidor(UUID id, UpdateRepartidorRequest request) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado con ID: " + id));

<<<<<<< HEAD
        EstadoRepartidor estadoAnterior = repartidor.getEstado();

=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        if (request.getNombreCompleto() != null) {
            repartidor.setNombreCompleto(request.getNombreCompleto());
        }
        if (request.getEmail() != null) {
            repartidor.setEmail(request.getEmail());
        }
        if (request.getTelefono() != null) {
            repartidor.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            repartidor.setDireccion(request.getDireccion());
        }
        if (request.getLicenciasConducir() != null) {
            repartidor.setLicenciasConducir(request.getLicenciasConducir());
        }
        if (request.getNumeroLicencia() != null) {
            repartidor.setNumeroLicencia(request.getNumeroLicencia());
        }
        if (request.getFechaVencimientoLicencia() != null) {
            repartidor.setFechaVencimientoLicencia(request.getFechaVencimientoLicencia());
        }
        if (request.getEstado() != null) {
            repartidor.setEstado(request.getEstado());
        }
        if (request.getVehiculoId() != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                    .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
            repartidor.setVehiculoAsignado(vehiculo);
        }
        if (request.getEntregasCompletadas() != null) {
            repartidor.setEntregasCompletadas(request.getEntregasCompletadas());
        }
        if (request.getEntregasCanceladas() != null) {
            repartidor.setEntregasCanceladas(request.getEntregasCanceladas());
        }
        if (request.getCalificacionPromedio() != null) {
            repartidor.setCalificacionPromedio(request.getCalificacionPromedio());
        }
        if (request.getObservaciones() != null) {
            repartidor.setObservaciones(request.getObservaciones());
        }
        if (request.getActivo() != null) {
            repartidor.setActivo(request.getActivo());
        }

        Repartidor updatedRepartidor = repartidorRepository.save(repartidor);
<<<<<<< HEAD
        
        // Publicar evento si cambió el estado
        if (request.getEstado() != null && !estadoAnterior.equals(request.getEstado())) {
            try {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("codigoEmpleado", updatedRepartidor.getCodigoEmpleado());
                eventData.put("nombreCompleto", updatedRepartidor.getNombreCompleto());
                
                notificationProducer.publishRepartidorEstadoActualizado(
                    updatedRepartidor.getId().toString(),
                    estadoAnterior.name(),
                    updatedRepartidor.getEstado().name(),
                    eventData
                );
            } catch (Exception e) {
                System.err.println("Error al publicar evento repartidor.estado.actualizado: " + e.getMessage());
            }
        }
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return convertirAResponse(updatedRepartidor);
    }

    //Cambiar estado del repartidor
    @Transactional
    public RepartidorResponse cambiarEstado(UUID id, EstadoRepartidor nuevoEstado) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado con ID: " + id));
        
<<<<<<< HEAD
        EstadoRepartidor estadoAnterior = repartidor.getEstado();
        
        repartidor.setEstado(nuevoEstado);
        Repartidor updatedRepartidor = repartidorRepository.save(repartidor);
        
        // Publicar evento de estado actualizado
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("codigoEmpleado", updatedRepartidor.getCodigoEmpleado());
            eventData.put("nombreCompleto", updatedRepartidor.getNombreCompleto());
            
            notificationProducer.publishRepartidorEstadoActualizado(
                updatedRepartidor.getId().toString(),
                estadoAnterior.name(),
                nuevoEstado.name(),
                eventData
            );
        } catch (Exception e) {
            System.err.println("Error al publicar evento cambio de estado: " + e.getMessage());
        }
        
=======
        repartidor.setEstado(nuevoEstado);
        Repartidor updatedRepartidor = repartidorRepository.save(repartidor);
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return convertirAResponse(updatedRepartidor);
    }

    //Asignar vehículo
    @Transactional
    public RepartidorResponse asignarVehiculo(UUID repartidorId, UUID vehiculoId) {
        Repartidor repartidor = repartidorRepository.findById(repartidorId)
                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado"));
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));
        
        repartidor.setVehiculoAsignado(vehiculo);
        Repartidor updatedRepartidor = repartidorRepository.save(repartidor);
        return convertirAResponse(updatedRepartidor);
    }

    //Eliminar repartidor (lógico)
    @Transactional
    public void eliminarRepartidor(UUID id) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado con ID: " + id));
        repartidor.setActivo(false);
        repartidorRepository.save(repartidor);
    }

    //Convertir entidad a DTO
    private RepartidorResponse convertirAResponse(Repartidor repartidor) {
        return RepartidorResponse.builder()
                .id(repartidor.getId())
                .codigoEmpleado(repartidor.getCodigoEmpleado())
                .nombreCompleto(repartidor.getNombreCompleto())
                .cedula(repartidor.getCedula())
                .email(repartidor.getEmail())
                .telefono(repartidor.getTelefono())
                .direccion(repartidor.getDireccion())
                .fechaNacimiento(repartidor.getFechaNacimiento())
                .fechaContratacion(repartidor.getFechaContratacion())
                .licenciasConducir(repartidor.getLicenciasConducir())
                .numeroLicencia(repartidor.getNumeroLicencia())
                .fechaVencimientoLicencia(repartidor.getFechaVencimientoLicencia())
                .estado(repartidor.getEstado())
                .observaciones(repartidor.getObservaciones())
                .activo(repartidor.getActivo())
                .vehiculoId(repartidor.getVehiculoAsignado() != null ? repartidor.getVehiculoAsignado().getId() : null)
                .vehiculoPlaca(repartidor.getVehiculoAsignado() != null ? repartidor.getVehiculoAsignado().getPlaca() : null)
                .entregasCompletadas(repartidor.getEntregasCompletadas())
                .entregasCanceladas(repartidor.getEntregasCanceladas())
                .calificacionPromedio(repartidor.getCalificacionPromedio())
                .fechaCreacion(repartidor.getFechaCreacion())
                .fechaActualizacion(repartidor.getFechaActualizacion())
                .build();
    }
}
