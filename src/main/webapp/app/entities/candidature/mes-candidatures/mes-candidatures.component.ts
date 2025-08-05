import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';

import { AccountService } from 'app/core/auth/account.service';
import { CandidatService } from 'app/entities/candidat/service/candidat.service';
import { CandidatureService } from '../service/candidature.service';
import { ICandidature } from '../candidature.model';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { StatutCandidature } from 'app/entities/enumerations/statut-candidature.model';

@Component({
  selector: 'jhi-mes-candidatures',
  templateUrl: './mes-candidatures.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class MesCandidaturesComponent implements OnInit {
  candidatures: ICandidature[] = [];
  candidat: ICandidat | null = null;
  isLoading = true;
  statutCandidature = StatutCandidature;

  protected accountService = inject(AccountService);
  protected candidatService = inject(CandidatService);
  protected candidatureService = inject(CandidatureService);
  protected router = inject(Router);

  ngOnInit(): void {
    this.loadCurrentCandidat();
  }

  private loadCurrentCandidat(): void {
    this.accountService.identity().subscribe(account => {
      if (account?.login) {
        this.candidatService.findByUserLogin(account.login).subscribe({
          next: (res: HttpResponse<ICandidat>) => {
            this.candidat = res.body;
            if (this.candidat) {
              this.loadMesCandidatures();
            }
          },
          error: () => {
            this.router.navigate(['/candidat/new']);
          }
        });
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  private loadMesCandidatures(): void {
    if (this.candidat) {
      // Charger les candidatures du candidat connecté
      this.candidatureService.query({
        'candidat.id.equals': this.candidat.id,
        sort: ['datePostulation,desc']
      }).subscribe({
        next: (res: HttpResponse<ICandidature[]>) => {
          this.candidatures = res.body ?? [];
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    }
  }

  getStatutBadgeClass(statut: keyof typeof StatutCandidature | null | undefined): string {
    switch (statut) {
      case StatutCandidature.EN_ATTENTE:
        return 'bg-warning text-dark';
      case StatutCandidature.ACCEPTEE:
        return 'bg-success';
      case StatutCandidature.REFUSEE:
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

  getStatutText(statut: keyof typeof StatutCandidature | null | undefined): string {
    switch (statut) {
      case StatutCandidature.EN_ATTENTE:
        return 'En attente';
      case StatutCandidature.ACCEPTEE:
        return 'Acceptée';
      case StatutCandidature.REFUSEE:
        return 'Refusée';
      default:
        return 'Inconnu';
    }
  }

  voirOffre(candidature: ICandidature): void {
    if (candidature.offre?.id) {
      this.router.navigate(['/offre-emploi', candidature.offre.id, 'view']);
    }
  }

  voirCandidature(candidature: ICandidature): void {
    if (candidature.id) {
      this.router.navigate(['/candidature', candidature.id, 'view']);
    }
  }

  getCandidaturesEnAttente(): number {
    return this.candidatures.filter(c => c.statut === StatutCandidature.EN_ATTENTE).length;
  }

  getCandidaturesAcceptees(): number {
    return this.candidatures.filter(c => c.statut === StatutCandidature.ACCEPTEE).length;
  }

  getCandidaturesRefusees(): number {
    return this.candidatures.filter(c => c.statut === StatutCandidature.REFUSEE).length;
  }
}
