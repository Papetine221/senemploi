import { IUser } from 'app/entities/user/user.model';

export interface IRecruteur {
  id: number;
  nomEntreprise?: string | null;
  secteur?: string | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewRecruteur = Omit<IRecruteur, 'id'> & { id: null };
