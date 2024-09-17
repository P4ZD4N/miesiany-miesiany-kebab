import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';

interface BeverageResponse {
  name: string,
  capacity: number,
  price: number;
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
}
