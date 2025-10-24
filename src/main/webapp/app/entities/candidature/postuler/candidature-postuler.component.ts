import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
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
  newCvFile: File | null = null;
  cvUploadError: string | null = null;
  formSubmitted = false;
  showCvPreview = false;
  showNewCvPreview = false;
  newCvPreviewUrl: any = null;

  protected eventManager = inject(EventManager);
  protected candidatureService = inject(CandidatureService);
  protected candidatService = inject(CandidatService);
  protected offreEmploiService = inject(OffreEmploiService);
  protected accountService = inject(AccountService);
  protected activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);
  protected fb = inject(FormBuilder);
  protected sanitizer = inject(DomSanitizer);

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
    this.formSubmitted = true;
    
    // Vérifier si le CV est présent
    if (!this.candidat?.cv && !this.newCvFile) {
      this.cvUploadError = 'Le CV est obligatoire pour postuler. Veuillez télécharger votre CV.';
      return;
    }

    if (this.editForm.valid && this.candidat && this.offreEmploi) {
      this.isSaving = true;
      this.cvUploadError = null;
      
      // Mettre à jour le profil candidat avec les nouvelles informations
      const updatedCandidat = {
        ...this.candidat,
        telephone: this.editForm.get('telephone')?.value,
        adresse: this.editForm.get('adresse')?.value
      };

      // Si un nouveau CV a été sélectionné, l'ajouter
      if (this.newCvFile) {
        this.convertFileToBase64(this.newCvFile).then(base64 => {
          updatedCandidat.cv = base64;
          updatedCandidat.cvContentType = this.newCvFile!.type;
          this.updateCandidatAndCreateCandidature(updatedCandidat);
        }).catch(error => {
          this.cvUploadError = 'Erreur lors de la lecture du fichier CV.';
          this.isSaving = false;
        });
      } else {
        this.updateCandidatAndCreateCandidature(updatedCandidat);
      }
    }
  }

  private updateCandidatAndCreateCandidature(updatedCandidat: any): void {

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
    this.router.navigate(['/candidat-dashboard/mes-candidatures']);
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

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Vérifier le type de fichier
      const allowedTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
      if (!allowedTypes.includes(file.type)) {
        this.cvUploadError = 'Format de fichier non accepté. Veuillez choisir un fichier PDF, DOC ou DOCX.';
        return;
      }
      
      // Vérifier la taille (max 5 Mo)
      const maxSize = 5 * 1024 * 1024; // 5 MB
      if (file.size > maxSize) {
        this.cvUploadError = 'Le fichier est trop volumineux. Taille maximale : 5 Mo.';
        return;
      }
      
      this.newCvFile = file;
      this.cvUploadError = null;
    }
  }

  removeCv(): void {
    this.newCvFile = null;
    this.newCvPreviewUrl = null;
    this.showNewCvPreview = false;
    const fileInput = document.getElementById('file_cv') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  downloadCv(): void {
    if (this.candidat?.cv && this.candidat?.cvContentType) {
      const byteCharacters = atob(this.candidat.cv);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: this.candidat.cvContentType });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `CV_${this.candidat.id || 'candidat'}.${this.getFileExtension(this.candidat.cvContentType)}`;
      link.click();
      window.URL.revokeObjectURL(url);
    }
  }

  getCvSize(): string {
    if (this.candidat?.cv) {
      const bytes = atob(this.candidat.cv).length;
      return this.formatFileSize(bytes);
    }
    return '';
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) {
      return bytes + ' B';
    } else if (bytes < 1024 * 1024) {
      return (bytes / 1024).toFixed(2) + ' KB';
    } else {
      return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
    }
  }

  private getFileExtension(contentType: string): string {
    switch (contentType) {
      case 'application/pdf':
        return 'pdf';
      case 'application/msword':
        return 'doc';
      case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
        return 'docx';
      default:
        return 'bin';
    }
  }

  private convertFileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const base64String = (reader.result as string).split(',')[1];
        resolve(base64String);
      };
      reader.onerror = error => reject(error);
      reader.readAsDataURL(file);
    });
  }

  previewCv(): void {
    this.showCvPreview = !this.showCvPreview;
    this.showNewCvPreview = false;
  }

  previewNewCv(): void {
    if (this.newCvFile) {
      // Créer une URL pour le fichier local
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.newCvPreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(e.target.result);
        this.showNewCvPreview = true;
        this.showCvPreview = false;
      };
      reader.readAsDataURL(this.newCvFile);
    }
  }

  getCvPreviewUrl(): SafeResourceUrl | null {
    if (this.candidat?.cv && this.candidat?.cvContentType) {
      const byteCharacters = atob(this.candidat.cv);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: this.candidat.cvContentType });
      const url = URL.createObjectURL(blob);
      return this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
    return null;
  }

  isPdfCv(contentType: string | null | undefined): boolean {
    return contentType === 'application/pdf';
  }

  getFileName(): string {
    if (this.candidat?.id) {
      return `CV_${this.candidat.id}.${this.getFileExtension(this.candidat.cvContentType || '')}`;
    }
    return 'CV.pdf';
  }
}
