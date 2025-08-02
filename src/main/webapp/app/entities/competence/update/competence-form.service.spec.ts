import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../competence.test-samples';

import { CompetenceFormService } from './competence-form.service';

describe('Competence Form Service', () => {
  let service: CompetenceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CompetenceFormService);
  });

  describe('Service methods', () => {
    describe('createCompetenceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCompetenceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            offreEmplois: expect.any(Object),
          }),
        );
      });

      it('passing ICompetence should create a new form with FormGroup', () => {
        const formGroup = service.createCompetenceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            offreEmplois: expect.any(Object),
          }),
        );
      });
    });

    describe('getCompetence', () => {
      it('should return NewCompetence for default Competence initial value', () => {
        const formGroup = service.createCompetenceFormGroup(sampleWithNewData);

        const competence = service.getCompetence(formGroup) as any;

        expect(competence).toMatchObject(sampleWithNewData);
      });

      it('should return NewCompetence for empty Competence initial value', () => {
        const formGroup = service.createCompetenceFormGroup();

        const competence = service.getCompetence(formGroup) as any;

        expect(competence).toMatchObject({});
      });

      it('should return ICompetence', () => {
        const formGroup = service.createCompetenceFormGroup(sampleWithRequiredData);

        const competence = service.getCompetence(formGroup) as any;

        expect(competence).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICompetence should not enable id FormControl', () => {
        const formGroup = service.createCompetenceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCompetence should disable id FormControl', () => {
        const formGroup = service.createCompetenceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
