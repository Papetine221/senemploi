import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICompetence } from '../competence.model';
import { CompetenceService } from '../service/competence.service';

const competenceResolve = (route: ActivatedRouteSnapshot): Observable<null | ICompetence> => {
  const id = route.params.id;
  if (id) {
    return inject(CompetenceService)
      .find(id)
      .pipe(
        mergeMap((competence: HttpResponse<ICompetence>) => {
          if (competence.body) {
            return of(competence.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default competenceResolve;
