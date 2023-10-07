import { Injectable } from '@angular/core';
import { ConfigurationService } from './configuration.service';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GestionEvaluacionesService {

  private baseUrl: string

  constructor(config: ConfigurationService, private http: HttpClient) {
    this.baseUrl = config.apiURL + '/evaluaciones'
  }

  public ping() {
    // Send a request to the API
    return this.http.get<string>(this.baseUrl + '/ping')
  }
}
