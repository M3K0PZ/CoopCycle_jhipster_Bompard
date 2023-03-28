import { ICooperative } from 'app/entities/cooperative/cooperative.model';

export interface IRestaurant {
  id: number;
  restaurantId?: number | null;
  name?: string | null;
  description?: string | null;
  cooperatives?: Pick<ICooperative, 'id'>[] | null;
}

export type NewRestaurant = Omit<IRestaurant, 'id'> & { id: null };
