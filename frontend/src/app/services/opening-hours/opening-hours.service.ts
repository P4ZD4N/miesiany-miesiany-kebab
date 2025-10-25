import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { LangService } from '../lang/lang.service';
import { UpdatedHourRequest } from '../../requests/requests';
import { OpeningHoursResponse } from '../../responses/responses';

export interface UpdatedHourResponse {
  status_code: number;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class OpeningHoursService {
  private apiUrl = 'http://localhost:8080/api/v1/hours';

  constructor(private http: HttpClient, private langService: LangService) {}

  getOpeningHours(): Observable<OpeningHoursResponse[]> {
    return this.http.get<OpeningHoursResponse[]>(
      `${this.apiUrl}/opening-hours`,
      { withCredentials: true }
    );
  }

  updateOpeningHour(hour: UpdatedHourRequest): Observable<UpdatedHourResponse> {
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang,
    });

    return this.http
      .put<UpdatedHourResponse>(`${this.apiUrl}/update-opening-hour`, hour, {
        headers,
        withCredentials: true,
      })
      .pipe(
        map((response) => response),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessages: { [key: string]: string } = {};

    error.error && typeof error.error === 'object'
      ? (errorMessages = error.error)
      : console.error(
          `Backend returned code ${error.status}, body was: ${error.error}`
        );

    return throwError(() => ({ errorMessages }));
  }
}
