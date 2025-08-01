import dayjs from 'dayjs/esm';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';

export interface ICandidature {
  id: number;
  lettreMotivation?: string | null;
  datePostulation?: dayjs.Dayjs | null;
  statut?: string | null;
  candidat?: Pick<ICandidat, 'id'> | null;
  offre?: Pick<IOffreEmploi, 'id'> | null;
}

export type NewCandidature = Omit<ICandidature, 'id'> & { id: null };
