import { Component, inject, OnInit } from '@angular/core'; // Import du décorateur @Component, d'OnInit et de la fonction inject
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms'; // Outils pour gérer les formulaires réactifs
import { CommonModule } from '@angular/common'; // Module commun (ngIf, ngFor, etc.)

import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service'; // Service pour gérer les offres d'emploi
import { TypeContratService } from 'app/entities/type-contrat/service/type-contrat.service'; // Service pour charger les types de contrat
import { LocalisationService } from 'app/entities/localisation/service/localisation.service'; // Service pour gérer les localisations
import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model'; // Interface du modèle TypeContrat
import { ILocalisation } from 'app/entities/localisation/localisation.model'; // Interface du modèle Localisation
import { IOffreEmploi, NewOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model'; // Interface et modèle pour les offres d'emploi
import { RecruteurService } from 'app/entities/recruteur/service/recruteur.service'; // Service pour récupérer le recruteur connecté
import { IRecruteur } from 'app/entities/recruteur/recruteur.model'; // Interface représentant un recruteur

// DÉCORATEUR DU COMPOSANT 
@Component({
  selector: 'jhi-recruteur-dashboard', // Nom du composant dans le HTML
  standalone: true, // Indique que le composant est autonome (pas besoin de module parent)
  imports: [CommonModule, ReactiveFormsModule], // Modules nécessaires pour le fonctionnement du composant
  templateUrl: './recruteur-dashboard.component.html', // Fichier HTML lié
  styleUrl: './recruteur-dashboard.component.scss', // Feuille de style liée
})
export class RecruteurDashboardComponent implements OnInit {
  //  VARIABLES D'ÉTAT =
  showForm = false; // Booléen pour afficher ou masquer le formulaire
  offreForm: FormGroup; // Formulaire réactif Angular pour saisir les offres
  offreEnEdition: IOffreEmploi | null = null; // Contient l'offre en cours de modification (ou null si création)

  // DONNÉES MÉTIERS 
  typeContrats: ITypeContrat[] = []; // Liste des types de contrat disponibles
  localisations: ILocalisation[] = []; // Liste des localisations disponibles
  offresPubliees: IOffreEmploi[] = []; // Liste des offres déjà publiées par le recruteur

  recruteur?: IRecruteur; // Informations du recruteur connecté
  loadingRecruteur = true; // Indique si la récupération du recruteur est en cours

  //  INJECTION DES SERVICES 
  private fb = inject(FormBuilder); // Permet de créer des formulaires dynamiques
  private offreService = inject(OffreEmploiService); // Service CRUD pour les offres d'emploi
  private typeContratService = inject(TypeContratService); // Service pour récupérer les types de contrat
  private localisationService = inject(LocalisationService); // Service pour récupérer les localisations
  private recruteurService = inject(RecruteurService); // Service pour obtenir le recruteur connecté

  //  CONSTRUCTEUR 
  constructor() {
    // Création et configuration du formulaire réactif avec validation
    this.offreForm = this.fb.group({
      titre: ['', Validators.required], // Champ obligatoire
      description: ['', Validators.required], // Champ obligatoire
      salaire: [0, [Validators.required, Validators.min(0)]], // Salaire minimal = 0
      datePublication: ['', Validators.required], // Date obligatoire
      dateExpiration: ['', Validators.required], // Date obligatoire
      typeContratId: [null, Validators.required], // Choix du type de contrat obligatoire
      localisationId: [null, Validators.required], // Choix de la localisation obligatoire
    });
  }

  //  HOOK ANGULAR : ngOnInit
  ngOnInit(): void {
    this.loadCurrentRecruteur(); //  Récupère le recruteur actuellement connecté
    this.loadOffres();           //  Charge toutes les offres déjà publiées
    //  Chargement des listes déroulantes pour le formulaire
    this.typeContratService.query().subscribe({
      next: res => (this.typeContrats = res.body ?? []), // Stocke les types de contrat
    });
    this.localisationService.query().subscribe({
      next: res => (this.localisations = res.body ?? []), // Stocke les localisations
    });
  }

  // ========================= RÉCUPÉRATION DU RECRUTEUR CONNECTÉ =========================
  loadCurrentRecruteur(): void {
    this.recruteurService.findCurrent().subscribe({
      next: (res) => {
        this.recruteur = res.body ?? undefined; // Stocke les infos du recruteur connecté
        this.loadingRecruteur = false; // Fin du chargement
        console.log('👤 Recruteur connecté :', this.recruteur);
      },
      error: (err) => {
        console.error('❌ Erreur chargement recruteur connecté', err);
        this.loadingRecruteur = false;
      },
    });
  }

  // CHARGEMENT DES OFFRES DU RECRUTEUR 
  loadOffres(): void {
    this.offreService.getByRecruteurConnecte().subscribe({
      next: res => {
        this.offresPubliees = res ?? []; // Enregistre les offres du recruteur
        console.log('📋 Offres du recruteur connecté :', this.offresPubliees);
      },
      error: err => console.error('❌ Erreur chargement des offres', err),
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm; // Bascule entre afficher et cacher le formulaire
  }

  supprimerOffre(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cette offre ?')) {
      this.offreService.delete(id).subscribe(() => {
        // Met à jour la liste après suppression
        this.offresPubliees = this.offresPubliees.filter(offre => offre.id !== id);
      });
    }
  }

  modifierOffre(offre: IOffreEmploi): void {
    this.offreEnEdition = offre; // Sauvegarde l’offre sélectionnée
    this.showForm = true; // Affiche le formulaire
    // Remplit les champs du formulaire avec les données existantes
    this.offreForm.patchValue({
      titre: offre.titre,
      description: offre.description,
      salaire: offre.salaire,
      datePublication: offre.datePublication,
      dateExpiration: offre.dateExpiration,
      typeContratId: offre.typeContrat?.id ?? null,
      localisationId: offre.localisation?.id ?? null,
    });
  }

  onSubmit(): void {
    // Vérifie si le formulaire est valide avant d’envoyer les données
    if (this.offreForm.valid) {
      const formValues = this.offreForm.value; // Récupère toutes les valeurs saisies

      // Recharge le recruteur connecté avant d’enregistrer
      this.recruteurService.findCurrent().subscribe({
        next: (res) => {
          const recruteur = res.body; // Données du recruteur connecté
          if (!recruteur?.id) {
            alert('❌ Recruteur non trouvé !');
            return;
          }

          const recruteurData = { id: recruteur.id }; // Crée un petit objet pour le lier à l’offre

          if (this.offreEnEdition) {
            //  CAS 1 : MODIFICATION D’UNE OFFRE EXISTANTE
            const offreData: IOffreEmploi = {
              id: this.offreEnEdition.id!, // On conserve l’ID existant
              titre: formValues.titre,
              description: formValues.description,
              salaire: formValues.salaire,
              datePublication: formValues.datePublication,
              dateExpiration: formValues.dateExpiration,
              typeContrat: { id: formValues.typeContratId },
              localisation: { id: formValues.localisationId },
              recruteur: recruteurData,
            };

            this.offreService.update(offreData).subscribe({
              next: () => {
                alert('✏️ Offre modifiée avec succès.');
                this.offreForm.reset();      // Vide le formulaire
                this.showForm = false;       // Cache le formulaire
                this.offreEnEdition = null;  // Sort du mode édition
                this.loadOffres();           // Recharge les offres
              },
              error: () => alert('❌ Erreur lors de la modification.'),
            });
          } else {
            // CAS 2 : CRÉATION D’UNE NOUVELLE OFFRE
            const offreData: NewOffreEmploi = {
              id: null, // Pas d’ID car création
              titre: formValues.titre,
              description: formValues.description,
              salaire: formValues.salaire,
              datePublication: formValues.datePublication,
              dateExpiration: formValues.dateExpiration,
              typeContrat: { id: formValues.typeContratId },
              localisation: { id: formValues.localisationId },
              recruteur: recruteurData,
            };

            this.offreService.create(offreData).subscribe({
              next: () => {
                alert('✅ Offre enregistrée avec succès.');
                this.offreForm.reset(); // Vide le formulaire
                this.showForm = false;  // Cache le formulaire
                this.loadOffres();      // Recharge la liste d’offres
              },
              error: () => alert('❌ Erreur lors de l’enregistrement.'),
            });
          }
        },
        error: () => {
          alert('Impossible de récupérer le recruteur connecté.');
        },
      });
    }
  }
}
