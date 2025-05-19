import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { OrdersService } from '../../../services/orders/orders.service';
import { OrderResponse } from '../../../responses/responses';
import { OrderStatus } from '../../../enums/order-status.enum';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-order-status-display',
  standalone: true,
  imports: [CommonModule, TranslateModule, RouterModule],
  templateUrl: './order-status-display.component.html',
  styleUrl: './order-status-display.component.scss'
})
export class OrderStatusDisplayComponent implements OnInit {

  orders: OrderResponse[] = [];

  constructor(
      private authenticationService: AuthenticationService,
      private ordersService: OrdersService
  ) {}

  ngOnInit() {
    document.body.style.paddingTop = '0';
    
    if (this.isManager() || this.isEmployee()) {
      this.loadOrders();
    }
  }

  ngOnDestroy() {
    document.body.style.paddingTop = '9.2rem';
  }

  loadOrders(): void {
    this.ordersService.getOrders().subscribe(
      (data: OrderResponse[]) => {
        this.orders = data.sort((a, b) => a.id - b.id);
      },
      (error) => {
        console.log('Error loading orders', error);
      }
    );
  }

  get ordersInPreparation(): OrderResponse[] {
    return this.orders.filter(order => order.order_status === OrderStatus.IN_PREPARATION);
  }

  get ordersReady(): OrderResponse[] {
    console.log(this.orders);
    return this.orders.filter(order => order.order_status === OrderStatus.READY);
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }
}
