import { ICooperative, NewCooperative } from './cooperative.model';

export const sampleWithRequiredData: ICooperative = {
  id: 84816,
  cooperativeId: 87664,
};

export const sampleWithPartialData: ICooperative = {
  id: 69989,
  cooperativeId: 73297,
};

export const sampleWithFullData: ICooperative = {
  id: 24269,
  cooperativeId: 93196,
  name: 'Rupee primary',
  area: 'compressing program intuitive',
};

export const sampleWithNewData: NewCooperative = {
  cooperativeId: 50739,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
