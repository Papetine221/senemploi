import dayjs from 'dayjs/esm';
import { IRecruteur } from 'app/entities/recruteur/recruteur.model';

export interface IOffreEmploi {
  id: number;
  titre?: string | null;
  description?: string | null;
  salaire?: number | null;
  datePublication?: dayjs.Dayjs | null;
  dateExpiration?: dayjs.Dayjs | null;
  recruteur?: Pick<IRecruteur, 'id'> | null;
}

export type NewOffreEmploi = Omit<IOffreEmploi, 'id'> & { id: null };
