import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBasket, NewBasket } from '../basket.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBasket for edit and NewBasketFormGroupInput for create.
 */
type BasketFormGroupInput = IBasket | PartialWithRequiredKeyOf<NewBasket>;

type BasketFormDefaults = Pick<NewBasket, 'id'>;

type BasketFormGroupContent = {
  id: FormControl<IBasket['id'] | NewBasket['id']>;
  basketId: FormControl<IBasket['basketId']>;
  basketState: FormControl<IBasket['basketState']>;
  user: FormControl<IBasket['user']>;
  order: FormControl<IBasket['order']>;
  customer: FormControl<IBasket['customer']>;
};

export type BasketFormGroup = FormGroup<BasketFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BasketFormService {
  createBasketFormGroup(basket: BasketFormGroupInput = { id: null }): BasketFormGroup {
    const basketRawValue = {
      ...this.getFormDefaults(),
      ...basket,
    };
    return new FormGroup<BasketFormGroupContent>({
      id: new FormControl(
        { value: basketRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      basketId: new FormControl(basketRawValue.basketId, {
        validators: [Validators.required],
      }),
      basketState: new FormControl(basketRawValue.basketState, {
        validators: [Validators.required],
      }),
      user: new FormControl(basketRawValue.user),
      order: new FormControl(basketRawValue.order),
      customer: new FormControl(basketRawValue.customer),
    });
  }

  getBasket(form: BasketFormGroup): IBasket | NewBasket {
    return form.getRawValue() as IBasket | NewBasket;
  }

  resetForm(form: BasketFormGroup, basket: BasketFormGroupInput): void {
    const basketRawValue = { ...this.getFormDefaults(), ...basket };
    form.reset(
      {
        ...basketRawValue,
        id: { value: basketRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BasketFormDefaults {
    return {
      id: null,
    };
  }
}
