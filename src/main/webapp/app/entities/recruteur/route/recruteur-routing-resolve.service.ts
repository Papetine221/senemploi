import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRecruteur } from '../recruteur.model';
import { RecruteurService } from '../service/recruteur.service';

const recruteurResolve = (route: ActivatedRouteSnapshot): Observable<null | IRecruteur> => {
  const id = route.params.id;
  if (id) {
    return inject(RecruteurService)
      .find(id)
      .pipe(
        mergeMap((recruteur: HttpResponse<IRecruteur>) => {
          if (recruteur.body) {
            return of(recruteur.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default recruteurResolve;
