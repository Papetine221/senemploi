import { IUser } from 'app/entities/user/user.model';

export interface ICandidat {
  id: number;
  telephone?: string | null;
  adresse?: string | null;
  cv?: string | null;
  cvContentType?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewCandidat = Omit<ICandidat, 'id'> & { id: null };
