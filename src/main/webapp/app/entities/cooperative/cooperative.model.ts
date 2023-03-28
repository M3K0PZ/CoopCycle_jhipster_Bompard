import { IRestaurant } from 'app/entities/restaurant/restaurant.model';

export interface ICooperative {
  id: number;
  cooperativeId?: number | null;
  name?: string | null;
  area?: string | null;
  restaurants?: Pick<IRestaurant, 'id'>[] | null;
}

export type NewCooperative = Omit<ICooperative, 'id'> & { id: null };
