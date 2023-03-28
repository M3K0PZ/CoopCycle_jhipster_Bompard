import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { Disponibility } from 'app/entities/enumerations/disponibility.model';

export interface IProduct {
  id: number;
  price?: number | null;
  disponibility?: Disponibility | null;
  description?: string | null;
  restaurant?: Pick<IRestaurant, 'id'> | null;
}

export type NewProduct = Omit<IProduct, 'id'> & { id: null };
