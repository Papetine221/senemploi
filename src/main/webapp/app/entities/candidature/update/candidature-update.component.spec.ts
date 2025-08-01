import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICandidat } from 'app/entities/candidat/candidat.model';
import { CandidatService } from 'app/entities/candidat/service/candidat.service';
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { ICandidature } from '../candidature.model';
import { CandidatureService } from '../service/candidature.service';
import { CandidatureFormService } from './candidature-form.service';

import { CandidatureUpdateComponent } from './candidature-update.component';

describe('Candidature Management Update Component', () => {
  let comp: CandidatureUpdateComponent;
  let fixture: ComponentFixture<CandidatureUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let candidatureFormService: CandidatureFormService;
  let candidatureService: CandidatureService;
  let candidatService: CandidatService;
  let offreEmploiService: OffreEmploiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CandidatureUpdateComponent],
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
      .overrideTemplate(CandidatureUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CandidatureUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    candidatureFormService = TestBed.inject(CandidatureFormService);
    candidatureService = TestBed.inject(CandidatureService);
    candidatService = TestBed.inject(CandidatService);
    offreEmploiService = TestBed.inject(OffreEmploiService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Candidat query and add missing value', () => {
      const candidature: ICandidature = { id: 12242 };
      const candidat: ICandidat = { id: 29649 };
      candidature.candidat = candidat;

      const candidatCollection: ICandidat[] = [{ id: 29649 }];
      jest.spyOn(candidatService, 'query').mockReturnValue(of(new HttpResponse({ body: candidatCollection })));
      const additionalCandidats = [candidat];
      const expectedCollection: ICandidat[] = [...additionalCandidats, ...candidatCollection];
      jest.spyOn(candidatService, 'addCandidatToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      expect(candidatService.query).toHaveBeenCalled();
      expect(candidatService.addCandidatToCollectionIfMissing).toHaveBeenCalledWith(
        candidatCollection,
        ...additionalCandidats.map(expect.objectContaining),
      );
      expect(comp.candidatsSharedCollection).toEqual(expectedCollection);
    });

    it('should call OffreEmploi query and add missing value', () => {
      const candidature: ICandidature = { id: 12242 };
      const offre: IOffreEmploi = { id: 12430 };
      candidature.offre = offre;

      const offreEmploiCollection: IOffreEmploi[] = [{ id: 12430 }];
      jest.spyOn(offreEmploiService, 'query').mockReturnValue(of(new HttpResponse({ body: offreEmploiCollection })));
      const additionalOffreEmplois = [offre];
      const expectedCollection: IOffreEmploi[] = [...additionalOffreEmplois, ...offreEmploiCollection];
      jest.spyOn(offreEmploiService, 'addOffreEmploiToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      expect(offreEmploiService.query).toHaveBeenCalled();
      expect(offreEmploiService.addOffreEmploiToCollectionIfMissing).toHaveBeenCalledWith(
        offreEmploiCollection,
        ...additionalOffreEmplois.map(expect.objectContaining),
      );
      expect(comp.offreEmploisSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const candidature: ICandidature = { id: 12242 };
      const candidat: ICandidat = { id: 29649 };
      candidature.candidat = candidat;
      const offre: IOffreEmploi = { id: 12430 };
      candidature.offre = offre;

      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      expect(comp.candidatsSharedCollection).toContainEqual(candidat);
      expect(comp.offreEmploisSharedCollection).toContainEqual(offre);
      expect(comp.candidature).toEqual(candidature);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICandidature>>();
      const candidature = { id: 17844 };
      jest.spyOn(candidatureFormService, 'getCandidature').mockReturnValue(candidature);
      jest.spyOn(candidatureService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: candidature }));
      saveSubject.complete();

      // THEN
      expect(candidatureFormService.getCandidature).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(candidatureService.update).toHaveBeenCalledWith(expect.objectContaining(candidature));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICandidature>>();
      const candidature = { id: 17844 };
      jest.spyOn(candidatureFormService, 'getCandidature').mockReturnValue({ id: null });
      jest.spyOn(candidatureService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candidature: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: candidature }));
      saveSubject.complete();

      // THEN
      expect(candidatureFormService.getCandidature).toHaveBeenCalled();
      expect(candidatureService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICandidature>>();
      const candidature = { id: 17844 };
      jest.spyOn(candidatureService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ candidature });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(candidatureService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCandidat', () => {
      it('should forward to candidatService', () => {
        const entity = { id: 29649 };
        const entity2 = { id: 17830 };
        jest.spyOn(candidatService, 'compareCandidat');
        comp.compareCandidat(entity, entity2);
        expect(candidatService.compareCandidat).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
