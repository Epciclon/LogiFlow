package ec.edu.espe.pedido_service.repository;

import ec.edu.espe.pedido_service.model.EstadoPedido;
import ec.edu.espe.pedido_service.model.Pedido;
import ec.edu.espe.pedido_service.model.TipoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Repositorio para gestionar pedidos
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByClienteIdAndActivoTrue(UUID clienteId);

    List<Pedido> findByRepartidorIdAndActivoTrue(UUID repartidorId);

    List<Pedido> findByEstadoAndActivoTrue(EstadoPedido estado);

    List<Pedido> findByTipoEntregaAndActivoTrue(TipoEntrega tipoEntrega);

    List<Pedido> findByActivoTrue();

    Boolean existsByNumeroPedido(String numeroPedido);
}
