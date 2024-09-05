import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OpeningHoursService {
  private apiUrl = 'http://localhost:8080/api/v1/hours';

  constructor(private http: HttpClient) {}

  getOpeningHours(): Observable<any> {

    return this.http.get(`${this.apiUrl}/opening-hours`);
  }
}
