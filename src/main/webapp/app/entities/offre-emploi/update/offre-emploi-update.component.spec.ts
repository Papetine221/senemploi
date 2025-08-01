import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IRecruteur } from 'app/entities/recruteur/recruteur.model';
import { RecruteurService } from 'app/entities/recruteur/service/recruteur.service';
import { OffreEmploiService } from '../service/offre-emploi.service';
import { IOffreEmploi } from '../offre-emploi.model';
import { OffreEmploiFormService } from './offre-emploi-form.service';

import { OffreEmploiUpdateComponent } from './offre-emploi-update.component';

describe('OffreEmploi Management Update Component', () => {
  let comp: OffreEmploiUpdateComponent;
  let fixture: ComponentFixture<OffreEmploiUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let offreEmploiFormService: OffreEmploiFormService;
  let offreEmploiService: OffreEmploiService;
  let recruteurService: RecruteurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OffreEmploiUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OffreEmploiUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OffreEmploiUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    offreEmploiFormService = TestBed.inject(OffreEmploiFormService);
    offreEmploiService = TestBed.inject(OffreEmploiService);
    recruteurService = TestBed.inject(RecruteurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Recruteur query and add missing value', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const recruteur: IRecruteur = { id: 4268 };
      offreEmploi.recruteur = recruteur;

      const recruteurCollection: IRecruteur[] = [{ id: 4268 }];
      jest.spyOn(recruteurService, 'query').mockReturnValue(of(new HttpResponse({ body: recruteurCollection })));
      const additionalRecruteurs = [recruteur];
      const expectedCollection: IRecruteur[] = [...additionalRecruteurs, ...recruteurCollection];
      jest.spyOn(recruteurService, 'addRecruteurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(recruteurService.query).toHaveBeenCalled();
      expect(recruteurService.addRecruteurToCollectionIfMissing).toHaveBeenCalledWith(
        recruteurCollection,
        ...additionalRecruteurs.map(expect.objectContaining),
      );
      expect(comp.recruteursSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const recruteur: IRecruteur = { id: 4268 };
      offreEmploi.recruteur = recruteur;

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(comp.recruteursSharedCollection).toContainEqual(recruteur);
      expect(comp.offreEmploi).toEqual(offreEmploi);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOffreEmploi>>();
      const offreEmploi = { id: 12430 };
      jest.spyOn(offreEmploiFormService, 'getOffreEmploi').mockReturnValue(offreEmploi);
      jest.spyOn(offreEmploiService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: offreEmploi }));
      saveSubject.complete();

      // THEN
      expect(offreEmploiFormService.getOffreEmploi).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(offreEmploiService.update).toHaveBeenCalledWith(expect.objectContaining(offreEmploi));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOffreEmploi>>();
      const offreEmploi = { id: 12430 };
      jest.spyOn(offreEmploiFormService, 'getOffreEmploi').mockReturnValue({ id: null });
      jest.spyOn(offreEmploiService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offreEmploi: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: offreEmploi }));
      saveSubject.complete();

      // THEN
      expect(offreEmploiFormService.getOffreEmploi).toHaveBeenCalled();
      expect(offreEmploiService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOffreEmploi>>();
      const offreEmploi = { id: 12430 };
      jest.spyOn(offreEmploiService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(offreEmploiService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRecruteur', () => {
      it('should forward to recruteurService', () => {
        const entity = { id: 4268 };
        const entity2 = { id: 10028 };
        jest.spyOn(recruteurService, 'compareRecruteur');
        comp.compareRecruteur(entity, entity2);
        expect(recruteurService.compareRecruteur).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
