package ec.edu.espe.pedido_service.service;

import ec.edu.espe.pedido_service.dto.CreatePedidoRequest;
import ec.edu.espe.pedido_service.dto.PedidoResponse;
import ec.edu.espe.pedido_service.dto.UpdatePedidoRequest;
import ec.edu.espe.pedido_service.model.EstadoPedido;
import ec.edu.espe.pedido_service.model.Pedido;
import ec.edu.espe.pedido_service.model.PrioridadPedido;
import ec.edu.espe.pedido_service.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
<<<<<<< HEAD
import java.util.HashMap;
import java.util.List;
import java.util.Map;
=======
import java.util.List;
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
import java.util.UUID;
import java.util.stream.Collectors;

//Servicio de negocio para gestión de pedidos
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
<<<<<<< HEAD
    private final NotificationProducer notificationProducer;
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a

    //Crear nuevo pedido con validación de cobertura
    @Transactional
    public PedidoResponse crearPedido(CreatePedidoRequest request) {
        //Validar coordenadas
        if (request.getLatitudOrigen() == null || request.getLongitudOrigen() == null ||
            request.getLatitudDestino() == null || request.getLongitudDestino() == null) {
            throw new IllegalArgumentException("Las coordenadas de origen y destino son obligatorias");
        }

        //Generar número de pedido único
        String numeroPedido = generarNumeroPedido();

        //Crear entidad - Usar constructor new() en lugar de builder para evitar @Builder.Default
        Pedido pedido = new Pedido();
        pedido.setId(null);  // Forzar estado transient
        pedido.setNumeroPedido(numeroPedido);
        pedido.setClienteId(request.getClienteId());
        pedido.setClienteNombre(request.getClienteNombre());
        pedido.setTipoEntrega(request.getTipoEntrega());
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setPrioridad(request.getPrioridad() != null ? request.getPrioridad() : PrioridadPedido.NORMAL);
        pedido.setDireccionOrigen(request.getDireccionOrigen());
        pedido.setLatitudOrigen(request.getLatitudOrigen());
        pedido.setLongitudOrigen(request.getLongitudOrigen());
        pedido.setDireccionDestino(request.getDireccionDestino());
        pedido.setLatitudDestino(request.getLatitudDestino());
        pedido.setLongitudDestino(request.getLongitudDestino());
<<<<<<< HEAD
        pedido.setZonaId(request.getZonaId());
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        pedido.setDescripcionPaquete(request.getDescripcionPaquete());
        pedido.setPesoKg(request.getPesoKg());
        pedido.setDimensiones(request.getDimensiones());
        pedido.setTarifaBase(BigDecimal.ZERO);
        pedido.setTarifaTotal(BigDecimal.ZERO);
        pedido.setFechaEstimadaEntrega(request.getFechaEstimadaEntrega());
        pedido.setObservaciones(request.getObservaciones());
        pedido.setActivo(true);

        //Validar cobertura antes de guardar
        if (!pedido.validarCobertura()) {
            throw new IllegalArgumentException(
                "La distancia entre origen y destino excede el límite para el tipo de entrega: " + 
                request.getTipoEntrega().name()
            );
        }

        // Guardar la entidad (persist - una sola vez)
        Pedido savedPedido = pedidoRepository.save(pedido);
        
<<<<<<< HEAD
        // Publicar evento de pedido creado
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("numeroPedido", savedPedido.getNumeroPedido());
            eventData.put("clienteNombre", savedPedido.getClienteNombre());
            eventData.put("tipoEntrega", savedPedido.getTipoEntrega().name());
            eventData.put("prioridad", savedPedido.getPrioridad().name());
            eventData.put("direccionDestino", savedPedido.getDireccionDestino());
            
            notificationProducer.publishPedidoCreado(savedPedido.getId().toString(), eventData);
        } catch (Exception e) {
            // No fallar la transacción si falla el evento, solo loguear
            System.err.println("Error al publicar evento pedido.creado: " + e.getMessage());
        }
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return convertirAResponse(savedPedido);
    }

    //Obtener todos los pedidos activos
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerTodosLosPedidos() {
        return pedidoRepository.findByActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener pedido por ID
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPedidoPorId(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
        return convertirAResponse(pedido);
    }

    //Obtener pedido por número
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPedidoPorNumero(String numeroPedido) {
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con número: " + numeroPedido));
        return convertirAResponse(pedido);
    }

    //Obtener pedidos de un cliente
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorCliente(UUID clienteId) {
        return pedidoRepository.findByClienteIdAndActivoTrue(clienteId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener pedidos de un repartidor
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorRepartidor(UUID repartidorId) {
        return pedidoRepository.findByRepartidorIdAndActivoTrue(repartidorId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener pedidos por estado
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstadoAndActivoTrue(estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

<<<<<<< HEAD
    //Obtener pedidos por zona
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorZona(String zonaId) {
        return pedidoRepository.findByZonaIdAndActivoTrue(zonaId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener pedidos por zona y estado
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPorZonaYEstado(String zonaId, EstadoPedido estado) {
        return pedidoRepository.findByZonaIdAndEstadoAndActivoTrue(zonaId, estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
    //Actualizar pedido
    @Transactional
    public PedidoResponse actualizarPedido(UUID id, UpdatePedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));

<<<<<<< HEAD
        EstadoPedido estadoAnterior = pedido.getEstado();

=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        if (request.getEstado() != null) {
            pedido.setEstado(request.getEstado());
            //Si se marca como ENTREGADO, registrar fecha de entrega real
            if (request.getEstado() == EstadoPedido.ENTREGADO && request.getFechaEntregaReal() == null) {
                pedido.setFechaEntregaReal(LocalDateTime.now());
            }
        }

        if (request.getRepartidorId() != null) {
            pedido.setRepartidorId(request.getRepartidorId());
            pedido.setRepartidorNombre(request.getRepartidorNombre());
        }

        if (request.getPrioridad() != null) {
            pedido.setPrioridad(request.getPrioridad());
        }

        if (request.getTarifaBase() != null) {
            pedido.setTarifaBase(request.getTarifaBase());
        }

        if (request.getTarifaTotal() != null) {
            pedido.setTarifaTotal(request.getTarifaTotal());
        }

        if (request.getFechaEstimadaEntrega() != null) {
            pedido.setFechaEstimadaEntrega(request.getFechaEstimadaEntrega());
        }

        if (request.getFechaEntregaReal() != null) {
            pedido.setFechaEntregaReal(request.getFechaEntregaReal());
        }

        if (request.getObservaciones() != null) {
            pedido.setObservaciones(request.getObservaciones());
        }

        if (request.getActivo() != null) {
            pedido.setActivo(request.getActivo());
        }

        Pedido updatedPedido = pedidoRepository.save(pedido);
<<<<<<< HEAD
        
        // Publicar evento si cambió el estado
        if (request.getEstado() != null && !estadoAnterior.equals(request.getEstado())) {
            try {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("numeroPedido", updatedPedido.getNumeroPedido());
                eventData.put("clienteNombre", updatedPedido.getClienteNombre());
                eventData.put("repartidorNombre", updatedPedido.getRepartidorNombre());
                
                notificationProducer.publishPedidoEstadoActualizado(
                    updatedPedido.getId().toString(),
                    estadoAnterior.name(),
                    updatedPedido.getEstado().name(),
                    eventData
                );
            } catch (Exception e) {
                System.err.println("Error al publicar evento pedido.estado.actualizado: " + e.getMessage());
            }
        }
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return convertirAResponse(updatedPedido);
    }

    //Asignar repartidor a pedido
    @Transactional
    public PedidoResponse asignarRepartidor(UUID pedidoId, UUID repartidorId, String repartidorNombre) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId));

<<<<<<< HEAD
        EstadoPedido estadoAnterior = pedido.getEstado();
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        pedido.setRepartidorId(repartidorId);
        pedido.setRepartidorNombre(repartidorNombre);
        pedido.setEstado(EstadoPedido.ASIGNADO);

        Pedido updatedPedido = pedidoRepository.save(pedido);
<<<<<<< HEAD
        
        // Publicar evento de estado actualizado
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("numeroPedido", updatedPedido.getNumeroPedido());
            eventData.put("clienteNombre", updatedPedido.getClienteNombre());
            eventData.put("repartidorId", repartidorId.toString());
            eventData.put("repartidorNombre", repartidorNombre);
            
            notificationProducer.publishPedidoEstadoActualizado(
                updatedPedido.getId().toString(),
                estadoAnterior.name(),
                EstadoPedido.ASIGNADO.name(),
                eventData
            );
        } catch (Exception e) {
            System.err.println("Error al publicar evento de asignación: " + e.getMessage());
        }
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return convertirAResponse(updatedPedido);
    }

    //Cambiar estado del pedido
    @Transactional
    public PedidoResponse cambiarEstado(UUID pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId));

<<<<<<< HEAD
        EstadoPedido estadoAnterior = pedido.getEstado();
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        pedido.setEstado(nuevoEstado);

        //Si se marca como ENTREGADO, registrar fecha de entrega real
        if (nuevoEstado == EstadoPedido.ENTREGADO && pedido.getFechaEntregaReal() == null) {
            pedido.setFechaEntregaReal(LocalDateTime.now());
        }

        Pedido updatedPedido = pedidoRepository.save(pedido);
<<<<<<< HEAD
        
        // Publicar evento de estado actualizado
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("numeroPedido", updatedPedido.getNumeroPedido());
            eventData.put("clienteNombre", updatedPedido.getClienteNombre());
            eventData.put("repartidorNombre", updatedPedido.getRepartidorNombre());
            
            notificationProducer.publishPedidoEstadoActualizado(
                updatedPedido.getId().toString(),
                estadoAnterior.name(),
                nuevoEstado.name(),
                eventData
            );
        } catch (Exception e) {
            System.err.println("Error al publicar evento cambio de estado: " + e.getMessage());
        }
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return convertirAResponse(updatedPedido);
    }

    //Cancelar pedido
    @Transactional
    public PedidoResponse cancelarPedido(UUID pedidoId, String motivo) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId));

        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido que ya fue entregado");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setObservaciones(
            (pedido.getObservaciones() != null ? pedido.getObservaciones() + " | " : "") +
            "CANCELADO: " + motivo
        );

        Pedido updatedPedido = pedidoRepository.save(pedido);
