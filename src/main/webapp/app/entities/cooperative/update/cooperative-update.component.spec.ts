import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CooperativeFormService } from './cooperative-form.service';
import { CooperativeService } from '../service/cooperative.service';
import { ICooperative } from '../cooperative.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';

import { CooperativeUpdateComponent } from './cooperative-update.component';

describe('Cooperative Management Update Component', () => {
  let comp: CooperativeUpdateComponent;
  let fixture: ComponentFixture<CooperativeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cooperativeFormService: CooperativeFormService;
  let cooperativeService: CooperativeService;
  let restaurantService: RestaurantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CooperativeUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CooperativeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CooperativeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cooperativeFormService = TestBed.inject(CooperativeFormService);
    cooperativeService = TestBed.inject(CooperativeService);
    restaurantService = TestBed.inject(RestaurantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Restaurant query and add missing value', () => {
      const cooperative: ICooperative = { id: 456 };
      const restaurants: IRestaurant[] = [{ id: 22030 }];
      cooperative.restaurants = restaurants;

      const restaurantCollection: IRestaurant[] = [{ id: 5247 }];
      jest.spyOn(restaurantService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantCollection })));
      const additionalRestaurants = [...restaurants];
      const expectedCollection: IRestaurant[] = [...additionalRestaurants, ...restaurantCollection];
      jest.spyOn(restaurantService, 'addRestaurantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cooperative });
      comp.ngOnInit();

      expect(restaurantService.query).toHaveBeenCalled();
      expect(restaurantService.addRestaurantToCollectionIfMissing).toHaveBeenCalledWith(
        restaurantCollection,
        ...additionalRestaurants.map(expect.objectContaining)
      );
      expect(comp.restaurantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const cooperative: ICooperative = { id: 456 };
      const restaurant: IRestaurant = { id: 67628 };
      cooperative.restaurants = [restaurant];

      activatedRoute.data = of({ cooperative });
      comp.ngOnInit();

      expect(comp.restaurantsSharedCollection).toContain(restaurant);
      expect(comp.cooperative).toEqual(cooperative);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICooperative>>();
      const cooperative = { id: 123 };
      jest.spyOn(cooperativeFormService, 'getCooperative').mockReturnValue(cooperative);
      jest.spyOn(cooperativeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cooperative });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cooperative }));
      saveSubject.complete();

      // THEN
      expect(cooperativeFormService.getCooperative).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cooperativeService.update).toHaveBeenCalledWith(expect.objectContaining(cooperative));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICooperative>>();
      const cooperative = { id: 123 };
      jest.spyOn(cooperativeFormService, 'getCooperative').mockReturnValue({ id: null });
      jest.spyOn(cooperativeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cooperative: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cooperative }));
      saveSubject.complete();

      // THEN
      expect(cooperativeFormService.getCooperative).toHaveBeenCalled();
      expect(cooperativeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICooperative>>();
      const cooperative = { id: 123 };
      jest.spyOn(cooperativeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cooperative });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cooperativeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRestaurant', () => {
      it('Should forward to restaurantService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(restaurantService, 'compareRestaurant');
        comp.compareRestaurant(entity, entity2);
        expect(restaurantService.compareRestaurant).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
