import { Component, inject, input, OnInit, signal } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IOffreEmploi } from '../offre-emploi.model';
import { AccountService } from 'app/core/auth/account.service';
import { CandidatureService } from 'app/entities/candidature/service/candidature.service';
import { ICandidature } from 'app/entities/candidature/candidature.model';

@Component({
  selector: 'jhi-offre-emploi-detail',
  templateUrl: './offre-emploi-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class OffreEmploiDetailComponent implements OnInit {
  offreEmploi = input<IOffreEmploi | null>(null);

  protected dataUtils = inject(DataUtils);
  protected accountService = inject(AccountService);
  protected candidatureService = inject(CandidatureService);
  
  candidatures = signal<ICandidature[]>([]);
  
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
  }
  
  hasAnyAuthority(authorities: string | string[]): boolean {
    return this.accountService.hasAnyAuthority(authorities);
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
  
  hasAlreadyApplied(): boolean {
    const offreId = this.offreEmploi()?.id;
    if (!offreId) return false;
    return this.candidatures().some(candidature => candidature.offre?.id === offreId);
  }
}
