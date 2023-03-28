import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../cooperative.test-samples';

import { CooperativeFormService } from './cooperative-form.service';

describe('Cooperative Form Service', () => {
  let service: CooperativeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CooperativeFormService);
  });

  describe('Service methods', () => {
    describe('createCooperativeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCooperativeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            cooperativeId: expect.any(Object),
            name: expect.any(Object),
            area: expect.any(Object),
            restaurants: expect.any(Object),
          })
        );
      });

      it('passing ICooperative should create a new form with FormGroup', () => {
        const formGroup = service.createCooperativeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            cooperativeId: expect.any(Object),
            name: expect.any(Object),
            area: expect.any(Object),
            restaurants: expect.any(Object),
          })
        );
      });
    });

    describe('getCooperative', () => {
      it('should return NewCooperative for default Cooperative initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCooperativeFormGroup(sampleWithNewData);

        const cooperative = service.getCooperative(formGroup) as any;

        expect(cooperative).toMatchObject(sampleWithNewData);
      });

      it('should return NewCooperative for empty Cooperative initial value', () => {
        const formGroup = service.createCooperativeFormGroup();

        const cooperative = service.getCooperative(formGroup) as any;

        expect(cooperative).toMatchObject({});
      });

      it('should return ICooperative', () => {
        const formGroup = service.createCooperativeFormGroup(sampleWithRequiredData);

        const cooperative = service.getCooperative(formGroup) as any;

        expect(cooperative).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICooperative should not enable id FormControl', () => {
        const formGroup = service.createCooperativeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCooperative should disable id FormControl', () => {
        const formGroup = service.createCooperativeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
