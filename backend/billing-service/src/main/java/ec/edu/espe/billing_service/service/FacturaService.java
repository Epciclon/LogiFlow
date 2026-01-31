package ec.edu.espe.billing_service.service;

import ec.edu.espe.billing_service.dto.CreateFacturaRequest;
import ec.edu.espe.billing_service.dto.FacturaResponse;
import ec.edu.espe.billing_service.dto.UpdateFacturaRequest;
import ec.edu.espe.billing_service.model.EstadoFactura;
import ec.edu.espe.billing_service.model.Factura;
import ec.edu.espe.billing_service.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//Servicio de negocio para gestión de facturas
@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;

    //Tarifas configurables desde application.yaml
    @Value("${billing.tarifas.urbana.base:3.00}")
    private BigDecimal tarifaUrbanaBase;

    @Value("${billing.tarifas.urbana.por-km:0.50}")
    private BigDecimal tarifaUrbanaPorKm;

    @Value("${billing.tarifas.urbana.por-kg:0.20}")
    private BigDecimal tarifaUrbanaPorKg;

    @Value("${billing.tarifas.intermunicipal.base:10.00}")
    private BigDecimal tarifaIntermunicipalBase;

    @Value("${billing.tarifas.intermunicipal.por-km:0.80}")
    private BigDecimal tarifaIntermunicipalPorKm;

    @Value("${billing.tarifas.intermunicipal.por-kg:0.30}")
    private BigDecimal tarifaIntermunicipalPorKg;

    @Value("${billing.tarifas.nacional.base:50.00}")
    private BigDecimal tarifaNacionalBase;

    @Value("${billing.tarifas.nacional.por-km:1.20}")
    private BigDecimal tarifaNacionalPorKm;

    @Value("${billing.tarifas.nacional.por-kg:0.50}")
    private BigDecimal tarifaNacionalPorKg;

    @Value("${billing.recargos.urgente:2.00}")
    private BigDecimal recargoUrgente;

    @Value("${billing.recargos.alta:1.50}")
    private BigDecimal recargoAlta;

    @Value("${billing.recargos.normal:1.00}")
    private BigDecimal recargoNormal;

    @Value("${billing.recargos.baja:1.00}")
    private BigDecimal recargoBaja;

    //Crear nueva factura con cálculo automático de tarifas
    @Transactional
    public FacturaResponse crearFactura(CreateFacturaRequest request) {
        //Validar que no exista factura para el pedido
        if (facturaRepository.existsByPedidoId(request.getPedidoId())) {
            throw new IllegalArgumentException("Ya existe una factura para el pedido: " + request.getNumeroPedido());
        }

        //Generar número de factura
        String numeroFactura = generarNumeroFactura();

        //Calcular tarifas
        BigDecimal tarifaBase = calcularTarifaBase(request.getTipoEntrega());
        BigDecimal cargoDistancia = calcularCargoDistancia(request.getTipoEntrega(), request.getDistanciaKm());
        BigDecimal cargoPeso = calcularCargoPeso(request.getTipoEntrega(), request.getPesoKg());
        BigDecimal recargoPrioridad = calcularRecargoPrioridad(request.getPrioridad());

        //Crear factura
        Factura factura = Factura.builder()
                .numeroFactura(numeroFactura)
                .pedidoId(request.getPedidoId())
                .numeroPedido(request.getNumeroPedido())
                .clienteId(request.getClienteId())
                .clienteNombre(request.getClienteNombre())
                .tipoEntrega(request.getTipoEntrega())
                .distanciaKm(request.getDistanciaKm())
                .pesoKg(request.getPesoKg())
                .tarifaBase(tarifaBase)
                .cargoDistancia(cargoDistancia)
                .cargoPeso(cargoPeso)
                .recargoPrioridad(recargoPrioridad)
                .descuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO)
                .estado(EstadoFactura.BORRADOR)
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(request.getFechaVencimiento() != null ? request.getFechaVencimiento() : LocalDate.now().plusDays(15))
                .observaciones(request.getObservaciones())
                .activo(true)
                .build();

        //Calcular total
        factura.calcularTotal();

        Factura savedFactura = facturaRepository.save(factura);
        return convertirAResponse(savedFactura);
    }

    //Obtener todas las facturas activas
    @Transactional(readOnly = true)
    public List<FacturaResponse> obtenerTodasLasFacturas() {
        return facturaRepository.findByActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener factura por ID
    @Transactional(readOnly = true)
    public FacturaResponse obtenerFacturaPorId(UUID id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con ID: " + id));
        return convertirAResponse(factura);
    }

    //Obtener factura por número
    @Transactional(readOnly = true)
    public FacturaResponse obtenerFacturaPorNumero(String numeroFactura) {
        Factura factura = facturaRepository.findByNumeroFactura(numeroFactura)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + numeroFactura));
        return convertirAResponse(factura);
    }

    //Obtener factura por pedido
    @Transactional(readOnly = true)
    public FacturaResponse obtenerFacturaPorPedido(UUID pedidoId) {
        Factura factura = facturaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("No hay factura para el pedido: " + pedidoId));
        return convertirAResponse(factura);
    }

    //Obtener facturas por cliente
    @Transactional(readOnly = true)
    public List<FacturaResponse> obtenerFacturasPorCliente(UUID clienteId) {
        return facturaRepository.findByClienteIdAndActivoTrue(clienteId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Obtener facturas por estado
    @Transactional(readOnly = true)
    public List<FacturaResponse> obtenerFacturasPorEstado(EstadoFactura estado) {
        return facturaRepository.findByEstadoAndActivoTrue(estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    //Actualizar factura
    @Transactional
    public FacturaResponse actualizarFactura(UUID id, UpdateFacturaRequest request) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con ID: " + id));

        boolean recalcular = false;

        if (request.getEstado() != null) {
            factura.setEstado(request.getEstado());
            //Si se marca como PAGADA, registrar fecha de pago
            if (request.getEstado() == EstadoFactura.PAGADA && request.getFechaPago() == null) {
                factura.setFechaPago(LocalDate.now());
            }
        }

        if (request.getDescuento() != null) {
            factura.setDescuento(request.getDescuento());
            recalcular = true;
        }

        if (request.getFechaVencimiento() != null) {
            factura.setFechaVencimiento(request.getFechaVencimiento());
        }

        if (request.getFechaPago() != null) {
            factura.setFechaPago(request.getFechaPago());
        }

        if (request.getMetodoPago() != null) {
            factura.setMetodoPago(request.getMetodoPago());
        }

        if (request.getObservaciones() != null) {
            factura.setObservaciones(request.getObservaciones());
        }

        if (request.getActivo() != null) {
            factura.setActivo(request.getActivo());
        }

        //Recalcular total si es necesario
        if (recalcular) {
            factura.calcularTotal();
        }

        Factura updatedFactura = facturaRepository.save(factura);
        return convertirAResponse(updatedFactura);
    }

    //Cambiar estado de factura
    @Transactional
    public FacturaResponse cambiarEstado(UUID id, EstadoFactura nuevoEstado) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con ID: " + id));

        factura.setEstado(nuevoEstado);

        //Si se marca como PAGADA, registrar fecha de pago
        if (nuevoEstado == EstadoFactura.PAGADA && factura.getFechaPago() == null) {
            factura.setFechaPago(LocalDate.now());
        }

        Factura updatedFactura = facturaRepository.save(factura);
        return convertirAResponse(updatedFactura);
    }

    //Registrar pago
    @Transactional
    public FacturaResponse registrarPago(UUID id, String metodoPago) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con ID: " + id));

        if (factura.getEstado() == EstadoFactura.PAGADA) {
            throw new IllegalStateException("La factura ya está marcada como pagada");
        }

        factura.setEstado(EstadoFactura.PAGADA);
        factura.setFechaPago(LocalDate.now());
        factura.setMetodoPago(metodoPago);

        Factura updatedFactura = facturaRepository.save(factura);
        return convertirAResponse(updatedFactura);
    }

    //Eliminar factura (lógico)
    @Transactional
    public void eliminarFactura(UUID id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con ID: " + id));
        factura.setActivo(false);
        facturaRepository.save(factura);
    }

    //Cálculo de tarifa base según tipo de entrega
    private BigDecimal calcularTarifaBase(String tipoEntrega) {
        return switch (tipoEntrega.toUpperCase()) {
            case "URBANA_RAPIDA" -> tarifaUrbanaBase;
            case "INTERMUNICIPAL" -> tarifaIntermunicipalBase;
            case "NACIONAL" -> tarifaNacionalBase;
            default -> tarifaUrbanaBase;
        };
    }

    //Cálculo de cargo por distancia
    private BigDecimal calcularCargoDistancia(String tipoEntrega, BigDecimal distanciaKm) {
        if (distanciaKm == null || distanciaKm.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tarifaPorKm = switch (tipoEntrega.toUpperCase()) {
            case "URBANA_RAPIDA" -> tarifaUrbanaPorKm;
            case "INTERMUNICIPAL" -> tarifaIntermunicipalPorKm;
            case "NACIONAL" -> tarifaNacionalPorKm;
            default -> tarifaUrbanaPorKm;
        };

        return distanciaKm.multiply(tarifaPorKm).setScale(2, RoundingMode.HALF_UP);
    }

    //Cálculo de cargo por peso
    private BigDecimal calcularCargoPeso(String tipoEntrega, BigDecimal pesoKg) {
        if (pesoKg == null || pesoKg.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tarifaPorKg = switch (tipoEntrega.toUpperCase()) {
            case "URBANA_RAPIDA" -> tarifaUrbanaPorKg;
            case "INTERMUNICIPAL" -> tarifaIntermunicipalPorKg;
            case "NACIONAL" -> tarifaNacionalPorKg;
            default -> tarifaUrbanaPorKg;
        };

        return pesoKg.multiply(tarifaPorKg).setScale(2, RoundingMode.HALF_UP);
    }

    //Cálculo de recargo por prioridad
    private BigDecimal calcularRecargoPrioridad(String prioridad) {
        if (prioridad == null) {
            return recargoNormal;
        }

        return switch (prioridad.toUpperCase()) {
            case "URGENTE" -> recargoUrgente;
            case "ALTA" -> recargoAlta;
            case "BAJA" -> recargoBaja;
            default -> recargoNormal;
        };
    }

    //Generar número de factura único
    private String generarNumeroFactura() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String random = String.format("%04d", (int)(Math.random() * 10000));
        String numero = "FAC-" + timestamp + "-" + random;

        while (facturaRepository.existsByNumeroFactura(numero)) {
            random = String.format("%04d", (int)(Math.random() * 10000));
            numero = "FAC-" + timestamp + "-" + random;
        }

        return numero;
    }

    //Convertir entidad a DTO
    private FacturaResponse convertirAResponse(Factura factura) {
        return FacturaResponse.builder()
                .id(factura.getId())
                .numeroFactura(factura.getNumeroFactura())
                .pedidoId(factura.getPedidoId())
                .numeroPedido(factura.getNumeroPedido())
                .clienteId(factura.getClienteId())
                .clienteNombre(factura.getClienteNombre())
                .tipoEntrega(factura.getTipoEntrega())
                .distanciaKm(factura.getDistanciaKm())
                .pesoKg(factura.getPesoKg())
                .tarifaBase(factura.getTarifaBase())
                .cargoDistancia(factura.getCargoDistancia())
                .cargoPeso(factura.getCargoPeso())
                .recargoPrioridad(factura.getRecargoPrioridad())
                .descuento(factura.getDescuento())
                .subtotal(factura.getSubtotal())
                .impuestoIVA(factura.getImpuestoIVA())
                .total(factura.getTotal())
                .estado(factura.getEstado())
                .fechaEmision(factura.getFechaEmision())
                .fechaVencimiento(factura.getFechaVencimiento())
                .fechaPago(factura.getFechaPago())
                .metodoPago(factura.getMetodoPago())
                .observaciones(factura.getObservaciones())
                .activo(factura.getActivo())
                .fechaCreacion(factura.getFechaCreacion())
                .fechaActualizacion(factura.getFechaActualizacion())
                .build();
    }
}
