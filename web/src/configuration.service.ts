import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {

  public apiURL: string

  constructor() {
    if (window.location.hostname === 'localhost') {
      this.apiURL = 'http://localhost:4000';
    } else {
      this.apiURL = 'https://api.abc.muniter.link';
    }
  }
}
