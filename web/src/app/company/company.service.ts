import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { CompanyRegisterForm } from './company';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private backCompanyUrl = environment.baseUrl + 'empresas';

  constructor(private http: HttpClient) { }

  companySignUp(company: CompanyRegisterForm){
    return this.http.post<any>(`${this.backCompanyUrl}/crear`, company);
  }

}
