package ec.edu.espe.auth_service.model;

//Enumeración para los roles del sistema LogiFlow
public enum RolEnum {
    CLIENTE,           //Clientes que solicitan entregas
    REPARTIDOR,        //Repartidores (motorizado, vehículo liviano, camión)
    SUPERVISOR,        //Supervisores de operaciones
    GERENTE,           //Gerentes de la empresa
    ADMINISTRADOR      //Administradores del sistema
}
