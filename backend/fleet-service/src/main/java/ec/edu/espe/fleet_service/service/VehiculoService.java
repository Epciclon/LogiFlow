package ec.edu.espe.fleet_service.service;

import ec.edu.espe.fleet_service.dto.CreateVehiculoRequest;
import ec.edu.espe.fleet_service.dto.UpdateVehiculoRequest;
import ec.edu.espe.fleet_service.dto.VehiculoResponse;
import ec.edu.espe.fleet_service.model.EstadoVehiculo;
import ec.edu.espe.fleet_service.model.TipoVehiculo;
import ec.edu.espe.fleet_service.model.Vehiculo;
import ec.edu.espe.fleet_service.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//Servicio de negocio para gestión de vehículos
@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    //Crear nuevo vehículo
    @Transactional
    public VehiculoResponse crearVehiculo(CreateVehiculoRequest request) {
        //Validar unicidad de placa
        if (vehiculoRepository.existsByPlaca(request.getPlaca())) {
            throw new IllegalArgumentException("La placa ya está registrada: " + request.getPlaca());
        }

        //Crear entidad
        Vehiculo vehiculo = Vehiculo.builder()
                .placa(request.getPlaca())
                .tipoVehiculo(request.getTipoVehiculo())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .anio(request.getAnio())
                .color(request.getColor())
                .capacidadCargaKg(request.getCapacidadCargaKg())
                .capacidadVolumenM3(request.getCapacidadVolumenM3())
                .estado(EstadoVehiculo.DISPONIBLE)
                .fechaUltimoMantenimiento(request.getFechaUltimoMantenimiento())
                .fechaProximoMantenimiento(request.getFechaProximoMantenimiento())
                .kilometraje(request.getKilometraje() != null ? request.getKilometraje() : 0)
                .numeroPolizaSeguro(request.getNumeroPolizaSeguro())
                .fechaVencimientoSeguro(request.getFechaVencimientoSeguro())
                .fechaVencimientoMatricula(request.getFechaVencimientoMatricula())
                .fechaVencimientoRevisionTecnica(request.getFechaVencimientoRevisionTecnica())
                .observaciones(request.getObservaciones())
                .activo(true)
                .build();

        Vehiculo savedVehiculo = vehiculoRepository.save(vehiculo);
        return convertirAResponse(savedVehiculo);
    }

    //Obtener todos los vehículos activos
    @Transactional(readOnly = true)
    public List<VehiculoResponse> obtenerTodosLosVehiculos() {
        return vehiculoRepository.findByActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener vehículo por ID
    @Transactional(readOnly = true)
    public VehiculoResponse obtenerVehiculoPorId(UUID id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con ID: " + id));
        return convertirAResponse(vehiculo);
    }

    //Obtener vehículo por placa
    @Transactional(readOnly = true)
    public VehiculoResponse obtenerVehiculoPorPlaca(String placa) {
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con placa: " + placa));
        return convertirAResponse(vehiculo);
    }

    //Obtener vehículos por tipo
    @Transactional(readOnly = true)
    public List<VehiculoResponse> obtenerVehiculosPorTipo(TipoVehiculo tipo) {
        return vehiculoRepository.findByTipoVehiculoAndActivoTrue(tipo).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener vehículos por estado
    @Transactional(readOnly = true)
    public List<VehiculoResponse> obtenerVehiculosPorEstado(EstadoVehiculo estado) {
        return vehiculoRepository.findByEstadoAndActivoTrue(estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener vehículos disponibles
    @Transactional(readOnly = true)
    public List<VehiculoResponse> obtenerVehiculosDisponibles() {
        return vehiculoRepository.findByEstadoAndActivoTrue(EstadoVehiculo.DISPONIBLE).stream()
                .filter(v -> v.estaOperativo())
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Actualizar vehículo
    @Transactional
    public VehiculoResponse actualizarVehiculo(UUID id, UpdateVehiculoRequest request) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con ID: " + id));

        if (request.getColor() != null) {
            vehiculo.setColor(request.getColor());
        }
        if (request.getCapacidadCargaKg() != null) {
            vehiculo.setCapacidadCargaKg(request.getCapacidadCargaKg());
        }
        if (request.getCapacidadVolumenM3() != null) {
            vehiculo.setCapacidadVolumenM3(request.getCapacidadVolumenM3());
        }
        if (request.getEstado() != null) {
            vehiculo.setEstado(request.getEstado());
        }
        if (request.getFechaUltimoMantenimiento() != null) {
            vehiculo.setFechaUltimoMantenimiento(request.getFechaUltimoMantenimiento());
        }
        if (request.getFechaProximoMantenimiento() != null) {
            vehiculo.setFechaProximoMantenimiento(request.getFechaProximoMantenimiento());
        }
        if (request.getKilometraje() != null) {
            vehiculo.setKilometraje(request.getKilometraje());
        }
        if (request.getNumeroPolizaSeguro() != null) {
            vehiculo.setNumeroPolizaSeguro(request.getNumeroPolizaSeguro());
        }
        if (request.getFechaVencimientoSeguro() != null) {
            vehiculo.setFechaVencimientoSeguro(request.getFechaVencimientoSeguro());
        }
        if (request.getFechaVencimientoMatricula() != null) {
            vehiculo.setFechaVencimientoMatricula(request.getFechaVencimientoMatricula());
        }
        if (request.getFechaVencimientoRevisionTecnica() != null) {
            vehiculo.setFechaVencimientoRevisionTecnica(request.getFechaVencimientoRevisionTecnica());
        }
        if (request.getObservaciones() != null) {
            vehiculo.setObservaciones(request.getObservaciones());
        }
        if (request.getActivo() != null) {
            vehiculo.setActivo(request.getActivo());
        }

        Vehiculo updatedVehiculo = vehiculoRepository.save(vehiculo);
        return convertirAResponse(updatedVehiculo);
    }

    //Cambiar estado del vehículo
    @Transactional
    public VehiculoResponse cambiarEstado(UUID id, EstadoVehiculo nuevoEstado) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con ID: " + id));
        
        vehiculo.setEstado(nuevoEstado);
        Vehiculo updatedVehiculo = vehiculoRepository.save(vehiculo);
        return convertirAResponse(updatedVehiculo);
    }

    //Eliminar vehículo (lógico)
    @Transactional
    public void eliminarVehiculo(UUID id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con ID: " + id));
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
    }

    //Convertir entidad a DTO
    private VehiculoResponse convertirAResponse(Vehiculo vehiculo) {
        return VehiculoResponse.builder()
                .id(vehiculo.getId())
                .placa(vehiculo.getPlaca())
                .tipoVehiculo(vehiculo.getTipoVehiculo())
                .marca(vehiculo.getMarca())
                .modelo(vehiculo.getModelo())
                .anio(vehiculo.getAnio())
                .color(vehiculo.getColor())
                .capacidadCargaKg(vehiculo.getCapacidadCargaKg())
                .capacidadVolumenM3(vehiculo.getCapacidadVolumenM3())
                .estado(vehiculo.getEstado())
                .fechaUltimoMantenimiento(vehiculo.getFechaUltimoMantenimiento())
                .fechaProximoMantenimiento(vehiculo.getFechaProximoMantenimiento())
                .kilometraje(vehiculo.getKilometraje())
                .numeroPolizaSeguro(vehiculo.getNumeroPolizaSeguro())
                .fechaVencimientoSeguro(vehiculo.getFechaVencimientoSeguro())
                .fechaVencimientoMatricula(vehiculo.getFechaVencimientoMatricula())
                .fechaVencimientoRevisionTecnica(vehiculo.getFechaVencimientoRevisionTecnica())
                .observaciones(vehiculo.getObservaciones())
                .activo(vehiculo.getActivo())
                .fechaCreacion(vehiculo.getFechaCreacion())
                .fechaActualizacion(vehiculo.getFechaActualizacion())
                .build();
    }
}
