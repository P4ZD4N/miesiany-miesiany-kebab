import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { Size } from '../../enums/size.enum';
import { LangService } from '../lang/lang.service';

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

  constructor(private http: HttpClient, private langService: LangService) {}

  getBeverages(): Observable<BeverageResponse[]> {

    return this.http.get<BeverageResponse[]>(`${this.apiUrl}/beverages`, { withCredentials: true });
  }

  addBeverage(beverage: any): Observable<any> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<any>(`${this.apiUrl}/add-beverage`, beverage, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  updateBeverage(beverage: any): Observable<any> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.put<any>(`${this.apiUrl}/update-beverage`, beverage, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(error => {
        console.error('Error updating beverage', error);
        return of(null);
      })
    )
  }

  removeBeverage(beverage: any): Observable<any> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    const requestBody = { name: beverage.name };

    return this.http.delete<any>(`${this.apiUrl}/remove-beverage`, { headers, body: requestBody, withCredentials: true }).pipe(
      map(response => response),
      catchError(error => {
        console.error('Error removing beverage', error);
        return of(null);
      })
    )
  }

  getAddons(): Observable<AddonResponse[]> {
    return this.http.get<AddonResponse[]>(`${this.apiUrl}/addons`, { withCredentials: true });
  }

  getMeals(): Observable<MealResponse[]> {
    return this.http.get<MealResponse[]>(`${this.apiUrl}/meals`, { withCredentials: true });
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
