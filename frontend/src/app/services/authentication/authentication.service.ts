import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { LangService } from '../lang/lang.service';
import { Role } from '../../enums/role.enum';

interface LoginResponse {
  role: Role;
}

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

    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, { email, password }, { headers, withCredentials: true }).pipe(
      map(response => {
        localStorage.setItem('userRole', response.role);
      }),
      catchError(this.handleError)
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true }).pipe(
      tap(() => {
        localStorage.removeItem('userRole');
      }),
      catchError(this.handleError)
    );
  }

  isManager(): boolean {
    const storedRole = localStorage.getItem('userRole');
    return storedRole === Role.MANAGER;
  }

  isEmployee(): boolean {
    const storedRole = localStorage.getItem('userRole');
    return storedRole === Role.EMPLOYEE;
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
