import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { TypeContratService } from 'app/entities/type-contrat/service/type-contrat.service';
import { LocalisationService } from 'app/entities/localisation/service/localisation.service';
import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model';
import { ILocalisation } from 'app/entities/localisation/localisation.model';

@Component({
  selector: 'jhi-recruteur-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './recruteur-dashboard.component.html',
  styleUrl: './recruteur-dashboard.component.scss',
})
export class RecruteurDashboardComponent implements OnInit {
  showForm = false;
  offreForm: FormGroup;

  typeContrats: ITypeContrat[] = [];
  localisations: ILocalisation[] = [];

  private fb = inject(FormBuilder);
  private offreService = inject(OffreEmploiService);
  private typeContratService = inject(TypeContratService);
  private localisationService = inject(LocalisationService);

  constructor() {
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

  ngOnInit(): void {
    this.typeContratService.query().subscribe({
      next: res => this.typeContrats = res.body ?? [],
    });

    this.localisationService.query().subscribe({
      next: res => this.localisations = res.body ?? [],
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  onSubmit(): void {
    if (this.offreForm.valid) {
      const formValues = this.offreForm.value;

      const offreData = {
        id: null,
        titre: formValues.titre,
        description: formValues.description,
        salaire: formValues.salaire,
        datePublication: formValues.datePublication,
        dateExpiration: formValues.dateExpiration,
        typeContrat: { id: formValues.typeContratId },
        localisation: { id: formValues.localisationId },
        recruteur: { id: 1 }, // plus tard : remplacer par le recruteur connecté
      };

      this.offreService.create(offreData).subscribe({
        next: () => {
          alert('✅ Offre enregistrée avec succès.');
          this.offreForm.reset();
          this.showForm = false;
        },
        error: () => {
          alert('❌ Erreur lors de la soumission.');
        },
      });
    }
  }
}
