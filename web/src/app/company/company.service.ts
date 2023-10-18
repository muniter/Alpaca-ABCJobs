import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { CompanyLoginRequest, CompanyRegisterRequest } from './company';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private backCompanyUrl = environment.baseUrl + 'empresas';
  private backUsersUrl = environment.baseUrl + 'usuarios';

  constructor(private http: HttpClient) { }

  companySignUp(company: CompanyRegisterRequest){
    return this.http.post<any>(`${this.backCompanyUrl}/crear`, company);
  }

  companyLogin(credentials: CompanyLoginRequest){
    return this.http.post<any>(`${this.backUsersUrl}/login`, credentials);
  }
}
