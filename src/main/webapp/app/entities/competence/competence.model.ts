import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';

export interface ICompetence {
  id: number;
  nom?: string | null;
  offreEmplois?: Pick<IOffreEmploi, 'id'>[] | null;
}

export type NewCompetence = Omit<ICompetence, 'id'> & { id: null };
