import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { EmployeeResponse, NewEmployeeResponse, RemovedEmployeeResponse, UpdatedEmployeeResponse } from '../../responses/responses';
import { catchError, map, Observable, throwError } from 'rxjs';
import { NewEmployeeRequest, RemovedEmployeeRequest, UpdatedEmployeeRequest } from '../../requests/requests';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private apiUrl = 'http://localhost:8080/api/v1/employees';
  
  constructor(private http: HttpClient, private langService: LangService) {}
  
  getEmployees(): Observable<EmployeeResponse[]> {
    
    return this.http.get<EmployeeResponse[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }

  addEmployee(newEmployee: NewEmployeeRequest): Observable<NewEmployeeResponse> {
  
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewEmployeeResponse>(`${this.apiUrl}/add-employee`, newEmployee, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  updateEmployee(updatedEmployee: UpdatedEmployeeRequest): Observable<UpdatedEmployeeResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.put<NewEmployeeResponse>(`${this.apiUrl}/update-employee`, updatedEmployee, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  removeEmployee(request: RemovedEmployeeRequest): Observable<RemovedEmployeeResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    const requestBody = { email: request.email };

    return this.http.delete<RemovedEmployeeResponse>(`${this.apiUrl}/remove-employee`, { body: requestBody, headers, withCredentials: true }).pipe(
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
