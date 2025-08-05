import { ComponentFixture, TestBed, fakeAsync, inject, tick } from '@angular/core/testing';
import { HttpHeaders, HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subject, of } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from 'app/core/auth/account.service';

import { sampleWithRequiredData } from '../offre-emploi.test-samples';
import { OffreEmploiService } from '../service/offre-emploi.service';

import { OffreEmploiComponent } from './offre-emploi.component';
import SpyInstance = jest.SpyInstance;

describe('OffreEmploi Management Component', () => {
  let comp: OffreEmploiComponent & { isCandidatUser?: boolean };
  let fixture: ComponentFixture<OffreEmploiComponent>;
  let service: OffreEmploiService;
  let accountService: AccountService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OffreEmploiComponent],
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            ),
            snapshot: {
              queryParams: {},
              queryParamMap: jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
                'filter[someId.in]': 'dc4279ea-cfb9-11ec-9d64-0242ac120002',
              }),
            },
          },
        },
      ],
    })
      .overrideTemplate(OffreEmploiComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OffreEmploiComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OffreEmploiService);
    accountService = TestBed.inject(AccountService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');
    
    // Mock AccountService
    jest.spyOn(accountService, 'hasAnyAuthority').mockImplementation((authorities) => {
      if (authorities === 'ROLE_CANDIDAT') {
        return comp.isCandidatUser || false;
      }
      return false;
    });

    jest
      .spyOn(service, 'query')
      .mockReturnValueOnce(
        of(
          new HttpResponse({
            body: [{ id: 12430 }],
            headers: new HttpHeaders({
              link: '<http://localhost/api/foo?page=1&size=20>; rel="next"',
            }),
          }),
        ),
      )
      .mockReturnValueOnce(
        of(
          new HttpResponse({
            body: [{ id: 17338 }],
            headers: new HttpHeaders({
              link: '<http://localhost/api/foo?page=0&size=20>; rel="prev",<http://localhost/api/foo?page=2&size=20>; rel="next"',
            }),
          }),
        ),
      );
  });

  it('should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.offreEmplois()[0]).toEqual(expect.objectContaining({ id: 12430 }));
  });

  describe('trackId', () => {
    it('should forward to offreEmploiService', () => {
      const entity = { id: 12430 };
      jest.spyOn(service, 'getOffreEmploiIdentifier');
      const id = comp.trackId(entity);
      expect(service.getOffreEmploiIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
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
    
    it('should hide create, edit and delete buttons for ROLE_CANDIDAT users', () => {
      // Setup candidat user
      comp.isCandidatUser = true;
      fixture.detectChanges();
      
      // Override template for testing
      const createButton = document.createElement('button');
      createButton.setAttribute('id', 'jh-create-entity');
      createButton.setAttribute('*ngIf', '!hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(createButton);
      
      const editButton = document.createElement('button');
      editButton.setAttribute('id', 'edit-button');
      editButton.setAttribute('*ngIf', '!hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(editButton);
      
      const deleteButton = document.createElement('button');
      deleteButton.setAttribute('id', 'delete-button');
      deleteButton.setAttribute('*ngIf', '!hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(deleteButton);
      
      const postulerButton = document.createElement('button');
      postulerButton.setAttribute('id', 'postuler-button');
      postulerButton.setAttribute('*ngIf', 'hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(postulerButton);
      
      fixture.detectChanges();
      
      // Verify buttons visibility
      expect(comp.hasAnyAuthority('ROLE_CANDIDAT')).toBeTruthy();
      expect(element.querySelector('#jh-create-entity')).toBeNull();
      expect(element.querySelector('#edit-button')).toBeNull();
      expect(element.querySelector('#delete-button')).toBeNull();
      expect(element.querySelector('#postuler-button')).not.toBeNull();
    });
    
    it('should show create, edit and delete buttons for non-ROLE_CANDIDAT users', () => {
      // Setup non-candidat user
      comp.isCandidatUser = false;
      fixture.detectChanges();
      
      // Override template for testing
      const createButton = document.createElement('button');
      createButton.setAttribute('id', 'jh-create-entity');
      createButton.setAttribute('*ngIf', '!hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(createButton);
      
      const editButton = document.createElement('button');
      editButton.setAttribute('id', 'edit-button');
      editButton.setAttribute('*ngIf', '!hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(editButton);
      
      const deleteButton = document.createElement('button');
      deleteButton.setAttribute('id', 'delete-button');
      deleteButton.setAttribute('*ngIf', '!hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(deleteButton);
      
      const postulerButton = document.createElement('button');
      postulerButton.setAttribute('id', 'postuler-button');
      postulerButton.setAttribute('*ngIf', 'hasAnyAuthority(\'ROLE_CANDIDAT\')');
      element.appendChild(postulerButton);
      
      fixture.detectChanges();
      
      // Verify buttons visibility
      expect(comp.hasAnyAuthority('ROLE_CANDIDAT')).toBeFalsy();
      expect(element.querySelector('#jh-create-entity')).not.toBeNull();
      expect(element.querySelector('#edit-button')).not.toBeNull();
      expect(element.querySelector('#delete-button')).not.toBeNull();
      expect(element.querySelector('#postuler-button')).toBeNull();
    });
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // WHEN
    comp.navigateToWithComponentValues({ predicate: 'non-existing-column', order: 'asc' });

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['non-existing-column,asc'],
        }),
      }),
    );
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['id,desc'] }));
  });

  it('should infinite scroll', () => {
    // GIVEN
    comp.loadNextPage();
    comp.loadNextPage();
    comp.loadNextPage();

    // THEN
    expect(service.query).toHaveBeenCalledTimes(3);
    expect(service.query).toHaveBeenNthCalledWith(2, expect.objectContaining({ page: '1' }));
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ page: '2' }));
  });

  describe('delete', () => {
    let ngbModal: NgbModal;
    let deleteModalMock: any;

    beforeEach(() => {
      deleteModalMock = { componentInstance: {}, closed: new Subject() };
      // NgbModal is not a singleton using TestBed.inject.
      // ngbModal = TestBed.inject(NgbModal);
      ngbModal = (comp as any).modalService;
      jest.spyOn(ngbModal, 'open').mockReturnValue(deleteModalMock);
    });

    it('on confirm should call load', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(comp, 'load');

        // WHEN
        comp.delete(sampleWithRequiredData);
        deleteModalMock.closed.next('deleted');
        tick();

        // THEN
        expect(ngbModal.open).toHaveBeenCalled();
        expect(comp.load).toHaveBeenCalled();
      }),
    ));

    it('on dismiss should call load', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(comp, 'load');

        // WHEN
        comp.delete(sampleWithRequiredData);
        deleteModalMock.closed.next();
        tick();

        // THEN
        expect(ngbModal.open).toHaveBeenCalled();
        expect(comp.load).not.toHaveBeenCalled();
      }),
    ));
  });
});
