package ec.edu.espe.fleet_service.model;

//Enum para estados del vehículo
public enum EstadoVehiculo {
    DISPONIBLE,      //Listo para uso
    EN_USO,          //Siendo utilizado
    MANTENIMIENTO,   //En reparación o servicio
    FUERA_SERVICIO   //No operativo
}
