import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { CompetenceService } from '../service/competence.service';
import { ICompetence } from '../competence.model';
import { CompetenceFormService } from './competence-form.service';

import { CompetenceUpdateComponent } from './competence-update.component';

describe('Competence Management Update Component', () => {
  let comp: CompetenceUpdateComponent;
  let fixture: ComponentFixture<CompetenceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let competenceFormService: CompetenceFormService;
  let competenceService: CompetenceService;
  let offreEmploiService: OffreEmploiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CompetenceUpdateComponent],
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
      .overrideTemplate(CompetenceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CompetenceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    competenceFormService = TestBed.inject(CompetenceFormService);
    competenceService = TestBed.inject(CompetenceService);
    offreEmploiService = TestBed.inject(OffreEmploiService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call OffreEmploi query and add missing value', () => {
      const competence: ICompetence = { id: 29641 };
      const offreEmplois: IOffreEmploi[] = [{ id: 12430 }];
      competence.offreEmplois = offreEmplois;

      const offreEmploiCollection: IOffreEmploi[] = [{ id: 12430 }];
      jest.spyOn(offreEmploiService, 'query').mockReturnValue(of(new HttpResponse({ body: offreEmploiCollection })));
      const additionalOffreEmplois = [...offreEmplois];
      const expectedCollection: IOffreEmploi[] = [...additionalOffreEmplois, ...offreEmploiCollection];
      jest.spyOn(offreEmploiService, 'addOffreEmploiToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ competence });
      comp.ngOnInit();

      expect(offreEmploiService.query).toHaveBeenCalled();
      expect(offreEmploiService.addOffreEmploiToCollectionIfMissing).toHaveBeenCalledWith(
        offreEmploiCollection,
        ...additionalOffreEmplois.map(expect.objectContaining),
      );
      expect(comp.offreEmploisSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const competence: ICompetence = { id: 29641 };
      const offreEmploi: IOffreEmploi = { id: 12430 };
      competence.offreEmplois = [offreEmploi];

      activatedRoute.data = of({ competence });
      comp.ngOnInit();

      expect(comp.offreEmploisSharedCollection).toContainEqual(offreEmploi);
      expect(comp.competence).toEqual(competence);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICompetence>>();
      const competence = { id: 15671 };
      jest.spyOn(competenceFormService, 'getCompetence').mockReturnValue(competence);
      jest.spyOn(competenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ competence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: competence }));
      saveSubject.complete();

      // THEN
      expect(competenceFormService.getCompetence).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(competenceService.update).toHaveBeenCalledWith(expect.objectContaining(competence));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICompetence>>();
      const competence = { id: 15671 };
      jest.spyOn(competenceFormService, 'getCompetence').mockReturnValue({ id: null });
      jest.spyOn(competenceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ competence: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: competence }));
      saveSubject.complete();

      // THEN
      expect(competenceFormService.getCompetence).toHaveBeenCalled();
      expect(competenceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICompetence>>();
      const competence = { id: 15671 };
      jest.spyOn(competenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ competence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(competenceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOffreEmploi', () => {
      it('should forward to offreEmploiService', () => {
        const entity = { id: 12430 };
        const entity2 = { id: 17338 };
        jest.spyOn(offreEmploiService, 'compareOffreEmploi');
        comp.compareOffreEmploi(entity, entity2);
        expect(offreEmploiService.compareOffreEmploi).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
