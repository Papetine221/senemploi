import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { LocalisationService } from '../service/localisation.service';
import { ILocalisation } from '../localisation.model';
import { LocalisationFormService } from './localisation-form.service';

import { LocalisationUpdateComponent } from './localisation-update.component';

describe('Localisation Management Update Component', () => {
  let comp: LocalisationUpdateComponent;
  let fixture: ComponentFixture<LocalisationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let localisationFormService: LocalisationFormService;
  let localisationService: LocalisationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LocalisationUpdateComponent],
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
      .overrideTemplate(LocalisationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LocalisationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    localisationFormService = TestBed.inject(LocalisationFormService);
    localisationService = TestBed.inject(LocalisationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const localisation: ILocalisation = { id: 18214 };

      activatedRoute.data = of({ localisation });
      comp.ngOnInit();

      expect(comp.localisation).toEqual(localisation);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocalisation>>();
      const localisation = { id: 6239 };
      jest.spyOn(localisationFormService, 'getLocalisation').mockReturnValue(localisation);
      jest.spyOn(localisationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ localisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: localisation }));
      saveSubject.complete();

      // THEN
      expect(localisationFormService.getLocalisation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(localisationService.update).toHaveBeenCalledWith(expect.objectContaining(localisation));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocalisation>>();
      const localisation = { id: 6239 };
      jest.spyOn(localisationFormService, 'getLocalisation').mockReturnValue({ id: null });
      jest.spyOn(localisationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ localisation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: localisation }));
      saveSubject.complete();

      // THEN
      expect(localisationFormService.getLocalisation).toHaveBeenCalled();
      expect(localisationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocalisation>>();
      const localisation = { id: 6239 };
      jest.spyOn(localisationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ localisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(localisationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
