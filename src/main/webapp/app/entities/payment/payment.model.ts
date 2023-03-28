import { IOrder } from 'app/entities/order/order.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

export interface IPayment {
  id: number;
  paymentMethod?: PaymentMethod | null;
  order?: Pick<IOrder, 'id'> | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
