package ec.edu.espe.billing_service.model;

//Enum para estados de la factura
public enum EstadoFactura {
    BORRADOR,       //Factura creada pero no finalizada
    PENDIENTE,      //Pendiente de pago
    PAGADA,         //Pago completado
    VENCIDA,        //Plazo de pago vencido
    CANCELADA,      //Factura cancelada
    ANULADA         //Factura anulada
}
