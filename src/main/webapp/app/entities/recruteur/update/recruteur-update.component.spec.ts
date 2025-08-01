import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { RecruteurService } from '../service/recruteur.service';
import { IRecruteur } from '../recruteur.model';
import { RecruteurFormService } from './recruteur-form.service';

import { RecruteurUpdateComponent } from './recruteur-update.component';

describe('Recruteur Management Update Component', () => {
  let comp: RecruteurUpdateComponent;
  let fixture: ComponentFixture<RecruteurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let recruteurFormService: RecruteurFormService;
  let recruteurService: RecruteurService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RecruteurUpdateComponent],
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
      .overrideTemplate(RecruteurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RecruteurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    recruteurFormService = TestBed.inject(RecruteurFormService);
    recruteurService = TestBed.inject(RecruteurService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const recruteur: IRecruteur = { id: 10028 };
      const user: IUser = { id: 3944 };
      recruteur.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ recruteur });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const recruteur: IRecruteur = { id: 10028 };
      const user: IUser = { id: 3944 };
      recruteur.user = user;

      activatedRoute.data = of({ recruteur });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.recruteur).toEqual(recruteur);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRecruteur>>();
      const recruteur = { id: 4268 };
      jest.spyOn(recruteurFormService, 'getRecruteur').mockReturnValue(recruteur);
      jest.spyOn(recruteurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recruteur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: recruteur }));
      saveSubject.complete();

      // THEN
      expect(recruteurFormService.getRecruteur).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(recruteurService.update).toHaveBeenCalledWith(expect.objectContaining(recruteur));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRecruteur>>();
      const recruteur = { id: 4268 };
      jest.spyOn(recruteurFormService, 'getRecruteur').mockReturnValue({ id: null });
      jest.spyOn(recruteurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recruteur: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: recruteur }));
      saveSubject.complete();

      // THEN
      expect(recruteurFormService.getRecruteur).toHaveBeenCalled();
      expect(recruteurService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRecruteur>>();
      const recruteur = { id: 4268 };
      jest.spyOn(recruteurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ recruteur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(recruteurService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
