import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILocalisation } from '../localisation.model';
import { LocalisationService } from '../service/localisation.service';

const localisationResolve = (route: ActivatedRouteSnapshot): Observable<null | ILocalisation> => {
  const id = route.params.id;
  if (id) {
    return inject(LocalisationService)
      .find(id)
      .pipe(
        mergeMap((localisation: HttpResponse<ILocalisation>) => {
          if (localisation.body) {
            return of(localisation.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default localisationResolve;
