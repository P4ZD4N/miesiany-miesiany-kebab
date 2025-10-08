import { Component, OnInit } from '@angular/core';
import { LangService } from '../../../services/lang/lang.service';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { OrdersService } from '../../../services/orders/orders.service';
import { OrderResponse } from '../../../responses/responses';
import { OrderStatus } from '../../../enums/order-status.enum';
import Swal from 'sweetalert2';
import {
  RemovedOrderRequest,
  UpdatedOrderRequest,
} from '../../../requests/requests';
import { OrderService } from '../../../services/order/order.service';
import { TranslationHelperService } from '../../../services/translation-helper/translation-helper.service';
import { AlertService } from '../../../services/alert/alert.service';

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './order-management.component.html',
  styleUrl: './order-management.component.scss',
})
export class OrderManagementComponent implements OnInit {
  OrderStatus = OrderStatus;
  statusFilter: OrderStatus | null = null;

  orders: OrderResponse[] = [];

  isTodayFilterActive: boolean = true;

  get filteredOrders(): OrderResponse[] {
    return this.orders.filter((order) => {
      const matchesStatus = this.statusFilter
        ? order.order_status === this.statusFilter
        : true;
      const isToday = this.isTodayFilterActive;

      const orderDate = new Date(order.created_at);
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      const isOrderToday = orderDate >= today;
      const matchesDate = isToday ? isOrderToday : !isOrderToday;

      return matchesStatus && matchesDate;
    });
  }

  constructor(
    private langService: LangService,
    private authenticationService: AuthenticationService,
    private orderService: OrderService,
    private ordersService: OrdersService,
    private alertService: AlertService,
    private translationHelper: TranslationHelperService
  ) {}

  ngOnInit(): void {
    if (this.isManager() || this.isEmployee()) this.loadOrders();
  }

  private loadOrders(): void {
    this.ordersService.getOrders().subscribe(
      (data: OrderResponse[]) =>
        (this.orders = data.sort((a, b) => a.id - b.id)),
      (error) => console.log('Error loading orders', error)
    );
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }

  protected isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  protected getTranslatedMealName(mealName: string): string {
    return this.translationHelper.getTranslatedMealName(mealName);
  }

  protected getTranslatedBeverageName(beverageName: string): string {
    return this.translationHelper.getTranslatedBeverageName(beverageName);
  }

  protected getTranslatedAddonName(addonName: string): string {
    return this.translationHelper.getTranslatedAddonName(addonName);
  }

  protected getTranslatedIngredientName(ingredientName: string): string {
    return this.translationHelper.getTranslatedIngredientName(ingredientName);
  }

  protected getTranslatedOrderStatus(orderStatus: OrderStatus): string {
    return this.translationHelper.getTranslatedOrderStatus(orderStatus);
  }

  protected setFilter(
    status: OrderStatus | null,
    isTodayFilterActive: boolean
  ): void {
    this.statusFilter = status;
    this.isTodayFilterActive = isTodayFilterActive;
  }

  protected getButtonClass(
    status: OrderStatus | null,
    isTodayFilterActive: boolean
  ): string {
    const isActive =
      this.statusFilter === status &&
      this.isTodayFilterActive === isTodayFilterActive;
    return isActive ? 'btn-success' : 'btn-dark';
  }

  protected addOrder(): void {
    this.orderService.selectNextOrderItem();
  }

  updateOrder(order: OrderResponse): void {
    this.alertService.showUpdateOrderAlert(order).then((result) => {
      if (result.isConfirmed && result.value) {
        this.ordersService
          .updateOrder(result.value as UpdatedOrderRequest)
          .subscribe(() => {
            this.alertService.showSuccessfulOrderUpdateAlert(order);
            this.loadOrders();
          });
      }
    });
  }

  removeOrder(order: OrderResponse): void {
    this.alertService.showRemoveOrderAlert(order).then((confirmed) => {
      if (!confirmed) return;

      this.ordersService
        .removeOrder({ id: order.id } as RemovedOrderRequest)
        .subscribe(() => {
          this.alertService.showSuccessfulOrderRemoveAlert(order);
          this.loadOrders();
        });
    });
  }
}
