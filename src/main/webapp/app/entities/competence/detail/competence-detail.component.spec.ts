import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { CompetenceDetailComponent } from './competence-detail.component';

describe('Competence Management Detail Component', () => {
  let comp: CompetenceDetailComponent;
  let fixture: ComponentFixture<CompetenceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompetenceDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./competence-detail.component').then(m => m.CompetenceDetailComponent),
              resolve: { competence: () => of({ id: 15671 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CompetenceDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CompetenceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load competence on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CompetenceDetailComponent);

      // THEN
      expect(instance.competence()).toEqual(expect.objectContaining({ id: 15671 }));
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
