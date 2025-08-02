import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IRecruteur } from 'app/entities/recruteur/recruteur.model';
import { RecruteurService } from 'app/entities/recruteur/service/recruteur.service';
import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model';
import { TypeContratService } from 'app/entities/type-contrat/service/type-contrat.service';
import { ILocalisation } from 'app/entities/localisation/localisation.model';
import { LocalisationService } from 'app/entities/localisation/service/localisation.service';
import { ICompetence } from 'app/entities/competence/competence.model';
import { CompetenceService } from 'app/entities/competence/service/competence.service';
import { IOffreEmploi } from '../offre-emploi.model';
import { OffreEmploiService } from '../service/offre-emploi.service';
import { OffreEmploiFormService } from './offre-emploi-form.service';

import { OffreEmploiUpdateComponent } from './offre-emploi-update.component';

describe('OffreEmploi Management Update Component', () => {
  let comp: OffreEmploiUpdateComponent;
  let fixture: ComponentFixture<OffreEmploiUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let offreEmploiFormService: OffreEmploiFormService;
  let offreEmploiService: OffreEmploiService;
  let recruteurService: RecruteurService;
  let typeContratService: TypeContratService;
  let localisationService: LocalisationService;
  let competenceService: CompetenceService;

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
    typeContratService = TestBed.inject(TypeContratService);
    localisationService = TestBed.inject(LocalisationService);
    competenceService = TestBed.inject(CompetenceService);

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

    it('should call TypeContrat query and add missing value', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const typeContrat: ITypeContrat = { id: 13691 };
      offreEmploi.typeContrat = typeContrat;

      const typeContratCollection: ITypeContrat[] = [{ id: 13691 }];
      jest.spyOn(typeContratService, 'query').mockReturnValue(of(new HttpResponse({ body: typeContratCollection })));
      const additionalTypeContrats = [typeContrat];
      const expectedCollection: ITypeContrat[] = [...additionalTypeContrats, ...typeContratCollection];
      jest.spyOn(typeContratService, 'addTypeContratToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(typeContratService.query).toHaveBeenCalled();
      expect(typeContratService.addTypeContratToCollectionIfMissing).toHaveBeenCalledWith(
        typeContratCollection,
        ...additionalTypeContrats.map(expect.objectContaining),
      );
      expect(comp.typeContratsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Localisation query and add missing value', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const localisation: ILocalisation = { id: 6239 };
      offreEmploi.localisation = localisation;

      const localisationCollection: ILocalisation[] = [{ id: 6239 }];
      jest.spyOn(localisationService, 'query').mockReturnValue(of(new HttpResponse({ body: localisationCollection })));
      const additionalLocalisations = [localisation];
      const expectedCollection: ILocalisation[] = [...additionalLocalisations, ...localisationCollection];
      jest.spyOn(localisationService, 'addLocalisationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(localisationService.query).toHaveBeenCalled();
      expect(localisationService.addLocalisationToCollectionIfMissing).toHaveBeenCalledWith(
        localisationCollection,
        ...additionalLocalisations.map(expect.objectContaining),
      );
      expect(comp.localisationsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Competence query and add missing value', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const competences: ICompetence[] = [{ id: 15671 }];
      offreEmploi.competences = competences;

      const competenceCollection: ICompetence[] = [{ id: 15671 }];
      jest.spyOn(competenceService, 'query').mockReturnValue(of(new HttpResponse({ body: competenceCollection })));
      const additionalCompetences = [...competences];
      const expectedCollection: ICompetence[] = [...additionalCompetences, ...competenceCollection];
      jest.spyOn(competenceService, 'addCompetenceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(competenceService.query).toHaveBeenCalled();
      expect(competenceService.addCompetenceToCollectionIfMissing).toHaveBeenCalledWith(
        competenceCollection,
        ...additionalCompetences.map(expect.objectContaining),
      );
      expect(comp.competencesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const offreEmploi: IOffreEmploi = { id: 17338 };
      const recruteur: IRecruteur = { id: 4268 };
      offreEmploi.recruteur = recruteur;
      const typeContrat: ITypeContrat = { id: 13691 };
      offreEmploi.typeContrat = typeContrat;
      const localisation: ILocalisation = { id: 6239 };
      offreEmploi.localisation = localisation;
      const competences: ICompetence = { id: 15671 };
      offreEmploi.competences = [competences];

      activatedRoute.data = of({ offreEmploi });
      comp.ngOnInit();

      expect(comp.recruteursSharedCollection).toContainEqual(recruteur);
      expect(comp.typeContratsSharedCollection).toContainEqual(typeContrat);
      expect(comp.localisationsSharedCollection).toContainEqual(localisation);
      expect(comp.competencesSharedCollection).toContainEqual(competences);
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

    describe('compareTypeContrat', () => {
      it('should forward to typeContratService', () => {
        const entity = { id: 13691 };
        const entity2 = { id: 16761 };
        jest.spyOn(typeContratService, 'compareTypeContrat');
        comp.compareTypeContrat(entity, entity2);
        expect(typeContratService.compareTypeContrat).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareLocalisation', () => {
      it('should forward to localisationService', () => {
        const entity = { id: 6239 };
        const entity2 = { id: 18214 };
        jest.spyOn(localisationService, 'compareLocalisation');
        comp.compareLocalisation(entity, entity2);
        expect(localisationService.compareLocalisation).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCompetence', () => {
      it('should forward to competenceService', () => {
        const entity = { id: 15671 };
        const entity2 = { id: 29641 };
        jest.spyOn(competenceService, 'compareCompetence');
        comp.compareCompetence(entity, entity2);
        expect(competenceService.compareCompetence).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
