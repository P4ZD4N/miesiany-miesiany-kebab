import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';
import { LangService } from '../lang/lang.service';

@Injectable({
  providedIn: 'root'
})
export class OpeningHoursService {
  private apiUrl = 'http://localhost:8080/api/v1/hours';

  constructor(private http: HttpClient, private langService: LangService) {}

  getOpeningHours(): Observable<any> {

    return this.http.get(`${this.apiUrl}/opening-hours`, { withCredentials: true });
  }

  updateOpeningHour(hour: any): Observable<any> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.put<any>(`${this.apiUrl}/update-opening-hour`, hour, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(error => {
        console.error('Error updating opening hour', error);
        return of(null);
      })
    )
  }
}
