import dayjs from 'dayjs/esm';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { StatutCandidature } from 'app/entities/enumerations/statut-candidature.model';

export interface ICandidature {
  id: number;
  lettreMotivation?: string | null;
  datePostulation?: dayjs.Dayjs | null;
  statut?: keyof typeof StatutCandidature | null;
  candidat?: Pick<ICandidat, 'id'> | null;
  offre?: Pick<IOffreEmploi, 'id' | 'titre' | 'description' | 'salaire' | 'datePublication' | 'dateExpiration' | 'recruteur' | 'typeContrat' | 'localisation' | 'competences'> | null;
}

export type NewCandidature = Omit<ICandidature, 'id'> & { id: null };
