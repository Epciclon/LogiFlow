package ec.edu.espe.fleet_service.repository;

import ec.edu.espe.fleet_service.model.EstadoRepartidor;
import ec.edu.espe.fleet_service.model.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Repositorio para gestionar repartidores
@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, UUID> {

    Optional<Repartidor> findByCodigoEmpleado(String codigoEmpleado);

    Optional<Repartidor> findByCedula(String cedula);

    Optional<Repartidor> findByEmail(String email);

    List<Repartidor> findByEstadoAndActivoTrue(EstadoRepartidor estado);

    List<Repartidor> findByActivoTrue();

    Boolean existsByCodigoEmpleado(String codigoEmpleado);

    Boolean existsByCedula(String cedula);

    Boolean existsByEmail(String email);
}
