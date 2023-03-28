import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 47537,
  paymentMethod: PaymentMethod['PAYPAL'],
};

export const sampleWithPartialData: IPayment = {
  id: 74412,
  paymentMethod: PaymentMethod['BITCOIN'],
};

export const sampleWithFullData: IPayment = {
  id: 34993,
  paymentMethod: PaymentMethod['IZLY'],
};

export const sampleWithNewData: NewPayment = {
  paymentMethod: PaymentMethod['BITCOIN'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
