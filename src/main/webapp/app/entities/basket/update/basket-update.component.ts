import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BasketFormService, BasketFormGroup } from './basket-form.service';
import { IBasket } from '../basket.model';
import { BasketService } from '../service/basket.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';
import { BasketState } from 'app/entities/enumerations/basket-state.model';

@Component({
  selector: 'jhi-basket-update',
  templateUrl: './basket-update.component.html',
})
export class BasketUpdateComponent implements OnInit {
  isSaving = false;
  basket: IBasket | null = null;
  basketStateValues = Object.keys(BasketState);

  usersSharedCollection: IUser[] = [];
  ordersCollection: IOrder[] = [];

  editForm: BasketFormGroup = this.basketFormService.createBasketFormGroup();

  constructor(
    protected basketService: BasketService,
    protected basketFormService: BasketFormService,
    protected userService: UserService,
    protected orderService: OrderService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareOrder = (o1: IOrder | null, o2: IOrder | null): boolean => this.orderService.compareOrder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ basket }) => {
      this.basket = basket;
      if (basket) {
        this.updateForm(basket);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const basket = this.basketFormService.getBasket(this.editForm);
    if (basket.id !== null) {
      this.subscribeToSaveResponse(this.basketService.update(basket));
    } else {
      this.subscribeToSaveResponse(this.basketService.create(basket));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBasket>>): void {
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

  protected updateForm(basket: IBasket): void {
    this.basket = basket;
    this.basketFormService.resetForm(this.editForm, basket);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      basket.user,
      basket.customer
    );
    this.ordersCollection = this.orderService.addOrderToCollectionIfMissing<IOrder>(this.ordersCollection, basket.order);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.basket?.user, this.basket?.customer)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.orderService
      .query({ filter: 'basket-is-null' })
      .pipe(map((res: HttpResponse<IOrder[]>) => res.body ?? []))
      .pipe(map((orders: IOrder[]) => this.orderService.addOrderToCollectionIfMissing<IOrder>(orders, this.basket?.order)))
      .subscribe((orders: IOrder[]) => (this.ordersCollection = orders));
  }
}
