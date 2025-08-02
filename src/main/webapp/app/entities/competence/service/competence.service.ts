import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICompetence, NewCompetence } from '../competence.model';

export type PartialUpdateCompetence = Partial<ICompetence> & Pick<ICompetence, 'id'>;

export type EntityResponseType = HttpResponse<ICompetence>;
export type EntityArrayResponseType = HttpResponse<ICompetence[]>;

@Injectable({ providedIn: 'root' })
export class CompetenceService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/competences');

  create(competence: NewCompetence): Observable<EntityResponseType> {
    return this.http.post<ICompetence>(this.resourceUrl, competence, { observe: 'response' });
  }

  update(competence: ICompetence): Observable<EntityResponseType> {
    return this.http.put<ICompetence>(`${this.resourceUrl}/${this.getCompetenceIdentifier(competence)}`, competence, {
      observe: 'response',
    });
  }

  partialUpdate(competence: PartialUpdateCompetence): Observable<EntityResponseType> {
    return this.http.patch<ICompetence>(`${this.resourceUrl}/${this.getCompetenceIdentifier(competence)}`, competence, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICompetence>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICompetence[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCompetenceIdentifier(competence: Pick<ICompetence, 'id'>): number {
    return competence.id;
  }

  compareCompetence(o1: Pick<ICompetence, 'id'> | null, o2: Pick<ICompetence, 'id'> | null): boolean {
    return o1 && o2 ? this.getCompetenceIdentifier(o1) === this.getCompetenceIdentifier(o2) : o1 === o2;
  }

  addCompetenceToCollectionIfMissing<Type extends Pick<ICompetence, 'id'>>(
    competenceCollection: Type[],
    ...competencesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const competences: Type[] = competencesToCheck.filter(isPresent);
    if (competences.length > 0) {
      const competenceCollectionIdentifiers = competenceCollection.map(competenceItem => this.getCompetenceIdentifier(competenceItem));
      const competencesToAdd = competences.filter(competenceItem => {
        const competenceIdentifier = this.getCompetenceIdentifier(competenceItem);
        if (competenceCollectionIdentifiers.includes(competenceIdentifier)) {
          return false;
        }
        competenceCollectionIdentifiers.push(competenceIdentifier);
        return true;
      });
      return [...competencesToAdd, ...competenceCollection];
    }
    return competenceCollection;
  }
}
