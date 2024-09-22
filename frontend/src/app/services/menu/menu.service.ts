import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { Size } from '../../enums/size.enum';

interface BeverageResponse {
  name: string,
  capacity: number,
  price: number;
}

interface AddonResponse {
  name: string,
  price: number;
}

interface SimpleMealIngredient {
  id: number;
  name: string;
  ingredientType: string;
}

interface MealResponse {
  name: string;
  prices: { [key in Size]: number };
  ingredients: SimpleMealIngredient[];
}

@Injectable({
  providedIn: 'root'
})
export class MenuService {

  private apiUrl = 'http://localhost:8080/api/v1/menu';

  constructor(private http: HttpClient) {}

  getBeverages(): Observable<BeverageResponse[]> {
    return this.http.get<BeverageResponse[]>(`${this.apiUrl}/beverages`, { withCredentials: true });
  }

  getAddons(): Observable<AddonResponse[]> {
    return this.http.get<AddonResponse[]>(`${this.apiUrl}/addons`, { withCredentials: true });
  }

  getMeals(): Observable<MealResponse[]> {
    return this.http.get<MealResponse[]>(`${this.apiUrl}/meals`, { withCredentials: true });
  } 
}
