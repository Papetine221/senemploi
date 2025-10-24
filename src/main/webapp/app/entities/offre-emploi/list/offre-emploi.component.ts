import { Component, NgZone, OnInit, WritableSignal, computed, inject, signal } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { ParseLinks } from 'app/core/util/parse-links.service';

import { OffreEmploiDeleteDialogComponent } from '../delete/offre-emploi-delete-dialog.component';
import { EntityArrayResponseType, OffreEmploiService } from '../service/offre-emploi.service';
import { IOffreEmploi } from '../offre-emploi.model';
import { AccountService } from 'app/core/auth/account.service';
import { CandidatureService } from 'app/entities/candidature/service/candidature.service';
import { ICandidature } from 'app/entities/candidature/candidature.model';

@Component({
  selector: 'jhi-offre-emploi',
  templateUrl: './offre-emploi.component.html',
  imports: [RouterModule, FormsModule, SharedModule, FormatMediumDatetimePipe],
})
export class OffreEmploiComponent implements OnInit {
  subscription: Subscription | null = null;
  offreEmplois = signal<IOffreEmploi[]>([]);
  isLoading = false;

  sortState = sortStateSignal({});

  itemsPerPage = ITEMS_PER_PAGE;

  public readonly router = inject(Router);
  protected readonly offreEmploiService = inject(OffreEmploiService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected parseLinks = inject(ParseLinks);
  protected dataUtils = inject(DataUtils);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);
  protected accountService = inject(AccountService);
  protected candidatureService = inject(CandidatureService);
  
  candidatures = signal<ICandidature[]>([]);
  
  hasAnyAuthority(authorities: string | string[]): boolean {
    return this.accountService.hasAnyAuthority(authorities);
  }

  hasAlreadyApplied(offreEmploi: IOffreEmploi): boolean {
    return this.candidatures().some(candidature => candidature.offre?.id === offreEmploi.id);
  }

  trackId = (item: IOffreEmploi): number => this.offreEmploiService.getOffreEmploiIdentifier(item);

  ngOnInit(): void {
    // Charger les candidatures du candidat si c'est un candidat
    if (this.hasAnyAuthority('ROLE_CANDIDAT')) {
      this.candidatureService.query().subscribe({
        next: (response) => {
          if (response.body) {
            this.candidatures.set(response.body);
          }
        },
        error: () => {
          console.error('Erreur lors du chargement des candidatures');
        }
      });
    }
    
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.offreEmplois().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  reset(): void {
    this.offreEmplois.set([]);
    this.isLoading = false;
  }



  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(offreEmploi: IOffreEmploi): void {
    const modalRef = this.modalService.open(OffreEmploiDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.offreEmploi = offreEmploi;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.isLoading = true;
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des offres d\'emploi:', error);
        this.isLoading = false;
      }
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.offreEmplois.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IOffreEmploi[] | null): IOffreEmploi[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    // Méthode simplifiée sans pagination infinie
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const queryObject: any = {
      size: this.itemsPerPage,
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState())
    };

    return this.offreEmploiService.query(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
