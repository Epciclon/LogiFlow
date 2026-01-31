package ec.edu.espe.fleet_service.model;

//Enum para estados del repartidor
public enum EstadoRepartidor {
    DISPONIBLE,      //Disponible para asignaciones
    EN_RUTA,         //Realizando entrega
    DESCANSO,        //En descanso
    MANTENIMIENTO,   //Vehículo en mantenimiento
    INACTIVO         //No disponible temporalmente
}
