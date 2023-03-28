import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBasket, NewBasket } from '../basket.model';

export type PartialUpdateBasket = Partial<IBasket> & Pick<IBasket, 'id'>;

export type EntityResponseType = HttpResponse<IBasket>;
export type EntityArrayResponseType = HttpResponse<IBasket[]>;

@Injectable({ providedIn: 'root' })
export class BasketService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/baskets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(basket: NewBasket): Observable<EntityResponseType> {
    return this.http.post<IBasket>(this.resourceUrl, basket, { observe: 'response' });
  }

  update(basket: IBasket): Observable<EntityResponseType> {
    return this.http.put<IBasket>(`${this.resourceUrl}/${this.getBasketIdentifier(basket)}`, basket, { observe: 'response' });
  }

  partialUpdate(basket: PartialUpdateBasket): Observable<EntityResponseType> {
    return this.http.patch<IBasket>(`${this.resourceUrl}/${this.getBasketIdentifier(basket)}`, basket, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBasket>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBasket[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBasketIdentifier(basket: Pick<IBasket, 'id'>): number {
    return basket.id;
  }

  compareBasket(o1: Pick<IBasket, 'id'> | null, o2: Pick<IBasket, 'id'> | null): boolean {
    return o1 && o2 ? this.getBasketIdentifier(o1) === this.getBasketIdentifier(o2) : o1 === o2;
  }

  addBasketToCollectionIfMissing<Type extends Pick<IBasket, 'id'>>(
    basketCollection: Type[],
    ...basketsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const baskets: Type[] = basketsToCheck.filter(isPresent);
    if (baskets.length > 0) {
      const basketCollectionIdentifiers = basketCollection.map(basketItem => this.getBasketIdentifier(basketItem)!);
      const basketsToAdd = baskets.filter(basketItem => {
        const basketIdentifier = this.getBasketIdentifier(basketItem);
        if (basketCollectionIdentifiers.includes(basketIdentifier)) {
          return false;
        }
        basketCollectionIdentifiers.push(basketIdentifier);
        return true;
      });
      return [...basketsToAdd, ...basketCollection];
    }
    return basketCollection;
  }
}
