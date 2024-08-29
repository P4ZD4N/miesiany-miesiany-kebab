import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, of, throwError } from 'rxjs';
import { LangService } from '../lang/lang.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(
    private http: HttpClient,
    private langService: LangService
  ) {}

  authenticate(email: string, password: string): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post(`${this.apiUrl}/login`, { email, password }, { headers }).pipe(
      catchError(this.handleError)
    );
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
