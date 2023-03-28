import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BasketFormService } from './basket-form.service';
import { BasketService } from '../service/basket.service';
import { IBasket } from '../basket.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';

import { BasketUpdateComponent } from './basket-update.component';

describe('Basket Management Update Component', () => {
  let comp: BasketUpdateComponent;
  let fixture: ComponentFixture<BasketUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let basketFormService: BasketFormService;
  let basketService: BasketService;
  let userService: UserService;
  let orderService: OrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BasketUpdateComponent],
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
      .overrideTemplate(BasketUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BasketUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    basketFormService = TestBed.inject(BasketFormService);
    basketService = TestBed.inject(BasketService);
    userService = TestBed.inject(UserService);
    orderService = TestBed.inject(OrderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const basket: IBasket = { id: 456 };
      const user: IUser = { id: 34508 };
      basket.user = user;
      const customer: IUser = { id: 10210 };
      basket.customer = customer;

      const userCollection: IUser[] = [{ id: 72431 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user, customer];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call order query and add missing value', () => {
      const basket: IBasket = { id: 456 };
      const order: IOrder = { id: 67856 };
      basket.order = order;

      const orderCollection: IOrder[] = [{ id: 56683 }];
      jest.spyOn(orderService, 'query').mockReturnValue(of(new HttpResponse({ body: orderCollection })));
      const expectedCollection: IOrder[] = [order, ...orderCollection];
      jest.spyOn(orderService, 'addOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      expect(orderService.query).toHaveBeenCalled();
      expect(orderService.addOrderToCollectionIfMissing).toHaveBeenCalledWith(orderCollection, order);
      expect(comp.ordersCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const basket: IBasket = { id: 456 };
      const user: IUser = { id: 13582 };
      basket.user = user;
      const customer: IUser = { id: 74338 };
      basket.customer = customer;
      const order: IOrder = { id: 69369 };
      basket.order = order;

      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.usersSharedCollection).toContain(customer);
      expect(comp.ordersCollection).toContain(order);
      expect(comp.basket).toEqual(basket);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBasket>>();
      const basket = { id: 123 };
      jest.spyOn(basketFormService, 'getBasket').mockReturnValue(basket);
      jest.spyOn(basketService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: basket }));
      saveSubject.complete();

      // THEN
      expect(basketFormService.getBasket).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(basketService.update).toHaveBeenCalledWith(expect.objectContaining(basket));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBasket>>();
      const basket = { id: 123 };
      jest.spyOn(basketFormService, 'getBasket').mockReturnValue({ id: null });
      jest.spyOn(basketService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basket: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: basket }));
      saveSubject.complete();

      // THEN
      expect(basketFormService.getBasket).toHaveBeenCalled();
      expect(basketService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBasket>>();
      const basket = { id: 123 };
      jest.spyOn(basketService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(basketService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareOrder', () => {
      it('Should forward to orderService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(orderService, 'compareOrder');
        comp.compareOrder(entity, entity2);
        expect(orderService.compareOrder).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
