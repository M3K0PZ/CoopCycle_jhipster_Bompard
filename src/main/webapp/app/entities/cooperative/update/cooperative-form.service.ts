import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICooperative, NewCooperative } from '../cooperative.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICooperative for edit and NewCooperativeFormGroupInput for create.
 */
type CooperativeFormGroupInput = ICooperative | PartialWithRequiredKeyOf<NewCooperative>;

type CooperativeFormDefaults = Pick<NewCooperative, 'id' | 'restaurants'>;

type CooperativeFormGroupContent = {
  id: FormControl<ICooperative['id'] | NewCooperative['id']>;
  cooperativeId: FormControl<ICooperative['cooperativeId']>;
  name: FormControl<ICooperative['name']>;
  area: FormControl<ICooperative['area']>;
  restaurants: FormControl<ICooperative['restaurants']>;
};

export type CooperativeFormGroup = FormGroup<CooperativeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CooperativeFormService {
  createCooperativeFormGroup(cooperative: CooperativeFormGroupInput = { id: null }): CooperativeFormGroup {
    const cooperativeRawValue = {
      ...this.getFormDefaults(),
      ...cooperative,
    };
    return new FormGroup<CooperativeFormGroupContent>({
      id: new FormControl(
        { value: cooperativeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      cooperativeId: new FormControl(cooperativeRawValue.cooperativeId, {
        validators: [Validators.required],
      }),
      name: new FormControl(cooperativeRawValue.name, {
        validators: [Validators.minLength(1), Validators.maxLength(100)],
      }),
      area: new FormControl(cooperativeRawValue.area, {
        validators: [Validators.maxLength(100)],
      }),
      restaurants: new FormControl(cooperativeRawValue.restaurants ?? []),
    });
  }

  getCooperative(form: CooperativeFormGroup): ICooperative | NewCooperative {
    return form.getRawValue() as ICooperative | NewCooperative;
  }

  resetForm(form: CooperativeFormGroup, cooperative: CooperativeFormGroupInput): void {
    const cooperativeRawValue = { ...this.getFormDefaults(), ...cooperative };
    form.reset(
      {
        ...cooperativeRawValue,
        id: { value: cooperativeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CooperativeFormDefaults {
    return {
      id: null,
      restaurants: [],
    };
  }
}
