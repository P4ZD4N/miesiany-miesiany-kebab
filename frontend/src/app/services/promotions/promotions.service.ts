import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { AddonPromotionResponse, BeveragePromotionResponse, MealPromotionResponse, NewMealPromotionResponse, RemovedMealPromotionResponse, UpdatedMealPromotionResponse } from '../../responses/responses';
import { catchError, map, Observable, throwError } from 'rxjs';
import { NewMealPromotionRequest, RemovedMealPromotionRequest, UpdatedMealPromotionRequest } from '../../requests/requests';

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
