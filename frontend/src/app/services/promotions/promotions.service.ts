import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { AddonPromotionResponse, BeveragePromotionResponse, MealPromotionResponse, NewAddonPromotionResponse, NewBeveragePromotionResponse, NewMealPromotionResponse, RemovedBeveragePromotionResponse, RemovedMealPromotionResponse, UpdatedBeveragePromotionResponse, UpdatedMealPromotionResponse } from '../../responses/responses';
import { catchError, map, Observable, throwError } from 'rxjs';
import { NewAddonPromotionRequest, NewBeveragePromotionRequest, NewMealPromotionRequest, RemovedBeveragePromotionRequest, RemovedMealPromotionRequest, UpdatedBeveragePromotionRequest, UpdatedMealPromotionRequest } from '../../requests/requests';

@Injectable({
  providedIn: 'root'
})
export class PromotionsService {
  private apiUrl = 'http://localhost:8080/api/v1/promotions';

  constructor(private http: HttpClient, private langService: LangService) {}

  getMealPromotions(): Observable<MealPromotionResponse[]> {
  
    return this.http.get<MealPromotionResponse[]>(`${this.apiUrl}/meal-promotions`, { withCredentials: true });
  }

  getBeveragePromotions(): Observable<BeveragePromotionResponse[]> {
  
    return this.http.get<BeveragePromotionResponse[]>(`${this.apiUrl}/beverage-promotions`, { withCredentials: true });
  }

  getAddonPromotions(): Observable<AddonPromotionResponse[]> {
  
    return this.http.get<AddonPromotionResponse[]>(`${this.apiUrl}/addon-promotions`, { withCredentials: true });
  }

  addMealPromotion(request: NewMealPromotionRequest): Observable<NewMealPromotionResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewMealPromotionResponse>(`${this.apiUrl}/add-meal-promotion`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  updateMealPromotion(request: UpdatedMealPromotionRequest): Observable<UpdatedMealPromotionResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
  
    return this.http.put<UpdatedMealPromotionResponse>(`${this.apiUrl}/update-meal-promotion`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  removeMealPromotion(request: RemovedMealPromotionRequest): Observable<RemovedMealPromotionResponse> {
  
    const requestBody = { id: request.id };

    return this.http.delete<RemovedMealPromotionResponse>(`${this.apiUrl}/remove-meal-promotion`, { body: requestBody, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  addBeveragePromotion(request: NewBeveragePromotionRequest): Observable<NewBeveragePromotionResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewBeveragePromotionResponse>(`${this.apiUrl}/add-beverage-promotion`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  updateBeveragePromotion(request: UpdatedBeveragePromotionRequest): Observable<UpdatedBeveragePromotionResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
  
    return this.http.put<UpdatedBeveragePromotionResponse>(`${this.apiUrl}/update-beverage-promotion`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  removeBeveragePromotion(request: RemovedBeveragePromotionRequest): Observable<RemovedBeveragePromotionResponse> {
  
    const requestBody = { id: request.id };

    return this.http.delete<RemovedBeveragePromotionResponse>(`${this.apiUrl}/remove-beverage-promotion`, { body: requestBody, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  addAddonPromotion(request: NewAddonPromotionRequest): Observable<NewAddonPromotionResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewAddonPromotionResponse>(`${this.apiUrl}/add-addon-promotion`, request, { headers, withCredentials: true }).pipe(
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
