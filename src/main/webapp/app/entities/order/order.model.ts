import { IUser } from 'app/entities/user/user.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';

export interface IOrder {
  id: number;
  orderId?: number | null;
  client?: Pick<IUser, 'id'> | null;
  restaurant?: Pick<IRestaurant, 'id'> | null;
}

export type NewOrder = Omit<IOrder, 'id'> & { id: null };
