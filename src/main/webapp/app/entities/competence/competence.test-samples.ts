import { ICompetence, NewCompetence } from './competence.model';

export const sampleWithRequiredData: ICompetence = {
  id: 10158,
  nom: 'occuper',
};

export const sampleWithPartialData: ICompetence = {
  id: 21266,
  nom: 'camarade pauvre',
};

export const sampleWithFullData: ICompetence = {
  id: 30196,
  nom: 'actionnaire assez',
};

export const sampleWithNewData: NewCompetence = {
  nom: 'triompher',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
