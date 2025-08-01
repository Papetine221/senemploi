import { ICandidat, NewCandidat } from './candidat.model';

export const sampleWithRequiredData: ICandidat = {
  id: 18880,
};

export const sampleWithPartialData: ICandidat = {
  id: 29576,
  telephone: '0593971751',
  adresse: 'collègue porte-parole super',
  cv: '../fake-data/blob/hipster.png',
  cvContentType: 'unknown',
};

export const sampleWithFullData: ICandidat = {
  id: 11710,
  telephone: '+33 579711841',
  adresse: 'rectorat',
  cv: '../fake-data/blob/hipster.png',
  cvContentType: 'unknown',
};

export const sampleWithNewData: NewCandidat = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
