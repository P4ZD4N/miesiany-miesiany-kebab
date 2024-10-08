import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { LangService } from '../lang/lang.service';
import { Role } from '../../enums/role.enum';
import { AuthenticationRequest } from '../../requests/requests';
import { AuthenticationResponse } from '../../responses/responses';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  private userRole: Role | null = null; 

  constructor(
    private http: HttpClient,
    private langService: LangService
  ) {
    const storedRole = sessionStorage.getItem('userRole');
    if (storedRole) {
      this.userRole = storedRole as Role;
    }
  }

  authenticate(request: AuthenticationRequest): Observable<AuthenticationResponse> {
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<AuthenticationResponse>(`${this.apiUrl}/login`, request, { headers, withCredentials: true }).pipe(
      map(response => {
        this.userRole = response.role;
        sessionStorage.setItem('userRole', response.role);
        return response;
      }),
      catchError(this.handleError)
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {}, { withCredentials: true }).pipe(
      tap(() => {
        this.userRole = null;
        sessionStorage.removeItem('userRole');
      }),
      catchError(this.handleError)
    );
  }

  isManager(): boolean {
    return this.userRole === Role.MANAGER;
  }

  isEmployee(): boolean {
    return this.userRole === Role.EMPLOYEE;
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
