package ec.edu.espe.fleet_service.repository;

import ec.edu.espe.fleet_service.model.EstadoVehiculo;
import ec.edu.espe.fleet_service.model.TipoVehiculo;
import ec.edu.espe.fleet_service.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Repositorio para gestionar vehículos
@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, UUID> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByTipoVehiculoAndActivoTrue(TipoVehiculo tipoVehiculo);

    List<Vehiculo> findByEstadoAndActivoTrue(EstadoVehiculo estado);

    List<Vehiculo> findByActivoTrue();

    Boolean existsByPlaca(String placa);
}
