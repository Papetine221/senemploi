import dayjs from 'dayjs/esm';

import { IOffreEmploi, NewOffreEmploi } from './offre-emploi.model';

export const sampleWithRequiredData: IOffreEmploi = {
  id: 11534,
  titre: 'en face de',
  description: '../fake-data/blob/hipster.txt',
  datePublication: dayjs('2025-07-31T18:29'),
  dateExpiration: dayjs('2025-08-01T02:40'),
};

export const sampleWithPartialData: IOffreEmploi = {
  id: 7123,
  titre: 'encore hé plutôt',
  description: '../fake-data/blob/hipster.txt',
  salaire: 20740.34,
  datePublication: dayjs('2025-07-31T16:45'),
  dateExpiration: dayjs('2025-08-01T06:04'),
};

export const sampleWithFullData: IOffreEmploi = {
  id: 19852,
  titre: 'fumer ha ha',
  description: '../fake-data/blob/hipster.txt',
  salaire: 17211.85,
  datePublication: dayjs('2025-08-01T14:29'),
  dateExpiration: dayjs('2025-08-01T09:09'),
};

export const sampleWithNewData: NewOffreEmploi = {
  titre: 'croâ à même parce que',
  description: '../fake-data/blob/hipster.txt',
  datePublication: dayjs('2025-08-01T13:22'),
  dateExpiration: dayjs('2025-07-31T19:51'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
