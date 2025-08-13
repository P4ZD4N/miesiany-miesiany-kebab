import { Component, OnInit } from '@angular/core';
import { LangService } from '../../../services/lang/lang.service';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { OrdersService } from '../../../services/orders/orders.service';
import { OrderResponse } from '../../../responses/responses';
import { OrderStatus } from '../../../enums/order-status.enum';
import Swal from 'sweetalert2';
import { RemovedOrderRequest, UpdatedOrderRequest } from '../../../requests/requests';
import { OrderService } from '../../../services/order/order.service';

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './order-management.component.html',
  styleUrl: './order-management.component.scss'
})
export class OrderManagementComponent implements OnInit{

  OrderStatus = OrderStatus; 
  orders: OrderResponse[] = [];
  isTodayFilterActive: boolean = true;
  statusFilter: OrderStatus | null = null;

  constructor(
    private langService: LangService,
    private authenticationService: AuthenticationService,
    private orderService: OrderService,
    private ordersService: OrdersService,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    if (this.isManager() || this.isEmployee()) {
      this.loadOrders();
    }
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

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  setFilter(status: OrderStatus | null, isTodayFilterActive: boolean): void {
    this.statusFilter = status;
    this.isTodayFilterActive = isTodayFilterActive;
  }

  getButtonClass(status: OrderStatus | null, isTodayFilterActive: boolean): string {
    const isActive = this.statusFilter === status && this.isTodayFilterActive === isTodayFilterActive;
    return isActive ? 'btn-success' : 'btn-dark';
  }
  
  get filteredOrders(): OrderResponse[] {
    return this.orders.filter(order => {
      const matchesStatus = this.statusFilter ? order.order_status === this.statusFilter : true;
      const isToday = this.isTodayFilterActive;
  
      const orderDate = new Date(order.created_at);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
  
      const isOrderToday = orderDate >= today;
      const matchesDate = isToday ? isOrderToday : !isOrderToday;
  
      return matchesStatus && matchesDate;
    });
  }

  getTranslatedMealName(mealName: string): string {
    let mealNameTranslated = this.translate.instant('menu.meals.' + mealName);

    if (mealNameTranslated === 'menu.meals.' + mealName) {
      mealNameTranslated = mealName;
    }
    
    return mealNameTranslated;
  }

  getTranslatedBeverageName(beverageName: string): string {
    let beverageNameTranslated = this.translate.instant('menu.beverages.' + beverageName);

    if (beverageNameTranslated === 'menu.addons.' + beverageName) {
      beverageNameTranslated = beverageName;
    }
    
    return beverageNameTranslated;
  }

  getTranslatedAddonName(addonName: string): string {
    let addonNameTranslated = this.translate.instant('menu.addons.' + addonName);

    if (addonNameTranslated === 'menu.addons.' + addonName) {
      addonNameTranslated = addonName;
    }
    
    return addonNameTranslated;
  }

  getTranslatedIngredientName(ingredientName: string): string {
    let ingredientNameTranslated = this.translate.instant('menu.meals.ingredients.' + ingredientName);

    if (ingredientNameTranslated === 'menu.meals.ingredients.' + ingredientName) {
      ingredientNameTranslated = ingredientName;
    }
    
    return ingredientNameTranslated;
  }

  getTranslatedOrderStatus(orderStatus: OrderStatus): string {
    let orderStatusTranslated = this.translate.instant('order-management.order_status.' + orderStatus);

    if (orderStatusTranslated === 'order-management.order_status.' + orderStatus) {
      orderStatusTranslated = orderStatus;
    }
    
    return orderStatusTranslated;
  }

  addOrder(): void {
    this.orderService.selectNextOrderItem();
  }

  updateOrder(order: OrderResponse): void {
    const hasAddress = order.street && order.house_number && order.postal_code && order.city;

    const confirmButtonText = this.langService.currentLang === 'pl' ? 'Zapisz' : 'Save';
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel'

    const streetPlaceholder = this.langService.currentLang === 'pl' ? 'Ulica' : 'Street';
    const houseNumberPlaceholder = this.langService.currentLang === 'pl' ? 'Numer domu' : 'House number';
    const postalCodePlaceholder = this.langService.currentLang === 'pl' ? 'Kod pocztowy' : 'Postal code';
    const cityPlaceholder = this.langService.currentLang === 'pl' ? 'Miasto' : 'City';
    const additionalCommentsPlaceholder = this.langService.currentLang === 'pl' ? 'Dodatkowe komentarze' : 'Additional comments';

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 
        `<span style="color: red;">Aktualizuj zamowienie</span>` : 
        `<span style="color: red;">Update order</span>`,
      background: '#141414',
      color: 'white',
      html: `
        <style>
          .form-group {
            display: flex;
            flex-direction: column;
            align-items: center;
            width: 100%;
            max-width: 400px;
            margin: 10px auto;
          }

          .form-group label {
            color: white;
            margin-bottom: 5px;
            text-align:center;
          }

          input[type="text"], input[type="number"], textarea {
            color: white;
            text-align: center;
            background-color: inherit;
            width: 100%; 
            max-width: 400px; 
            padding: 10px; 
            margin: 10px 0; 
            border: 1px solid #ccc; 
            border-radius: 5px;
            transition: border 0.3s ease;
            outline: none;
          }

          input[type="text"]:focus, input[type="number"]:focus, textarea:focus {
              border: 1px solid red; 
          }

          select {
              width: 100%;
              text-align: center;
              color: white;
              border: none;
              border-bottom: 1px solid white;
              outline: none;
              background: inherit;
              transition: border-bottom 0.3s ease;
              cursor: pointer;

              &:focus {
                  border-bottom: 1px solid red;
              }
          }

          option {
              text-align: center;
              background: black;
          }
        </style>

        <div class="form-group">
          <label>Status</label>
          <select id="status">
            ${Object.values(OrderStatus).map(status =>
              `<option value="${status}" ${order.order_status === status ? 'selected' : ''}>${this.getTranslatedOrderStatus(status)}</option>`
            ).join('')}
          </select>
        </div>

        ${hasAddress ? `
          <div class="form-group">
            <label>${streetPlaceholder}</label>
            <input type="text" id="street" maxlength="25" value="${order.street || ''}" placeholder="${streetPlaceholder}">
          </div>

          <div class="form-group">
            <label>${houseNumberPlaceholder}</label>
            <input type="number" id="house_number" value="${order.house_number || ''}" placeholder="${houseNumberPlaceholder}">
          </div>

          <div class="form-group">
            <label>${postalCodePlaceholder}</label>
            <input type="text" id="postal_code" maxlength="6" value="${order.postal_code || ''}" placeholder="${postalCodePlaceholder}">
          </div>

          <div class="form-group">
            <label>${cityPlaceholder}</label>
            <input type="text" id="city" maxlength="25" value="${order.city || ''}" placeholder="${cityPlaceholder}">
          </div>
        ` : ''}

        <div class="form-group">
          <label>${additionalCommentsPlaceholder}</label>
          <textarea id="comments">${order.additional_comments || ''}</textarea>
        </div>
        
      `,
      focusConfirm: false,
      showCancelButton: true,
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      confirmButtonText: confirmButtonText,
      confirmButtonColor: '#198754',
      customClass: {
        validationMessage: 'custom-validation-message'
      },
       preConfirm: () => {

        const street = (document.getElementById('street') as HTMLInputElement)?.value;
        const houseNumber = Number((document.getElementById('house_number') as HTMLInputElement)?.value) ;
        const postalCode = (document.getElementById('postal_code') as HTMLInputElement)?.value;
        const city = (document.getElementById('city') as HTMLInputElement)?.value;

        if (hasAddress){
          if (!street || !houseNumber || !postalCode || !city) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl' 
                ? 'Wszystkie pola sa wymagane' 
                : 'All fields are required'
            );
            return null;
          }

          if (street.length > 25) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl' ? 'Niepoprawna ulica!' : 'Invalid street!'
            );
            return null;
          }

