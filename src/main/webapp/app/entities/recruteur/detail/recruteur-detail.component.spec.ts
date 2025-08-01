import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { RecruteurDetailComponent } from './recruteur-detail.component';

describe('Recruteur Management Detail Component', () => {
  let comp: RecruteurDetailComponent;
  let fixture: ComponentFixture<RecruteurDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecruteurDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./recruteur-detail.component').then(m => m.RecruteurDetailComponent),
              resolve: { recruteur: () => of({ id: 4268 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RecruteurDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecruteurDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load recruteur on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RecruteurDetailComponent);

      // THEN
      expect(instance.recruteur()).toEqual(expect.objectContaining({ id: 4268 }));
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
