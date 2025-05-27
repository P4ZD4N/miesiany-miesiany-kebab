import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { NewOrderResponse, OrderResponse, RemovedOrderResponse, UpdatedOrderResponse } from '../../responses/responses';
import { catchError, map, Observable, throwError } from 'rxjs';
import { NewOrderRequest, RemovedOrderRequest, TrackOrderRequest, UpdatedOrderRequest } from '../../requests/requests';

@Injectable({
  providedIn: 'root'
})
export class OrdersService {
  
  private apiUrl = 'http://localhost:8080/api/v1/orders';

  constructor(private http: HttpClient, private langService: LangService) {}

  getOrders(): Observable<OrderResponse[]> {
    
    return this.http.get<OrderResponse[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }

  addOrder(request: NewOrderRequest): Observable<NewOrderResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewOrderResponse>(`${this.apiUrl}/add-order`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  trackOrder(request: TrackOrderRequest): Observable<OrderResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<OrderResponse>(`${this.apiUrl}/track-order`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  updateOrder(order: UpdatedOrderRequest): Observable<UpdatedOrderResponse> {
  
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.put<UpdatedOrderResponse>(`${this.apiUrl}/update-order`, order, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  removeOrder(order: RemovedOrderRequest): Observable<RemovedOrderResponse> {

  const requestBody = { id: order.id };

    return this.http.delete<RemovedOrderResponse>(`${this.apiUrl}/remove-order`, { body: requestBody, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  handleError(error: HttpErrorResponse) {
    
    let errorMessages: { [key: string]: string } = {};

    if (error.error && typeof error.error === 'object') {
      errorMessages = error.error;
    } else {
      console.error(`Backend returned code ${error.status}, body was: ${error.error}`);
    }

    return throwError(() => ({ errorMessages }));
  }
}