          if (Number(houseNumber) < 1) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl' ? 'Niepoprawny numer domu!' : 'Invalid house number!'
            );
            return null;
          }

          const postalCodeRegex = /^[0-9]{2}-[0-9]{3}$/;
          if (!postalCodeRegex.test(postalCode)) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl' ? 'Niepoprawny kod pocztowy!' : 'Invalid postal code!'
            );
            return null;
          }

          if (city.length > 25) {
            Swal.showValidationMessage(
              this.langService.currentLang === 'pl' ? 'Niepoprawne miasto!' : 'Invalid city!'
            );
            return null;
          }
        }

        const updatedOrder: UpdatedOrderRequest = {
          id: order.id,
          updated_order_status: (document.getElementById('status') as HTMLSelectElement)?.value as OrderStatus,
          updated_street: street || null,
          updated_house_number: Number(houseNumber) || null,
          updated_postal_code: postalCode || null,
          updated_city: city || null,
          updated_additional_comments: (document.getElementById('comments') as HTMLTextAreaElement)?.value || null
        };

        return updatedOrder;
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.ordersService.updateOrder(result.value as UpdatedOrderRequest).subscribe(
          () => {
            Swal.fire({
              text: this.langService.currentLang === 'pl' ? `Pomyslnie zaktualizowano zamowienie z id '${order.id}'!` : `Successfully updated order with id '${order.id}'!`,
              icon: 'success',
              iconColor: 'green',
              confirmButtonColor: 'green',
              background: '#141414',
              color: 'white',
              confirmButtonText: 'Ok',
            });
            this.loadOrders();
          },
          (err) => {
            Swal.fire({
              text: this.langService.currentLang === 'pl' ? `Nie udalo sie zaktualizowac zamowienia z id '${order.id}'!` : `Can't update order with id '${order.id}'!`,
              icon: 'error',
              iconColor: 'red',
              confirmButtonColor: 'red',
              background: '#141414',
              color: 'white',
              confirmButtonText: 'Ok',
            });
          }
        );
      }
    });
  }

  removeOrder(order: OrderResponse): void {

      const confirmationMessage =
        this.langService.currentLang === 'pl'
          ? `Czy na pewno chcesz usunac zamowienie z id '${order.id}'?`
          : `Are you sure you want to remove order with id '${order.id}'?`;

      Swal.fire({
        title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
        text: confirmationMessage,
        icon: 'warning',
        iconColor: 'red',
        showCancelButton: true,
        confirmButtonColor: '#0077ff',
        cancelButtonColor: 'red',
        background: '#141414',
        color: 'white',
        confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
        cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
      }).then((result) => {
        if (result.isConfirmed) {
          this.ordersService.removeOrder({ id: order.id } as RemovedOrderRequest).subscribe(() => {
            Swal.fire({
              text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto zamowienie z id '${order.id}'!` : `Successfully removed order with id '${order.id}'!`,
              icon: 'success',
              iconColor: 'green',
              confirmButtonColor: 'green',
              background: '#141414',
              color: 'white',
              confirmButtonText: 'Ok',
            });
            this.loadOrders();
          });
        }
      });
    }
}
