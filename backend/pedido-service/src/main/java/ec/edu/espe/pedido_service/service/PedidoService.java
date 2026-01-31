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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//Servicio de negocio para gestión de pedidos
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;

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

    //Actualizar pedido
    @Transactional
    public PedidoResponse actualizarPedido(UUID id, UpdatePedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));

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
        return convertirAResponse(updatedPedido);
    }

    //Asignar repartidor a pedido
    @Transactional
    public PedidoResponse asignarRepartidor(UUID pedidoId, UUID repartidorId, String repartidorNombre) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId));

        pedido.setRepartidorId(repartidorId);
        pedido.setRepartidorNombre(repartidorNombre);
        pedido.setEstado(EstadoPedido.ASIGNADO);

        Pedido updatedPedido = pedidoRepository.save(pedido);
        return convertirAResponse(updatedPedido);
    }

    //Cambiar estado del pedido
    @Transactional
    public PedidoResponse cambiarEstado(UUID pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + pedidoId));

        pedido.setEstado(nuevoEstado);

        //Si se marca como ENTREGADO, registrar fecha de entrega real
        if (nuevoEstado == EstadoPedido.ENTREGADO && pedido.getFechaEntregaReal() == null) {
            pedido.setFechaEntregaReal(LocalDateTime.now());
        }

        Pedido updatedPedido = pedidoRepository.save(pedido);
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
