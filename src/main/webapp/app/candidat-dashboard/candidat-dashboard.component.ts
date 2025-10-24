// ===================== IMPORTS =====================
// Importation des modules et dépendances nécessaires
import { Component, OnInit, inject, signal } from '@angular/core';  // Décorateur de composant, interface OnInit et les fonctions inject/signal
import { CommonModule } from '@angular/common';                    // Module commun (ngIf, ngFor, etc.)
import { RouterModule } from '@angular/router';                    // Permet la navigation entre les pages (routerLink)
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome'; // Icônes FontAwesome
import { TranslateModule } from '@ngx-translate/core';             // Module de traduction pour l’internationalisation

// Importation des services et modèles nécessaires
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service'; // Service pour interagir avec les offres d’emploi (backend)
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';                 // Interface décrivant la structure d’une offre
import { AccountService } from 'app/core/auth/account.service';                              // Service gérant l’authentification et le compte utilisateur
import { Account } from 'app/core/auth/account.model';                                       // Modèle représentant un utilisateur connecté
import { CandidatureService } from 'app/entities/candidature/service/candidature.service';   // Service pour gérer les candidatures
import { ICandidature } from 'app/entities/candidature/candidature.model';                   // Interface représentant une candidature

// ==DÉCORATEUR DU COMPOSANT ==
@Component({
  selector: 'jhi-candidat-dashboard',        // Nom de la balise HTML utilisée pour ce composant
  standalone: true,                          // Indique que ce composant est autonome (sans module Angular parent)
  imports: [CommonModule, RouterModule, FontAwesomeModule, TranslateModule], // Modules nécessaires pour son bon fonctionnement
  templateUrl: './candidat-dashboard.component.html',    // Fichier HTML lié à ce composant
  styleUrls: ['./candidat-dashboard.component.scss']     // Fichier SCSS contenant le style de la page
})
export class CandidatDashboardComponent implements OnInit {  // Classe principale du composant, implémente OnInit pour exécuter du code au chargement

  // ==INJECTION DES SERVICES ==
  private offreEmploiService = inject(OffreEmploiService);  // Injection du service pour gérer les offres d’emploi
  private accountService = inject(AccountService);          // Injection du service de gestion du compte utilisateur
  private candidatureService = inject(CandidatureService);  // Injection du service de gestion des candidatures

  // == SIGNALS (NOUVELLE API ANGULAR) ====
  // Les "signals" remplacent les Subject/BehaviorSubject pour un état réactif plus simple
  account = signal<Account | null>(null);          // Stocke les informations du compte connecté (ou null si aucun utilisateur)
  recentOffers = signal<IOffreEmploi[]>([]);       // Stocke la liste des offres récentes affichées dans le tableau de bord
  isLoading = signal<boolean>(false);              // Indique si les données sont en cours de chargement
  candidatures = signal<ICandidature[]>([]);       // Contient la liste des candidatures du candidat connecté

  // = MÉTHODE INITIALE =
  // Cette méthode est appelée automatiquement au chargement du composant
  ngOnInit(): void {
    this.loadAccount();          // Récupère les informations du compte connecté
    this.loadDashboardData();    // Charge les offres et les candidatures
  }

  // = MÉTHODE : RÉCUPÉRATION DU COMPTE =
  private loadAccount(): void {
    // Appel du service AccountService pour obtenir les infos de l’utilisateur connecté
    this.accountService.identity().subscribe(account => {
      this.account.set(account); // Stocke les infos du compte dans le signal "account"
    });
  }

  // =MÉTHODE : CHARGEMENT DES DONNÉES DU DASHBOARD =
  private loadDashboardData(): void {
    this.isLoading.set(true); // Active le mode "chargement" (affichage possible d’un spinner)

    // --- 1. Chargement des candidatures du candidat connecté ---
    this.candidatureService.query().subscribe({
      next: (response) => {
        if (response.body) {
          this.candidatures.set(response.body); // Met à jour la liste des candidatures si la réponse contient des données
        }
      },
      error: () => {
        console.error('Erreur lors du chargement des candidatures'); // Affiche une erreur en cas d’échec
      }
    });

    // --- 2. Chargement des offres d’emploi récentes ---
    this.offreEmploiService.query({ size: 6, sort: ['datePublication,desc'] }).subscribe({
      next: (response) => {
        if (response.body) {
          this.recentOffers.set(response.body);  // Met à jour la liste des offres récentes
        }
        this.isLoading.set(false); // Désactive le mode "chargement"
      },
      error: () => {
        this.isLoading.set(false); // Même en cas d’erreur, on désactive le chargement
      }
    });
  }

  // == MÉTHODE : ACTUALISATION DES DONNÉES =
  // Permet de recharger toutes les informations du tableau de bord
  refreshData(): void {
    this.loadDashboardData();
  }

  // == MÉTHODE : OPTIMISATION DU *ngFor ==
  // Angular utilise cette fonction pour identifier chaque offre et éviter de recharger inutilement le DOM
  trackById(index: number, item: IOffreEmploi): number {
    return item.id ?? index;
  }

  // == MÉTHODE : VÉRIFIER SI LE CANDIDAT A DÉJÀ POSTULÉ ==
  hasAlreadyApplied(offreId: number | undefined): boolean {
    // Retourne "true" si le candidat a déjà une candidature liée à cette offre
    if (!offreId) return false;
    return this.candidatures().some(candidature => candidature.offre?.id === offreId);
  }
}
