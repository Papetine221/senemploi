import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { LocalisationDetailComponent } from './localisation-detail.component';

describe('Localisation Management Detail Component', () => {
  let comp: LocalisationDetailComponent;
  let fixture: ComponentFixture<LocalisationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocalisationDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./localisation-detail.component').then(m => m.LocalisationDetailComponent),
              resolve: { localisation: () => of({ id: 6239 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(LocalisationDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LocalisationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load localisation on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LocalisationDetailComponent);

      // THEN
      expect(instance.localisation()).toEqual(expect.objectContaining({ id: 6239 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
