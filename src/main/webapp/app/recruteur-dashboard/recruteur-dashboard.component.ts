import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { TypeContratService } from 'app/entities/type-contrat/service/type-contrat.service';
import { LocalisationService } from 'app/entities/localisation/service/localisation.service';
import { ITypeContrat } from 'app/entities/type-contrat/type-contrat.model';
import { ILocalisation } from 'app/entities/localisation/localisation.model';
import { IOffreEmploi, NewOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';

@Component({
  selector: 'jhi-recruteur-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './recruteur-dashboard.component.html',
  styleUrl: './recruteur-dashboard.component.scss',
})
export class RecruteurDashboardComponent implements OnInit {
  showForm = false;
  offreForm: FormGroup;

  typeContrats: ITypeContrat[] = [];
  localisations: ILocalisation[] = [];
  offresPubliees: IOffreEmploi[] = [];

  offreEnEdition: IOffreEmploi | null = null;


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
  

  ngOnInit(): void {
    this.typeContratService.query().subscribe({
      next: res => (this.typeContrats = res.body ?? []),
    });

    this.localisationService.query().subscribe({
      next: res => (this.localisations = res.body ?? []),
    });

    this.loadOffres();
  }

  loadOffres(): void {
    this.offreService.query().subscribe({
      next: res => {
        this.offresPubliees = res.body ?? [];
        console.log('Offres reçues :', this.offresPubliees); // ✅ Pour voir typeContrat.nom et localisation.ville
      },
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  supprimerOffre(id: number): void {
    if (confirm("Voulez-vous vraiment supprimer cette offre ?")) {
      this.offreService.delete(id).subscribe(() => {
        this.offresPubliees = this.offresPubliees.filter(offre => offre.id !== id);
      });
    }
  }
  
  

  onSubmit(): void {
    if (this.offreForm.valid) {
      const formValues = this.offreForm.value;
  
      if (this.offreEnEdition) {
        // ✅ Cas MODIFICATION (update)
        const offreData: IOffreEmploi = {
          id: this.offreEnEdition.id!,
          titre: formValues.titre,
          description: formValues.description,
          salaire: formValues.salaire,
          datePublication: formValues.datePublication,
          dateExpiration: formValues.dateExpiration,
          typeContrat: { id: formValues.typeContratId },
          localisation: { id: formValues.localisationId },
          recruteur: { id: 1 },
        };
  
        this.offreService.update(offreData).subscribe({
          next: () => {
            alert('✏️ Offre modifiée avec succès.');
            this.offreForm.reset();
            this.showForm = false;
            this.offreEnEdition = null;
            this.loadOffres();
          },
          error: () => {
            alert('❌ Erreur lors de la modification.');
          },
        });
  
      } else {
        // ✅ Cas CRÉATION (create)
        const offreData: NewOffreEmploi = {
          id: null,
          titre: formValues.titre,
          description: formValues.description,
          salaire: formValues.salaire,
          datePublication: formValues.datePublication,
          dateExpiration: formValues.dateExpiration,
          typeContrat: { id: formValues.typeContratId },
          localisation: { id: formValues.localisationId },
          recruteur: { id: 1 },
        };
  
        this.offreService.create(offreData).subscribe({
          next: () => {
            alert('✅ Offre enregistrée avec succès.');
            this.offreForm.reset();
            this.showForm = false;
            this.loadOffres();
          },
          error: () => {
            alert('❌ Erreur lors de l’enregistrement.');
          },
        });
      }
    }
  }
  
    
  
}
