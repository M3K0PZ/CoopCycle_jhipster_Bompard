import { BasketState } from 'app/entities/enumerations/basket-state.model';

import { IBasket, NewBasket } from './basket.model';

export const sampleWithRequiredData: IBasket = {
  id: 81422,
  basketId: 98805,
  basketState: BasketState['VALIDATED'],
};

export const sampleWithPartialData: IBasket = {
  id: 20774,
  basketId: 21103,
  basketState: BasketState['NOTFINISHED'],
};

export const sampleWithFullData: IBasket = {
  id: 19115,
  basketId: 96448,
  basketState: BasketState['VALIDATED'],
};

export const sampleWithNewData: NewBasket = {
  basketId: 48514,
  basketState: BasketState['PAID'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
