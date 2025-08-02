import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICompetence, NewCompetence } from '../competence.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICompetence for edit and NewCompetenceFormGroupInput for create.
 */
type CompetenceFormGroupInput = ICompetence | PartialWithRequiredKeyOf<NewCompetence>;

type CompetenceFormDefaults = Pick<NewCompetence, 'id' | 'offreEmplois'>;

type CompetenceFormGroupContent = {
  id: FormControl<ICompetence['id'] | NewCompetence['id']>;
  nom: FormControl<ICompetence['nom']>;
  offreEmplois: FormControl<ICompetence['offreEmplois']>;
};

export type CompetenceFormGroup = FormGroup<CompetenceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CompetenceFormService {
  createCompetenceFormGroup(competence: CompetenceFormGroupInput = { id: null }): CompetenceFormGroup {
    const competenceRawValue = {
      ...this.getFormDefaults(),
      ...competence,
    };
    return new FormGroup<CompetenceFormGroupContent>({
      id: new FormControl(
        { value: competenceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(competenceRawValue.nom, {
        validators: [Validators.required],
      }),
      offreEmplois: new FormControl(competenceRawValue.offreEmplois ?? []),
    });
  }

  getCompetence(form: CompetenceFormGroup): ICompetence | NewCompetence {
    return form.getRawValue() as ICompetence | NewCompetence;
  }

  resetForm(form: CompetenceFormGroup, competence: CompetenceFormGroupInput): void {
    const competenceRawValue = { ...this.getFormDefaults(), ...competence };
    form.reset(
      {
        ...competenceRawValue,
        id: { value: competenceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CompetenceFormDefaults {
    return {
      id: null,
      offreEmplois: [],
    };
  }
}
