import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { AddonPromotionResponse, BeveragePromotionResponse, MealPromotionResponse } from '../../responses/responses';
import { Observable } from 'rxjs';

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
}
