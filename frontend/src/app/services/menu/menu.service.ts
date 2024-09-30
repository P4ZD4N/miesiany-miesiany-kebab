import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { Size } from '../../enums/size.enum';
import { LangService } from '../lang/lang.service';
import { NewBeverageRequest, UpdatedBeverageRequest, RemovedBeverageRequest, NewAddonRequest } from '../../requests/requests';
import { NewBeverageResponse, UpdatedBeverageResponse, RemovedBeverageResponse, BeverageResponse, AddonResponse, MealResponse, NewAddonResponse } from '../../responses/responses';


@Injectable({
  providedIn: 'root'
})
export class MenuService {

  private apiUrl = 'http://localhost:8080/api/v1/menu';

  constructor(private http: HttpClient, private langService: LangService) {}

  getBeverages(): Observable<BeverageResponse[]> {

    return this.http.get<BeverageResponse[]>(`${this.apiUrl}/beverages`, { withCredentials: true });
  }

  addBeverage(beverage: NewBeverageRequest): Observable<NewBeverageResponse> {

console.log(beverage);

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewBeverageResponse>(`${this.apiUrl}/add-beverage`, beverage, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  updateBeverage(beverage: UpdatedBeverageRequest): Observable<UpdatedBeverageResponse> {
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.put<UpdatedBeverageResponse>(`${this.apiUrl}/update-beverage`, beverage, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  removeBeverage(beverage: RemovedBeverageRequest): Observable<RemovedBeverageResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    const requestBody = { name: beverage.name, capacity: beverage.capacity };

    return this.http.delete<RemovedBeverageResponse>(`${this.apiUrl}/remove-beverage`, { headers, body: requestBody, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  getAddons(): Observable<AddonResponse[]> {
    return this.http.get<AddonResponse[]>(`${this.apiUrl}/addons`, { withCredentials: true });
  }

  addAddon(addon: NewAddonRequest): Observable<NewAddonResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewAddonResponse>(`${this.apiUrl}/add-addon`, addon, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
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
