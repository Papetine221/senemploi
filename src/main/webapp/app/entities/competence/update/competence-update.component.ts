import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IOffreEmploi } from 'app/entities/offre-emploi/offre-emploi.model';
import { OffreEmploiService } from 'app/entities/offre-emploi/service/offre-emploi.service';
import { ICompetence } from '../competence.model';
import { CompetenceService } from '../service/competence.service';
import { CompetenceFormGroup, CompetenceFormService } from './competence-form.service';

@Component({
  selector: 'jhi-competence-update',
  templateUrl: './competence-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CompetenceUpdateComponent implements OnInit {
  isSaving = false;
  competence: ICompetence | null = null;

  offreEmploisSharedCollection: IOffreEmploi[] = [];

  protected competenceService = inject(CompetenceService);
  protected competenceFormService = inject(CompetenceFormService);
  protected offreEmploiService = inject(OffreEmploiService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CompetenceFormGroup = this.competenceFormService.createCompetenceFormGroup();

  compareOffreEmploi = (o1: IOffreEmploi | null, o2: IOffreEmploi | null): boolean => this.offreEmploiService.compareOffreEmploi(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ competence }) => {
      this.competence = competence;
      if (competence) {
        this.updateForm(competence);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const competence = this.competenceFormService.getCompetence(this.editForm);
    if (competence.id !== null) {
      this.subscribeToSaveResponse(this.competenceService.update(competence));
    } else {
      this.subscribeToSaveResponse(this.competenceService.create(competence));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICompetence>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(competence: ICompetence): void {
    this.competence = competence;
    this.competenceFormService.resetForm(this.editForm, competence);

    this.offreEmploisSharedCollection = this.offreEmploiService.addOffreEmploiToCollectionIfMissing<IOffreEmploi>(
      this.offreEmploisSharedCollection,
      ...(competence.offreEmplois ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.offreEmploiService
      .query()
      .pipe(map((res: HttpResponse<IOffreEmploi[]>) => res.body ?? []))
      .pipe(
        map((offreEmplois: IOffreEmploi[]) =>
          this.offreEmploiService.addOffreEmploiToCollectionIfMissing<IOffreEmploi>(offreEmplois, ...(this.competence?.offreEmplois ?? [])),
        ),
      )
      .subscribe((offreEmplois: IOffreEmploi[]) => (this.offreEmploisSharedCollection = offreEmplois));
  }
}
