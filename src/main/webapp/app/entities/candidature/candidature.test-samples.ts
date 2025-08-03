import dayjs from 'dayjs/esm';

import { ICandidature, NewCandidature } from './candidature.model';

export const sampleWithRequiredData: ICandidature = {
  id: 28558,
  datePostulation: dayjs('2025-08-01T00:48'),
  statut: 'EN_ATTENTE',
};

export const sampleWithPartialData: ICandidature = {
  id: 27809,
  lettreMotivation: '../fake-data/blob/hipster.txt',
  datePostulation: dayjs('2025-08-01T00:32'),
  statut: 'EN_ATTENTE',
};

export const sampleWithFullData: ICandidature = {
  id: 23153,
  lettreMotivation: '../fake-data/blob/hipster.txt',
  datePostulation: dayjs('2025-08-01T14:40'),
  statut: 'REFUSEE',
};

export const sampleWithNewData: NewCandidature = {
  datePostulation: dayjs('2025-07-31T18:45'),
  statut: 'REFUSEE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
