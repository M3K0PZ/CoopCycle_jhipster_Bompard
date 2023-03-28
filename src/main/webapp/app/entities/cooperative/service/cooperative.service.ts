import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICooperative, NewCooperative } from '../cooperative.model';

export type PartialUpdateCooperative = Partial<ICooperative> & Pick<ICooperative, 'id'>;

export type EntityResponseType = HttpResponse<ICooperative>;
export type EntityArrayResponseType = HttpResponse<ICooperative[]>;

@Injectable({ providedIn: 'root' })
export class CooperativeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cooperatives');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(cooperative: NewCooperative): Observable<EntityResponseType> {
    return this.http.post<ICooperative>(this.resourceUrl, cooperative, { observe: 'response' });
  }

  update(cooperative: ICooperative): Observable<EntityResponseType> {
    return this.http.put<ICooperative>(`${this.resourceUrl}/${this.getCooperativeIdentifier(cooperative)}`, cooperative, {
      observe: 'response',
    });
  }

  partialUpdate(cooperative: PartialUpdateCooperative): Observable<EntityResponseType> {
    return this.http.patch<ICooperative>(`${this.resourceUrl}/${this.getCooperativeIdentifier(cooperative)}`, cooperative, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICooperative>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICooperative[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCooperativeIdentifier(cooperative: Pick<ICooperative, 'id'>): number {
    return cooperative.id;
  }

  compareCooperative(o1: Pick<ICooperative, 'id'> | null, o2: Pick<ICooperative, 'id'> | null): boolean {
    return o1 && o2 ? this.getCooperativeIdentifier(o1) === this.getCooperativeIdentifier(o2) : o1 === o2;
  }

  addCooperativeToCollectionIfMissing<Type extends Pick<ICooperative, 'id'>>(
    cooperativeCollection: Type[],
    ...cooperativesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cooperatives: Type[] = cooperativesToCheck.filter(isPresent);
    if (cooperatives.length > 0) {
      const cooperativeCollectionIdentifiers = cooperativeCollection.map(
        cooperativeItem => this.getCooperativeIdentifier(cooperativeItem)!
      );
      const cooperativesToAdd = cooperatives.filter(cooperativeItem => {
        const cooperativeIdentifier = this.getCooperativeIdentifier(cooperativeItem);
        if (cooperativeCollectionIdentifiers.includes(cooperativeIdentifier)) {
          return false;
        }
        cooperativeCollectionIdentifiers.push(cooperativeIdentifier);
        return true;
      });
      return [...cooperativesToAdd, ...cooperativeCollection];
    }
    return cooperativeCollection;
  }
}
