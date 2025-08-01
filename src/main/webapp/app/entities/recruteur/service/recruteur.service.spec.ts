import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IRecruteur } from '../recruteur.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../recruteur.test-samples';

import { RecruteurService } from './recruteur.service';

const requireRestSample: IRecruteur = {
  ...sampleWithRequiredData,
};

describe('Recruteur Service', () => {
  let service: RecruteurService;
  let httpMock: HttpTestingController;
  let expectedResult: IRecruteur | IRecruteur[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RecruteurService);
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

    it('should create a Recruteur', () => {
      const recruteur = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(recruteur).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Recruteur', () => {
      const recruteur = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(recruteur).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Recruteur', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Recruteur', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Recruteur', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRecruteurToCollectionIfMissing', () => {
      it('should add a Recruteur to an empty array', () => {
        const recruteur: IRecruteur = sampleWithRequiredData;
        expectedResult = service.addRecruteurToCollectionIfMissing([], recruteur);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(recruteur);
      });

      it('should not add a Recruteur to an array that contains it', () => {
        const recruteur: IRecruteur = sampleWithRequiredData;
        const recruteurCollection: IRecruteur[] = [
          {
            ...recruteur,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRecruteurToCollectionIfMissing(recruteurCollection, recruteur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Recruteur to an array that doesn't contain it", () => {
        const recruteur: IRecruteur = sampleWithRequiredData;
        const recruteurCollection: IRecruteur[] = [sampleWithPartialData];
        expectedResult = service.addRecruteurToCollectionIfMissing(recruteurCollection, recruteur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(recruteur);
      });

      it('should add only unique Recruteur to an array', () => {
        const recruteurArray: IRecruteur[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const recruteurCollection: IRecruteur[] = [sampleWithRequiredData];
        expectedResult = service.addRecruteurToCollectionIfMissing(recruteurCollection, ...recruteurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const recruteur: IRecruteur = sampleWithRequiredData;
        const recruteur2: IRecruteur = sampleWithPartialData;
        expectedResult = service.addRecruteurToCollectionIfMissing([], recruteur, recruteur2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(recruteur);
        expect(expectedResult).toContain(recruteur2);
      });

      it('should accept null and undefined values', () => {
        const recruteur: IRecruteur = sampleWithRequiredData;
        expectedResult = service.addRecruteurToCollectionIfMissing([], null, recruteur, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(recruteur);
      });

      it('should return initial array if no Recruteur is added', () => {
        const recruteurCollection: IRecruteur[] = [sampleWithRequiredData];
        expectedResult = service.addRecruteurToCollectionIfMissing(recruteurCollection, undefined, null);
        expect(expectedResult).toEqual(recruteurCollection);
      });
    });

    describe('compareRecruteur', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRecruteur(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4268 };
        const entity2 = null;

        const compareResult1 = service.compareRecruteur(entity1, entity2);
        const compareResult2 = service.compareRecruteur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4268 };
        const entity2 = { id: 10028 };

        const compareResult1 = service.compareRecruteur(entity1, entity2);
        const compareResult2 = service.compareRecruteur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 4268 };
        const entity2 = { id: 4268 };

        const compareResult1 = service.compareRecruteur(entity1, entity2);
        const compareResult2 = service.compareRecruteur(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
