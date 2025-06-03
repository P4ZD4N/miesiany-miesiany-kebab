import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { catchError, map, Observable, throwError } from 'rxjs';
import { NewWorkScheduleEntryResponse, WorkScheduleEntryResponse } from '../../responses/responses';
import { NewWorkScheduleEntryRequest } from '../../requests/requests';

@Injectable({
  providedIn: 'root'
})
export class WorkScheduleService {

  private apiUrl = 'http://localhost:8080/api/v1/work-schedule';

  constructor(private http: HttpClient, private langService: LangService) {}
  
  getWorkScheduleEntries(): Observable<WorkScheduleEntryResponse[]> {
    
    return this.http.get<WorkScheduleEntryResponse[]>(`${this.apiUrl}/all-entries`, { withCredentials: true });
  }

  addWorkScheduleEntry(request: NewWorkScheduleEntryRequest): Observable<NewWorkScheduleEntryResponse> {
  
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewWorkScheduleEntryResponse>(`${this.apiUrl}/add-entry`, request, { headers, withCredentials: true }).pipe(
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
