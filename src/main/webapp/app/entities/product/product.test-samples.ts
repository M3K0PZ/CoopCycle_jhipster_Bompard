import { Disponibility } from 'app/entities/enumerations/disponibility.model';

import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 77672,
  price: 311135291,
  disponibility: Disponibility['AVAILABLE'],
};

export const sampleWithPartialData: IProduct = {
  id: 82518,
  price: 963073330,
  disponibility: Disponibility['UNAVAILABLE'],
};

export const sampleWithFullData: IProduct = {
  id: 34739,
  price: 943626396,
  disponibility: Disponibility['AVAILABLE'],
  description: 'Programmable Networked deliver',
};

export const sampleWithNewData: NewProduct = {
  price: 396411033,
  disponibility: Disponibility['UNAVAILABLE'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
