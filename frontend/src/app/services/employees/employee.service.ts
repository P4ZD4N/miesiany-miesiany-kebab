import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { EmployeeResponse } from '../../responses/responses';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private apiUrl = 'http://localhost:8080/api/v1/employees';
  
  constructor(private http: HttpClient, private langService: LangService) {}
  
  getEmployees(): Observable<EmployeeResponse[]> {
    
    return this.http.get<EmployeeResponse[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }
}
