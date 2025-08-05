import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-candidat-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FontAwesomeModule, TranslateModule],
  templateUrl: './candidat-dashboard.component.html',
  styleUrls: ['./candidat-dashboard.component.scss']
})
export class CandidatDashboardComponent implements OnInit {
  private offreEmploiService = inject(OffreEmploiService);
  private accountService = inject(AccountService);

  // Signaux pour la gestion d'état
  account = signal<Account | null>(null);
  recentOffers = signal<IOffreEmploi[]>([]);
  isLoading = signal<boolean>(false);

  ngOnInit(): void {
    this.loadAccount();
    this.loadDashboardData();
  }

  private loadAccount(): void {
    this.accountService.identity().subscribe(account => {
      this.account.set(account);
    });
  }

  private loadDashboardData(): void {
    this.isLoading.set(true);
    
    // Charger les offres récentes (limitées à 6 pour le dashboard)
    this.offreEmploiService.query({ size: 6, sort: ['datePublication,desc'] }).subscribe({
      next: (response) => {
        if (response.body) {
          this.recentOffers.set(response.body);
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  refreshData(): void {
    this.loadDashboardData();
  }

  trackById(index: number, item: IOffreEmploi): number {
    return item.id;
  }
}
