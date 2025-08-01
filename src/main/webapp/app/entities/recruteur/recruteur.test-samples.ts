import { IRecruteur, NewRecruteur } from './recruteur.model';

export const sampleWithRequiredData: IRecruteur = {
  id: 7612,
  nomEntreprise: 'responsable',
};

export const sampleWithPartialData: IRecruteur = {
  id: 17427,
  nomEntreprise: 'oh',
};

export const sampleWithFullData: IRecruteur = {
  id: 22999,
  nomEntreprise: 'à partir de calmer',
  secteur: 'du moment que puis',
};

export const sampleWithNewData: NewRecruteur = {
  nomEntreprise: 'blablabla',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
