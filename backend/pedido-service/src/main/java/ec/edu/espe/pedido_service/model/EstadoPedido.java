package ec.edu.espe.pedido_service.model;

//Estados del pedido en su ciclo de vida
public enum EstadoPedido {
    RECIBIDO,           //Pedido recibido en el sistema
    EN_PREPARACION,     //Pedido en preparación en el almacén
    ASIGNADO,           //Pedido asignado a un repartidor
    EN_RUTA,            //Repartidor en camino
    ENTREGADO,          //Pedido entregado exitosamente
    CANCELADO,          //Pedido cancelado
    DEVUELTO            //Pedido devuelto
}
