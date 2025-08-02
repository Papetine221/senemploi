import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ILocalisation } from '../localisation.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../localisation.test-samples';

import { LocalisationService } from './localisation.service';

const requireRestSample: ILocalisation = {
  ...sampleWithRequiredData,
};

describe('Localisation Service', () => {
  let service: LocalisationService;
  let httpMock: HttpTestingController;
  let expectedResult: ILocalisation | ILocalisation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LocalisationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Localisation', () => {
      const localisation = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(localisation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Localisation', () => {
      const localisation = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(localisation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Localisation', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Localisation', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Localisation', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addLocalisationToCollectionIfMissing', () => {
      it('should add a Localisation to an empty array', () => {
        const localisation: ILocalisation = sampleWithRequiredData;
        expectedResult = service.addLocalisationToCollectionIfMissing([], localisation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(localisation);
      });

      it('should not add a Localisation to an array that contains it', () => {
        const localisation: ILocalisation = sampleWithRequiredData;
        const localisationCollection: ILocalisation[] = [
          {
            ...localisation,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLocalisationToCollectionIfMissing(localisationCollection, localisation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Localisation to an array that doesn't contain it", () => {
        const localisation: ILocalisation = sampleWithRequiredData;
        const localisationCollection: ILocalisation[] = [sampleWithPartialData];
        expectedResult = service.addLocalisationToCollectionIfMissing(localisationCollection, localisation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(localisation);
      });

      it('should add only unique Localisation to an array', () => {
        const localisationArray: ILocalisation[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const localisationCollection: ILocalisation[] = [sampleWithRequiredData];
        expectedResult = service.addLocalisationToCollectionIfMissing(localisationCollection, ...localisationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const localisation: ILocalisation = sampleWithRequiredData;
        const localisation2: ILocalisation = sampleWithPartialData;
        expectedResult = service.addLocalisationToCollectionIfMissing([], localisation, localisation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(localisation);
        expect(expectedResult).toContain(localisation2);
      });

      it('should accept null and undefined values', () => {
        const localisation: ILocalisation = sampleWithRequiredData;
        expectedResult = service.addLocalisationToCollectionIfMissing([], null, localisation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(localisation);
      });

      it('should return initial array if no Localisation is added', () => {
        const localisationCollection: ILocalisation[] = [sampleWithRequiredData];
        expectedResult = service.addLocalisationToCollectionIfMissing(localisationCollection, undefined, null);
        expect(expectedResult).toEqual(localisationCollection);
      });
    });

    describe('compareLocalisation', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLocalisation(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6239 };
        const entity2 = null;

        const compareResult1 = service.compareLocalisation(entity1, entity2);
        const compareResult2 = service.compareLocalisation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6239 };
        const entity2 = { id: 18214 };

        const compareResult1 = service.compareLocalisation(entity1, entity2);
        const compareResult2 = service.compareLocalisation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 6239 };
        const entity2 = { id: 6239 };

        const compareResult1 = service.compareLocalisation(entity1, entity2);
        const compareResult2 = service.compareLocalisation(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
