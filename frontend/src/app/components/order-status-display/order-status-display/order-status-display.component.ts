import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { OrdersService } from '../../../services/orders/orders.service';
import { OrderResponse } from '../../../responses/responses';
import { OrderStatus } from '../../../enums/order-status.enum';

@Component({
  selector: 'app-order-status-display',
  standalone: true,
  imports: [CommonModule, TranslateModule, RouterModule],
  templateUrl: './order-status-display.component.html',
  styleUrl: './order-status-display.component.scss',
})
export class OrderStatusDisplayComponent implements OnInit {
  orders: OrderResponse[] = [];

  get ordersInPreparation(): OrderResponse[] {
    return this.orders.filter(
      (order) => order.order_status === OrderStatus.IN_PREPARATION
    );
  }

  get ordersReady(): OrderResponse[] {
    return this.orders.filter(
      (order) => order.order_status === OrderStatus.READY
    );
  }

  constructor(
    private authenticationService: AuthenticationService,
    private ordersService: OrdersService
  ) {}

  ngOnInit(): void {
    document.body.style.paddingTop = '0';
    if (this.isManager() || this.isEmployee()) this.loadOrders();
  }

  ngOnDestroy(): void {
    document.body.style.paddingTop = '9.2rem';
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
}
