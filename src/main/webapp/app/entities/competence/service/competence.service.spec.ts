import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICompetence } from '../competence.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../competence.test-samples';

import { CompetenceService } from './competence.service';

const requireRestSample: ICompetence = {
  ...sampleWithRequiredData,
};

describe('Competence Service', () => {
  let service: CompetenceService;
  let httpMock: HttpTestingController;
  let expectedResult: ICompetence | ICompetence[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CompetenceService);
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

    it('should create a Competence', () => {
      const competence = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(competence).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Competence', () => {
      const competence = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(competence).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Competence', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Competence', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Competence', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCompetenceToCollectionIfMissing', () => {
      it('should add a Competence to an empty array', () => {
        const competence: ICompetence = sampleWithRequiredData;
        expectedResult = service.addCompetenceToCollectionIfMissing([], competence);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(competence);
      });

      it('should not add a Competence to an array that contains it', () => {
        const competence: ICompetence = sampleWithRequiredData;
        const competenceCollection: ICompetence[] = [
          {
            ...competence,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCompetenceToCollectionIfMissing(competenceCollection, competence);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Competence to an array that doesn't contain it", () => {
        const competence: ICompetence = sampleWithRequiredData;
        const competenceCollection: ICompetence[] = [sampleWithPartialData];
        expectedResult = service.addCompetenceToCollectionIfMissing(competenceCollection, competence);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(competence);
      });

      it('should add only unique Competence to an array', () => {
        const competenceArray: ICompetence[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const competenceCollection: ICompetence[] = [sampleWithRequiredData];
        expectedResult = service.addCompetenceToCollectionIfMissing(competenceCollection, ...competenceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const competence: ICompetence = sampleWithRequiredData;
        const competence2: ICompetence = sampleWithPartialData;
        expectedResult = service.addCompetenceToCollectionIfMissing([], competence, competence2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(competence);
        expect(expectedResult).toContain(competence2);
      });

      it('should accept null and undefined values', () => {
        const competence: ICompetence = sampleWithRequiredData;
        expectedResult = service.addCompetenceToCollectionIfMissing([], null, competence, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(competence);
      });

      it('should return initial array if no Competence is added', () => {
        const competenceCollection: ICompetence[] = [sampleWithRequiredData];
        expectedResult = service.addCompetenceToCollectionIfMissing(competenceCollection, undefined, null);
        expect(expectedResult).toEqual(competenceCollection);
      });
    });

    describe('compareCompetence', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCompetence(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15671 };
        const entity2 = null;

        const compareResult1 = service.compareCompetence(entity1, entity2);
        const compareResult2 = service.compareCompetence(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15671 };
        const entity2 = { id: 29641 };

        const compareResult1 = service.compareCompetence(entity1, entity2);
        const compareResult2 = service.compareCompetence(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15671 };
        const entity2 = { id: 15671 };

        const compareResult1 = service.compareCompetence(entity1, entity2);
        const compareResult2 = service.compareCompetence(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
