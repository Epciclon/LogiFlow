export const typeDefs = `#graphql
  type Ubicacion {
    latitud: Float
    longitud: Float
  }

  type Repartidor {
    id: ID!
    codigoEmpleado: String
    nombreCompleto: String
    cedula: String
    email: String
    telefono: String
    direccion: String
    estado: String
    entregasCompletadas: Int
    calificacionPromedio: Float
    activo: Boolean
  }

  type Cliente {
    id: String
    nombre: String
  }

  type Pedido {
    id: ID!
    numeroPedido: String
    clienteId: String
    clienteNombre: String
    cliente: Cliente
    repartidorId: String
    repartidorNombre: String
    repartidor: Repartidor
    tipoEntrega: String
    estado: String
    prioridad: String
    direccionOrigen: String
    latitudOrigen: Float
    longitudOrigen: Float
    direccionDestino: String
    latitudDestino: Float
    longitudDestino: Float
    descripcionPaquete: String
    pesoKg: Float
    dimensiones: String
    tarifaBase: Float
    tarifaTotal: Float
    fechaEstimadaEntrega: String
    fechaEntregaReal: String
    observaciones: String
    activo: Boolean
    fechaCreacion: String
    fechaActualizacion: String
    factura: Factura
  }

  type Factura {
    id: ID!
    numeroFactura: String
    pedidoId: String
    numeroPedido: String
    clienteId: String
    clienteNombre: String
    tipoEntrega: String
    distanciaKm: Float
    pesoKg: Float
    tarifaBase: Float
    cargoDistancia: Float
    cargoPeso: Float
    recargoPrioridad: Float
    descuento: Float
    subtotal: Float
    impuestoIVA: Float
    total: Float
    estado: String
    fechaEmision: String
    fechaVencimiento: String
    fechaPago: String
    metodoPago: String
    observaciones: String
    activo: Boolean
    fechaCreacion: String
    fechaActualizacion: String
  }

  input PedidoFilterInput {
    estado: String
    zonaId: String
    repartidorId: String
  }

  type FlotaResumen {
    total: Int
    disponibles: Int
    enRuta: Int
    fueraDeServicio: Int
  }

  type EntregaZona {
    zonaId: String
    cantidad: Int
  }

  type KPIDiario {
    fecha: String
    totalPedidos: Int
    ingresosTotales: Float
    tiempoPromedioEntregaMin: Float
    entregasPorZona: [EntregaZona]
  }

  type Query {
    pedido(id: ID!): Pedido
    pedidos(filtro: PedidoFilterInput): [Pedido]
    factura(id: ID!): Factura
    facturaPorPedido(pedidoId: ID!): Factura
    flotaActiva(zonaId: String): FlotaResumen
    kpiDiario(fecha: String, zonaId: String): KPIDiario
  }
`;
