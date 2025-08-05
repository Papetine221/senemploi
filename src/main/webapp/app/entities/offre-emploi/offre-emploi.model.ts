import dayjs from 'dayjs/esm';
import { IRecruteur } from 'app/entities/recruteur/recruteur.model';
import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model';
import { ILocalisation } from 'app/entities/localisation/localisation.model';
import { ICompetence } from 'app/entities/competence/competence.model';

export interface IOffreEmploi {
  id: number;
  titre?: string | null;
  description?: string | null;
  salaire?: number | null;
  datePublication?: dayjs.Dayjs | null;
  dateExpiration?: dayjs.Dayjs | null;
  recruteur?: Pick<IRecruteur, 'id' | 'nomEntreprise'> | null;
  typeContrat?: Pick<ITypeContrat, 'id' | 'nom'> | null;
  localisation?: Pick<ILocalisation, 'id' | 'ville' | 'region'> | null;
  competences?: Pick<ICompetence, 'id' | 'nom'>[] | null;
}

export type NewOffreEmploi = Omit<IOffreEmploi, 'id'> & { id: null };
