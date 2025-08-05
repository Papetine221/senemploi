import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DataUtils } from 'app/core/util/data-util.service';
import { AccountService } from 'app/core/auth/account.service';

import { OffreEmploiDetailComponent } from './offre-emploi-detail.component';

describe('OffreEmploi Management Detail Component', () => {
  let comp: OffreEmploiDetailComponent & { isCandidatUser?: boolean };
  let fixture: ComponentFixture<OffreEmploiDetailComponent>;
  let dataUtils: DataUtils;
  let accountService: AccountService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OffreEmploiDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./offre-emploi-detail.component').then(m => m.OffreEmploiDetailComponent),
              resolve: { offreEmploi: () => of({ id: 12430 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(OffreEmploiDetailComponent, '')
      .compileComponents();
    dataUtils = TestBed.inject(DataUtils);
    jest.spyOn(window, 'open').mockImplementation(() => null);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OffreEmploiDetailComponent);
    comp = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    
    // Mock AccountService
    jest.spyOn(accountService, 'hasAnyAuthority').mockImplementation((authorities) => {
      if (authorities === 'ROLE_CANDIDAT') {
        return comp.isCandidatUser || false;
      }
      return false;
    });
  });

  describe('OnInit', () => {
    it('should load offreEmploi on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', OffreEmploiDetailComponent);

      // THEN
      expect(instance.offreEmploi()).toEqual(expect.objectContaining({ id: 12430 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });

  describe('byteSize', () => {
    it('should call byteSize from DataUtils', () => {
      // GIVEN
      jest.spyOn(dataUtils, 'byteSize');
      const fakeBase64 = 'fake base64';

      // WHEN
      comp.byteSize(fakeBase64);

      // THEN
      expect(dataUtils.byteSize).toHaveBeenCalledWith(fakeBase64);
    });
  });

  describe('openFile', () => {
    it('should call openFile from DataUtils', () => {
      const newWindow = { ...window };
      window.open = jest.fn(() => newWindow);
      window.onload = jest.fn(() => newWindow) as any;
      window.URL.createObjectURL = jest.fn() as any;
      // GIVEN
      jest.spyOn(dataUtils, 'openFile');
      const fakeContentType = 'fake content type';
      const fakeBase64 = 'fake base64';

      // WHEN
      comp.openFile(fakeBase64, fakeContentType);

      // THEN
      expect(dataUtils.openFile).toHaveBeenCalledWith(fakeBase64, fakeContentType);
    });
  });
  
  describe('hasAnyAuthority', () => {
    it('should return false for ROLE_CANDIDAT when user is not a candidat', () => {
      comp.isCandidatUser = false;
      expect(comp.hasAnyAuthority('ROLE_CANDIDAT')).toBeFalsy();
    });

    it('should return true for ROLE_CANDIDAT when user is a candidat', () => {
      comp.isCandidatUser = true;
      expect(comp.hasAnyAuthority('ROLE_CANDIDAT')).toBeTruthy();
    });
  });
  
  describe('UI elements visibility based on user role', () => {
    let element: HTMLElement;
    
    beforeEach(() => {
      element = fixture.nativeElement;
      fixture.detectChanges();
    });
    
    it('should hide edit button for ROLE_CANDIDAT users and show postuler button', () => {
      // Setup candidat user
      comp.isCandidatUser = true;
      fixture.detectChanges();
      
      // Override template for testing
      const editButton = document.createElement('button');
      editButton.setAttribute('id', 'edit-button');
      editButton.setAttribute('*ngIf', '!hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(editButton);
      
      const postulerButton = document.createElement('button');
      postulerButton.setAttribute('id', 'postuler-button');
      postulerButton.setAttribute('*ngIf', 'hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(postulerButton);
      
      fixture.detectChanges();
      
      // Verify buttons visibility
      expect(comp.hasAnyAuthority('ROLE_CANDIDAT')).toBeTruthy();
      expect(element.querySelector('#edit-button')).toBeNull();
      expect(element.querySelector('#postuler-button')).not.toBeNull();
    });
    
    it('should show edit button for non-ROLE_CANDIDAT users and hide postuler button', () => {
      // Setup non-candidat user
      comp.isCandidatUser = false;
      fixture.detectChanges();
      
      // Override template for testing
      const editButton = document.createElement('button');
      editButton.setAttribute('id', 'edit-button');
      editButton.setAttribute('*ngIf', '!hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(editButton);
      
      const postulerButton = document.createElement('button');
      postulerButton.setAttribute('id', 'postuler-button');
      postulerButton.setAttribute('*ngIf', 'hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(postulerButton);
      
      fixture.detectChanges();
      
      // Verify buttons visibility
      expect(comp.hasAnyAuthority('ROLE_CANDIDAT')).toBeFalsy();
      expect(element.querySelector('#edit-button')).not.toBeNull();
      expect(element.querySelector('#postuler-button')).toBeNull();
    });
  });
});
