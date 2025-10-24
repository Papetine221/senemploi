import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { TypeContratService } from 'app/entities/type-contrat/service/type-contrat.service';
import { LocalisationService } from 'app/entities/localisation/service/localisation.service';
import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model';
import { ILocalisation } from 'app/entities/localisation/localisation.model';
import { IOffreEmploi, NewOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { RecruteurService } from 'app/entities/recruteur/service/recruteur.service';
import { IRecruteur } from 'app/entities/recruteur/recruteur.model';

@Component({
  selector: 'jhi-recruteur-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './recruteur-dashboard.component.html',
  styleUrl: './recruteur-dashboard.component.scss',
})
export class RecruteurDashboardComponent implements OnInit {
  // 🟦 États principaux
  showForm = false;
  offreForm: FormGroup;
  offreEnEdition: IOffreEmploi | null = null;

  // 🟨 Données
  typeContrats: ITypeContrat[] = [];
  localisations: ILocalisation[] = [];
  offresPubliees: IOffreEmploi[] = [];

  // 🟩 Recruteur connecté
  recruteur?: IRecruteur;
  loadingRecruteur = true;

  // 🧩 Injections de services
  private fb = inject(FormBuilder);
  private offreService = inject(OffreEmploiService);
  private typeContratService = inject(TypeContratService);
  private localisationService = inject(LocalisationService);
  private recruteurService = inject(RecruteurService);

  constructor() {
    // 🧱 Construction du formulaire
    this.offreForm = this.fb.group({
      titre: ['', Validators.required],
      description: ['', Validators.required],
      salaire: [0, [Validators.required, Validators.min(0)]],
      datePublication: ['', Validators.required],
      dateExpiration: ['', Validators.required],
      typeContratId: [null, Validators.required],
      localisationId: [null, Validators.required],
    });
  }

  // 🔹 Au démarrage du composant
  ngOnInit(): void {
    this.loadCurrentRecruteur(); // 🧠 récupère le recruteur connecté
    this.loadOffres();
    this.typeContratService.query().subscribe({
      next: res => (this.typeContrats = res.body ?? []),
    });
    this.localisationService.query().subscribe({
      next: res => (this.localisations = res.body ?? []),
    });
  }

  // 🔹 Récupérer le recruteur connecté
  loadCurrentRecruteur(): void {
    this.recruteurService.findCurrent().subscribe({
      next: (res) => {
        this.recruteur = res.body ?? undefined;
        this.loadingRecruteur = false;
        console.log('✅ Recruteur connecté :', this.recruteur);
      },
      error: (err) => {
        console.error('❌ Erreur chargement recruteur connecté', err);
        this.loadingRecruteur = false;
      },
    });
  }

  // 🔹 Charger les offres du recruteur connecté
  loadOffres(): void {
    this.offreService.getByRecruteurConnecte().subscribe({
      next: res => {
        this.offresPubliees = res ?? [];
        console.log('📋 Offres du recruteur connecté :', this.offresPubliees);
      },
      error: err => console.error('Erreur chargement des offres', err),
    });
  }

  // 🔹 Afficher / cacher le formulaire
  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  // 🔹 Supprimer une offre
  supprimerOffre(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cette offre ?')) {
      this.offreService.delete(id).subscribe(() => {
        this.offresPubliees = this.offresPubliees.filter(offre => offre.id !== id);
      });
    }
  }

  // 🔹 Modifier une offre
  modifierOffre(offre: IOffreEmploi): void {
    this.offreEnEdition = offre;
    this.showForm = true;
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

  // 🔹 Soumettre le formulaire (création ou modification)
  onSubmit(): void {
    if (this.offreForm.valid) {
      const formValues = this.offreForm.value;

      this.recruteurService.findCurrent().subscribe({
        next: (res) => {
          const recruteur = res.body;
          if (!recruteur?.id) {
            alert('❌ Recruteur non trouvé !');
            return;
          }

          const recruteurData = { id: recruteur.id };

          if (this.offreEnEdition) {
            // 🟢 Cas MODIFICATION
            const offreData: IOffreEmploi = {
              id: this.offreEnEdition.id!,
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
                this.offreForm.reset();
                this.showForm = false;
                this.offreEnEdition = null;
                this.loadOffres();
              },
              error: () => alert('❌ Erreur lors de la modification.'),
            });
          } else {
            // 🟢 Cas CRÉATION
            const offreData: NewOffreEmploi = {
              id: null,
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
                this.offreForm.reset();
                this.showForm = false;
                this.loadOffres();
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
