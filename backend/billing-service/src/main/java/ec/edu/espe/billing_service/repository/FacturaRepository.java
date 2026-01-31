package ec.edu.espe.billing_service.repository;

import ec.edu.espe.billing_service.model.EstadoFactura;
import ec.edu.espe.billing_service.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Repositorio para gestionar facturas
@Repository
public interface FacturaRepository extends JpaRepository<Factura, UUID> {

    Optional<Factura> findByNumeroFactura(String numeroFactura);

    Optional<Factura> findByPedidoId(UUID pedidoId);

    List<Factura> findByClienteIdAndActivoTrue(UUID clienteId);

    List<Factura> findByEstadoAndActivoTrue(EstadoFactura estado);

    List<Factura> findByActivoTrue();

    Boolean existsByNumeroFactura(String numeroFactura);

    Boolean existsByPedidoId(UUID pedidoId);
}
