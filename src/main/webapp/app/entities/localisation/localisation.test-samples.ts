import { ILocalisation, NewLocalisation } from './localisation.model';

export const sampleWithRequiredData: ILocalisation = {
  id: 24192,
  region: 'vroum sans que',
  ville: 'écraser vis-à-vie de',
};

export const sampleWithPartialData: ILocalisation = {
  id: 29394,
  region: 'pauvre jadis',
  ville: 'ouille alors que aussitôt que',
};

export const sampleWithFullData: ILocalisation = {
  id: 26734,
  region: 'psitt baigner tellement',
  departement: 'membre titulaire',
  ville: 'chut',
};

export const sampleWithNewData: NewLocalisation = {
  region: 'corps enseignant',
  ville: 'cocorico lorsque avaler',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
