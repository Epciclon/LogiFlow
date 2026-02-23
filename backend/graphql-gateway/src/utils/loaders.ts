import DataLoader from 'dataloader';
import { DeliveryDataSource } from '../services/delivery.service';

export function createLoaders(token?: string) {
  return {
    repartidorLoader: new DataLoader(async (ids: readonly string[]) => {
      // Implementación básica de batch loading si fuera necesario
      // Por ahora devuelve promesas individuales usando Promise.all
      // En producción, esto debería usar un endpoint de 'getManyByIds' si existe
      return Promise.all(ids.map(id => DeliveryDataSource.getRepartidorById(id, token)));
    }),
  };
}
