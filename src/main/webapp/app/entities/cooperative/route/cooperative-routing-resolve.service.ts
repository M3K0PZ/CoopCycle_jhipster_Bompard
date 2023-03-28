import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICooperative } from '../cooperative.model';
import { CooperativeService } from '../service/cooperative.service';

@Injectable({ providedIn: 'root' })
export class CooperativeRoutingResolveService implements Resolve<ICooperative | null> {
  constructor(protected service: CooperativeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICooperative | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cooperative: HttpResponse<ICooperative>) => {
          if (cooperative.body) {
            return of(cooperative.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
