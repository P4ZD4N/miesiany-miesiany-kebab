import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { OrdersService } from '../../../services/orders/orders.service';
import { OrderResponse } from '../../../responses/responses';
import { OrderStatus } from '../../../enums/order-status.enum';
import { WebSocketService } from '../../../services/websocket/websocket.service';

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
    private ordersService: OrdersService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    document.body.style.paddingTop = '0';
    if (this.isManager() || this.isEmployee()) {
      this.loadOrders();
      this.subscribeToRealTimeUpdates();
    }
  }

  ngOnDestroy(): void {
    document.body.style.paddingTop = '9.2rem';
  }

  private subscribeToRealTimeUpdates(): void {
    this.subscribeToOrderUpdates();
    this.subscribeToOrderRemove();
  }

  private subscribeToOrderUpdates(): void {
    this.webSocketService.onOrderStatusUpdate().subscribe({
      next: (order: OrderResponse) => {
        const index = this.orders.findIndex((o) => o.id === order.id);

        if (index > -1) {
          this.orders[index] = order;
        } else {
          this.orders.push(order);
          this.orders.sort((a, b) => a.id - b.id);
        }
      },
      error: (error) => console.error('WebSocket update error', error),
    });
  }

  private subscribeToOrderRemove(): void {
    this.webSocketService.onOrderRemoved().subscribe({
      next: (removedOrderId: number) =>
        (this.orders = this.orders.filter((o) => o.id !== removedOrderId)),
      error: (error) => console.error('WebSocket removal error', error),
    });
  }

  private loadOrders(): void {
    this.ordersService.getOrders().subscribe({
      next: (data: OrderResponse[]) =>
        (this.orders = data.sort((a, b) => a.id - b.id)),
      error: (error) => console.log('Error loading orders', error),
    });
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }

  protected isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }
}
