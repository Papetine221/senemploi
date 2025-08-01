import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../recruteur.test-samples';

import { RecruteurFormService } from './recruteur-form.service';

describe('Recruteur Form Service', () => {
  let service: RecruteurFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RecruteurFormService);
  });

  describe('Service methods', () => {
    describe('createRecruteurFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRecruteurFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nomEntreprise: expect.any(Object),
            secteur: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IRecruteur should create a new form with FormGroup', () => {
        const formGroup = service.createRecruteurFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nomEntreprise: expect.any(Object),
            secteur: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getRecruteur', () => {
      it('should return NewRecruteur for default Recruteur initial value', () => {
        const formGroup = service.createRecruteurFormGroup(sampleWithNewData);

        const recruteur = service.getRecruteur(formGroup) as any;

        expect(recruteur).toMatchObject(sampleWithNewData);
      });

      it('should return NewRecruteur for empty Recruteur initial value', () => {
        const formGroup = service.createRecruteurFormGroup();

        const recruteur = service.getRecruteur(formGroup) as any;

        expect(recruteur).toMatchObject({});
      });

      it('should return IRecruteur', () => {
        const formGroup = service.createRecruteurFormGroup(sampleWithRequiredData);

        const recruteur = service.getRecruteur(formGroup) as any;

        expect(recruteur).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRecruteur should not enable id FormControl', () => {
        const formGroup = service.createRecruteurFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRecruteur should disable id FormControl', () => {
        const formGroup = service.createRecruteurFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
