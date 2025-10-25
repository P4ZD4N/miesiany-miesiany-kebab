import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { catchError, map, Observable, throwError } from 'rxjs';
import {
  NewWorkScheduleEntryResponse,
  RemovedWorkScheduleEntryResponse,
  WorkScheduleEntryResponse,
} from '../../responses/responses';
import {
  NewWorkScheduleEntryRequest,
  RemovedWorkScheduleEntryRequest,
} from '../../requests/requests';

@Injectable({
  providedIn: 'root',
})
export class WorkScheduleService {
  private apiUrl = 'http://localhost:8080/api/v1/work-schedule';

  constructor(private http: HttpClient, private langService: LangService) {}

  getWorkScheduleEntries(): Observable<WorkScheduleEntryResponse[]> {
    return this.http.get<WorkScheduleEntryResponse[]>(
      `${this.apiUrl}/all-entries`,
      { withCredentials: true }
    );
  }

  getWorkSchedulePDF(startDate: string, endDate: string): Observable<Blob> {
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang,
    });

    const params = {
      startDate: startDate,
      endDate: endDate,
    };

    return this.http
      .get(`${this.apiUrl}/get-work-schedule-pdf`, {
        headers,
        params: params,
        responseType: 'blob',
        withCredentials: true,
      })
      .pipe(
        map((response) => response),
        catchError(this.handleError)
      );
  }

  addWorkScheduleEntry(
    request: NewWorkScheduleEntryRequest
  ): Observable<NewWorkScheduleEntryResponse> {
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang,
    });

    return this.http
      .post<NewWorkScheduleEntryResponse>(`${this.apiUrl}/add-entry`, request, {
        headers,
        withCredentials: true,
      })
      .pipe(
        map((response) => response),
        catchError(this.handleError)
      );
  }

  removeWorkScheduleEntry(
    request: RemovedWorkScheduleEntryRequest
  ): Observable<RemovedWorkScheduleEntryResponse> {
    const requestBody = { id: request.id };

    return this.http
      .delete<RemovedWorkScheduleEntryResponse>(`${this.apiUrl}/remove-entry`, {
        body: requestBody,
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
