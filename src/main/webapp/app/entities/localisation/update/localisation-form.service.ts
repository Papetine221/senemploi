import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILocalisation, NewLocalisation } from '../localisation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILocalisation for edit and NewLocalisationFormGroupInput for create.
 */
type LocalisationFormGroupInput = ILocalisation | PartialWithRequiredKeyOf<NewLocalisation>;

type LocalisationFormDefaults = Pick<NewLocalisation, 'id'>;

type LocalisationFormGroupContent = {
  id: FormControl<ILocalisation['id'] | NewLocalisation['id']>;
  region: FormControl<ILocalisation['region']>;
  departement: FormControl<ILocalisation['departement']>;
  ville: FormControl<ILocalisation['ville']>;
};

export type LocalisationFormGroup = FormGroup<LocalisationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LocalisationFormService {
  createLocalisationFormGroup(localisation: LocalisationFormGroupInput = { id: null }): LocalisationFormGroup {
    const localisationRawValue = {
      ...this.getFormDefaults(),
      ...localisation,
    };
    return new FormGroup<LocalisationFormGroupContent>({
      id: new FormControl(
        { value: localisationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      region: new FormControl(localisationRawValue.region, {
        validators: [Validators.required],
      }),
      departement: new FormControl(localisationRawValue.departement),
      ville: new FormControl(localisationRawValue.ville, {
        validators: [Validators.required],
      }),
    });
  }

  getLocalisation(form: LocalisationFormGroup): ILocalisation | NewLocalisation {
    return form.getRawValue() as ILocalisation | NewLocalisation;
  }

  resetForm(form: LocalisationFormGroup, localisation: LocalisationFormGroupInput): void {
    const localisationRawValue = { ...this.getFormDefaults(), ...localisation };
    form.reset(
      {
        ...localisationRawValue,
        id: { value: localisationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LocalisationFormDefaults {
    return {
      id: null,
    };
  }
}
