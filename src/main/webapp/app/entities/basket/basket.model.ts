import { IUser } from 'app/entities/user/user.model';
import { IOrder } from 'app/entities/order/order.model';
import { BasketState } from 'app/entities/enumerations/basket-state.model';

export interface IBasket {
  id: number;
  basketId?: number | null;
  basketState?: BasketState | null;
  user?: Pick<IUser, 'id'> | null;
  order?: Pick<IOrder, 'id'> | null;
  customer?: Pick<IUser, 'id'> | null;
}

export type NewBasket = Omit<IBasket, 'id'> & { id: null };
