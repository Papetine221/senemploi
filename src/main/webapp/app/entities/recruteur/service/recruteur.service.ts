import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRecruteur, NewRecruteur } from '../recruteur.model';

export type PartialUpdateRecruteur = Partial<IRecruteur> & Pick<IRecruteur, 'id'>;

export type EntityResponseType = HttpResponse<IRecruteur>;
export type EntityArrayResponseType = HttpResponse<IRecruteur[]>;

@Injectable({ providedIn: 'root' })
export class RecruteurService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/recruteurs');

  create(recruteur: NewRecruteur): Observable<EntityResponseType> {
    return this.http.post<IRecruteur>(this.resourceUrl, recruteur, { observe: 'response' });
  }

  update(recruteur: IRecruteur): Observable<EntityResponseType> {
    return this.http.put<IRecruteur>(`${this.resourceUrl}/${this.getRecruteurIdentifier(recruteur)}`, recruteur, { observe: 'response' });
  }

  partialUpdate(recruteur: PartialUpdateRecruteur): Observable<EntityResponseType> {
    return this.http.patch<IRecruteur>(`${this.resourceUrl}/${this.getRecruteurIdentifier(recruteur)}`, recruteur, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRecruteur>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRecruteur[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRecruteurIdentifier(recruteur: Pick<IRecruteur, 'id'>): number {
    return recruteur.id;
  }

  compareRecruteur(o1: Pick<IRecruteur, 'id'> | null, o2: Pick<IRecruteur, 'id'> | null): boolean {
    return o1 && o2 ? this.getRecruteurIdentifier(o1) === this.getRecruteurIdentifier(o2) : o1 === o2;
  }

  addRecruteurToCollectionIfMissing<Type extends Pick<IRecruteur, 'id'>>(
    recruteurCollection: Type[],
    ...recruteursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const recruteurs: Type[] = recruteursToCheck.filter(isPresent);
    if (recruteurs.length > 0) {
      const recruteurCollectionIdentifiers = recruteurCollection.map(recruteurItem => this.getRecruteurIdentifier(recruteurItem));
      const recruteursToAdd = recruteurs.filter(recruteurItem => {
        const recruteurIdentifier = this.getRecruteurIdentifier(recruteurItem);
        if (recruteurCollectionIdentifiers.includes(recruteurIdentifier)) {
          return false;
        }
        recruteurCollectionIdentifiers.push(recruteurIdentifier);
        return true;
      });
      return [...recruteursToAdd, ...recruteurCollection];
    }
    return recruteurCollection;
  }
}