<<<<<<< HEAD
        
        // Publicar evento de pedido cancelado
        try {
            notificationProducer.publishPedidoCancelado(updatedPedido.getId().toString(), motivo);
        } catch (Exception e) {
            System.err.println("Error al publicar evento pedido.cancelado: " + e.getMessage());
        }
        
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
        return convertirAResponse(updatedPedido);
    }

    //Eliminación lógica
    @Transactional
    public void eliminarPedido(UUID id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
        pedido.setActivo(false);
        pedidoRepository.save(pedido);
    }

    //Generar número de pedido único (formato: PED-YYYYMMDD-HHMMSS-XXXX)
    private String generarNumeroPedido() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String random = String.format("%04d", (int)(Math.random() * 10000));
        String numero = "PED-" + timestamp + "-" + random;

        //Verificar unicidad
        while (pedidoRepository.existsByNumeroPedido(numero)) {
            random = String.format("%04d", (int)(Math.random() * 10000));
            numero = "PED-" + timestamp + "-" + random;
        }

        return numero;
    }

    //Convertir entidad a DTO
    private PedidoResponse convertirAResponse(Pedido pedido) {
        return PedidoResponse.builder()
                .id(pedido.getId())
                .numeroPedido(pedido.getNumeroPedido())
                .clienteId(pedido.getClienteId())
                .clienteNombre(pedido.getClienteNombre())
                .repartidorId(pedido.getRepartidorId())
                .repartidorNombre(pedido.getRepartidorNombre())
                .tipoEntrega(pedido.getTipoEntrega())
                .estado(pedido.getEstado())
                .prioridad(pedido.getPrioridad())
                .direccionOrigen(pedido.getDireccionOrigen())
                .latitudOrigen(pedido.getLatitudOrigen())
                .longitudOrigen(pedido.getLongitudOrigen())
                .direccionDestino(pedido.getDireccionDestino())
                .latitudDestino(pedido.getLatitudDestino())
                .longitudDestino(pedido.getLongitudDestino())
<<<<<<< HEAD
                .zonaId(pedido.getZonaId())
=======
>>>>>>> 9e74cc4ddb0f03faf66297a7ffe73dc4a3b2a29a
                .descripcionPaquete(pedido.getDescripcionPaquete())
                .pesoKg(pedido.getPesoKg())
                .dimensiones(pedido.getDimensiones())
                .tarifaBase(pedido.getTarifaBase())
                .tarifaTotal(pedido.getTarifaTotal())
                .fechaEstimadaEntrega(pedido.getFechaEstimadaEntrega())
                .fechaEntregaReal(pedido.getFechaEntregaReal())
                .observaciones(pedido.getObservaciones())
                .activo(pedido.getActivo())
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .build();
    }
}
