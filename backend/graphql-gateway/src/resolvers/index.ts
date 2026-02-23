import { DeliveryDataSource } from '../services/delivery.service';

export const resolvers = {
  Query: {
    pedido: async (_: any, { id }: { id: string }, context: any) => {
      return DeliveryDataSource.getPedidoById(id, context.token);
    },

    pedidos: async (_: any, { filtro }: { filtro: any }, context: any) => {
      return DeliveryDataSource.getPedidos(filtro, context.token);
    },

    factura: async (_: any, { id }: { id: string }, context: any) => {
      return DeliveryDataSource.getFacturaById(id, context.token);
    },

    facturaPorPedido: async (_: any, { pedidoId }: { pedidoId: string }, context: any) => {
      return DeliveryDataSource.getFacturaByPedidoId(pedidoId, context.token);
    },

    flotaActiva: async (_: any, { zonaId }: { zonaId: string }, context: any) => {
      return DeliveryDataSource.getFlotaResumen(zonaId, context.token);
    },

    kpiDiario: async (_: any, { fecha, zonaId }: { fecha: string, zonaId?: string }, context: any) => {
      return DeliveryDataSource.getKPIDiario(fecha, zonaId, context.token);
    }
  },

  Pedido: {
    cliente: (parent: any) => {
      return parent.cliente || { id: parent.clienteId, nombre: parent.clienteNombre || 'Cliente' };
    },

    repartidor: async (parent: any, _: any, { loaders, token }: any) => {
      if (!parent.repartidorId) return null;
      return loaders.repartidorLoader.load(parent.repartidorId);
    },

    factura: async (parent: any, _: any, context: any) => {
      return DeliveryDataSource.getFacturaByPedidoId(parent.id, context.token);
    }
  },

  Repartidor: {
    // Mapear campos si es necesario
  }
};
