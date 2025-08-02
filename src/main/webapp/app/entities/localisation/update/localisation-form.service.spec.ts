import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../localisation.test-samples';

import { LocalisationFormService } from './localisation-form.service';

describe('Localisation Form Service', () => {
  let service: LocalisationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocalisationFormService);
  });

  describe('Service methods', () => {
    describe('createLocalisationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLocalisationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            region: expect.any(Object),
            departement: expect.any(Object),
            ville: expect.any(Object),
          }),
        );
      });

      it('passing ILocalisation should create a new form with FormGroup', () => {
        const formGroup = service.createLocalisationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            region: expect.any(Object),
            departement: expect.any(Object),
            ville: expect.any(Object),
          }),
        );
      });
    });

    describe('getLocalisation', () => {
      it('should return NewLocalisation for default Localisation initial value', () => {
        const formGroup = service.createLocalisationFormGroup(sampleWithNewData);

        const localisation = service.getLocalisation(formGroup) as any;

        expect(localisation).toMatchObject(sampleWithNewData);
      });

      it('should return NewLocalisation for empty Localisation initial value', () => {
        const formGroup = service.createLocalisationFormGroup();

        const localisation = service.getLocalisation(formGroup) as any;

        expect(localisation).toMatchObject({});
      });

      it('should return ILocalisation', () => {
        const formGroup = service.createLocalisationFormGroup(sampleWithRequiredData);

        const localisation = service.getLocalisation(formGroup) as any;

        expect(localisation).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILocalisation should not enable id FormControl', () => {
        const formGroup = service.createLocalisationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLocalisation should disable id FormControl', () => {
        const formGroup = service.createLocalisationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
