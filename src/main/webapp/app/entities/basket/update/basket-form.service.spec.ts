import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../basket.test-samples';

import { BasketFormService } from './basket-form.service';

describe('Basket Form Service', () => {
  let service: BasketFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BasketFormService);
  });

  describe('Service methods', () => {
    describe('createBasketFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBasketFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            basketId: expect.any(Object),
            basketState: expect.any(Object),
            user: expect.any(Object),
            order: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });

      it('passing IBasket should create a new form with FormGroup', () => {
        const formGroup = service.createBasketFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            basketId: expect.any(Object),
            basketState: expect.any(Object),
            user: expect.any(Object),
            order: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });
    });

    describe('getBasket', () => {
      it('should return NewBasket for default Basket initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBasketFormGroup(sampleWithNewData);

        const basket = service.getBasket(formGroup) as any;

        expect(basket).toMatchObject(sampleWithNewData);
      });

      it('should return NewBasket for empty Basket initial value', () => {
        const formGroup = service.createBasketFormGroup();

        const basket = service.getBasket(formGroup) as any;

        expect(basket).toMatchObject({});
      });

      it('should return IBasket', () => {
        const formGroup = service.createBasketFormGroup(sampleWithRequiredData);

        const basket = service.getBasket(formGroup) as any;

        expect(basket).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBasket should not enable id FormControl', () => {
        const formGroup = service.createBasketFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBasket should disable id FormControl', () => {
        const formGroup = service.createBasketFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
