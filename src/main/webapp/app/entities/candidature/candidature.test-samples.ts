import dayjs from 'dayjs/esm';

import { ICandidature, NewCandidature } from './candidature.model';

export const sampleWithRequiredData: ICandidature = {
  id: 28558,
  datePostulation: dayjs('2025-08-01T00:48'),
};

export const sampleWithPartialData: ICandidature = {
  id: 13073,
  lettreMotivation: '../fake-data/blob/hipster.txt',
  datePostulation: dayjs('2025-07-31T21:10'),
};

export const sampleWithFullData: ICandidature = {
  id: 23153,
  lettreMotivation: '../fake-data/blob/hipster.txt',
  datePostulation: dayjs('2025-08-01T14:40'),
  statut: 'ferme après aussi',
};

export const sampleWithNewData: NewCandidature = {
  datePostulation: dayjs('2025-07-31T18:45'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
