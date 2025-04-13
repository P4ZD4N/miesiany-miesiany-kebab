import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { TrackOrderRequest } from '../../../requests/requests';
import { OrderService } from '../../../services/order/order.service';
import { OrdersService } from '../../../services/orders/orders.service';
import { OrderResponse } from '../../../responses/responses';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-track-order',
  standalone: true,
  imports: [TranslateModule, CommonModule, FormsModule],
  templateUrl: './track-order.component.html',
  styleUrl: './track-order.component.scss'
})
export class TrackOrderComponent implements OnInit{

  storageKey = 'trackOrderData';

  errorMessages: { [key: string]: string } = {};
  orderId: number | null = null;
  customerPhone: string | null = null;
  trackOrderRequest: TrackOrderRequest = {
    id: null,
    customer_phone: ''
  };
  order: OrderResponse = {
    order_type: null, 
    order_status: null,
    customer_phone: '',
    customer_email: '',
    street: '',
    house_number: 0,
    postal_code: '',
    city: '',
    additional_comments: '',
    meals: {},
    beverages: {},
    addons: {}
  };
  languageChangeSubscription: Subscription;

  constructor(
    private orderService: OrderService, 
    private ordersService: OrdersService,
    private langService: LangService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.errorMessages = {};
    });
  }

  ngOnInit(): void {
    const saved = this.getTrackOrderData();
    const fromService = this.orderService.trackOrderData;
  
    const id = saved?.id ?? fromService.orderId;
    const phone = saved?.customer_phone ?? fromService.customerPhone;
  
    if (id && phone) {
      this.trackOrderRequest = { id, customer_phone: phone };
      this.orderId = id;
      this.customerPhone = phone;
      this.setTrackOrderData(this.trackOrderRequest);
      this.startTrackingOrder();
    }
  }

  startTrackingOrder(): void {
    this.ordersService.trackOrder(this.trackOrderRequest).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' 
            ? `Pomyslnie rozpoczeto sledzenie zamowienia z id '${this.trackOrderRequest.id}'!` 
            : `Successfully started tracking order with id '${this.trackOrderRequest.id}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.orderId = this.trackOrderRequest.id;
        this.customerPhone = this.trackOrderRequest.customer_phone;
        this.initOrder(response);
        this.setTrackOrderData(this.trackOrderRequest);
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  initOrder(response: OrderResponse): void {
    this.order = {
      order_type: response.order_type,
      order_status: response.order_status,
      customer_phone: response.customer_phone,
      customer_email: response.customer_email,
      street: response.street,
      house_number: response.house_number,
      postal_code: response.postal_code,
      city: response.city,
      additional_comments: response.additional_comments,
      meals: response.meals,
      beverages: response.beverages,
      addons: response.addons
    }
  }

  setTrackOrderData(request: TrackOrderRequest): void {
    sessionStorage.setItem(this.storageKey, JSON.stringify(request));
  }

  getTrackOrderData(): TrackOrderRequest | null {
    const data = sessionStorage.getItem(this.storageKey);
    return data ? JSON.parse(data) : null;
  }

  clearTrackOrderData(): void {
    sessionStorage.removeItem(this.storageKey);
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
      console.log(this.errorMessages);
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }
}
