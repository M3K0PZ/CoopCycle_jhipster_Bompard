import { IRestaurant, NewRestaurant } from './restaurant.model';

export const sampleWithRequiredData: IRestaurant = {
  id: 59197,
  restaurantId: 66056,
  name: 'Manager',
};

export const sampleWithPartialData: IRestaurant = {
  id: 31031,
  restaurantId: 84617,
  name: 'turn-key',
  description: 'Frozen',
};

export const sampleWithFullData: IRestaurant = {
  id: 37337,
  restaurantId: 64585,
  name: 'Towels Enhanced Champagne-Ardenne',
  description: 'Marcadet Toys',
};

export const sampleWithNewData: NewRestaurant = {
  restaurantId: 99702,
  name: 'Computers',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
