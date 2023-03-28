import { IOrder, NewOrder } from './order.model';

export const sampleWithRequiredData: IOrder = {
  id: 47761,
  orderId: 34511,
};

export const sampleWithPartialData: IOrder = {
  id: 70907,
  orderId: 52696,
};

export const sampleWithFullData: IOrder = {
  id: 69128,
  orderId: 34054,
};

export const sampleWithNewData: NewOrder = {
  orderId: 47091,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
