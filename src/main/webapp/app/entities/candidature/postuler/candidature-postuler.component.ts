import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { AccountService } from 'app/core/auth/account.service';
import { CandidatService } from 'app/entities/candidat/service/candidat.service';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { CandidatureService } from '../service/candidature.service';
import { ICandidature, NewCandidature } from '../candidature.model';
import { ICandidat } from 'app/entities/candidat/candidat.model';
import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { StatutCandidature } from 'app/entities/enumerations/statut-candidature.model';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-candidature-postuler',
  templateUrl: './candidature-postuler.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterModule],
})
export class CandidaturePostulerComponent implements OnInit {
  isSaving = false;
  isLoading = true;
  candidat: ICandidat | null = null;
  offreEmploi: IOffreEmploi | null = null;
  offreId: number | null = null;

  protected eventManager = inject(EventManager);
  protected candidatureService = inject(CandidatureService);
  protected candidatService = inject(CandidatService);
  protected offreEmploiService = inject(OffreEmploiService);
  protected accountService = inject(AccountService);
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);
  protected fb = inject(FormBuilder);

  editForm: FormGroup = this.fb.group({
    lettreMotivation: ['', [Validators.required, Validators.minLength(50), Validators.maxLength(2000)]],
    telephone: ['', [Validators.required, Validators.pattern(/^[+]?[0-9\s\-\(\)]{8,15}$/)]],
    adresse: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(200)]],
  });

  ngOnInit(): void {
    // Récupérer l'ID de l'offre depuis les paramètres de requête
    this.activatedRoute.queryParams.subscribe(params => {
      this.offreId = params['offre'] ? +params['offre'] : null;
      if (this.offreId) {
        this.loadOffreEmploi();
        this.loadCurrentCandidat();
      } else {
        this.router.navigate(['/offre-emploi']);
      }
    });
  }

  private loadOffreEmploi(): void {
    if (this.offreId) {
      this.offreEmploiService.find(this.offreId).subscribe({
        next: (res: HttpResponse<IOffreEmploi>) => {
          this.offreEmploi = res.body;
        },
        error: () => {
          this.router.navigate(['/offre-emploi']);
        }
      });
    }
  }

  private loadCurrentCandidat(): void {
    this.accountService.identity().subscribe(account => {
      if (account?.login) {
        this.candidatService.findByUserLogin(account.login).subscribe({
          next: (res: HttpResponse<ICandidat>) => {
            this.candidat = res.body;
            this.prefillForm();
            this.isLoading = false;
          },
          error: () => {
            this.eventManager.broadcast(
              new EventWithContent<AlertError>('senemploiV4App.error', { 
                key: 'error.candidat.notFound',
                message: 'Profil candidat non trouvé. Veuillez créer votre profil candidat.' 
              })
            );
            this.router.navigate(['/candidat/new']);
          }
        });
      } else {
        this.router.navigate(['/login']);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    if (this.editForm.valid && this.candidat && this.offreEmploi) {
      this.isSaving = true;
      
      // Mettre à jour le profil candidat avec les nouvelles informations
      const updatedCandidat = {
        ...this.candidat,
        telephone: this.editForm.get('telephone')?.value,
        adresse: this.editForm.get('adresse')?.value
      };

      // Mettre à jour le candidat d'abord, puis créer la candidature
      this.candidatService.update(updatedCandidat).subscribe({
        next: () => {
          // Créer la candidature après la mise à jour du profil
          const candidature: NewCandidature = {
            id: null,
            lettreMotivation: this.editForm.get('lettreMotivation')?.value,
            datePostulation: dayjs(),
            statut: StatutCandidature.EN_ATTENTE,
            candidat: { id: this.candidat!.id },
            offre: { id: this.offreEmploi!.id }
          };

          this.subscribeToSaveResponse(this.candidatureService.create(candidature));
        },
        error: () => {
          this.eventManager.broadcast(
            new EventWithContent<AlertError>('senemploiV4App.error', { 
              key: 'error.candidat.update',
              message: 'Erreur lors de la mise à jour de votre profil. Veuillez réessayer.' 
            })
          );
          this.isSaving = false;
        }
      });
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICandidature>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.eventManager.broadcast(
      new EventWithContent<AlertError>('senemploiV4App.success', { 
        key: 'success.candidature.created',
        message: 'Votre candidature a été envoyée avec succès !' 
      })
    );
    this.router.navigate(['/offre-emploi']);
  }

  protected onSaveError(): void {
    this.eventManager.broadcast(
      new EventWithContent<AlertError>('senemploiV4App.error', { 
        key: 'error.candidature.creation',
        message: 'Erreur lors de l\'envoi de votre candidature. Veuillez réessayer.' 
      })
    );
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  private prefillForm(): void {
    if (this.candidat) {
      this.editForm.patchValue({
        telephone: this.candidat.telephone || '',
        adresse: this.candidat.adresse || ''
      });
    }
  }

  get lettreMotivation() {
    return this.editForm.get('lettreMotivation');
  }

  get telephone() {
    return this.editForm.get('telephone');
  }

  get adresse() {
    return this.editForm.get('adresse');
  }
}
