package ec.edu.espe.pedido_service.model;

//Tipos de entrega según modalidad de servicio
public enum TipoEntrega {
    URBANA_RAPIDA,      //Entregas urbanas rápidas (última milla) mediante motorizados
    INTERMUNICIPAL,     //Entregas intermunicipales dentro de la provincia con vehículos livianos
    NACIONAL            //Entregas nacionales mediante furgonetas o camiones medianos/grandes
}
