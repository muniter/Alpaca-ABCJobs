import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { UserConfig, UserSettings, UserSettingsDetail } from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private backUsersUrl = environment.baseUrl + 'usuarios';

  constructor(private http: HttpClient) { }

  getConfig(token: string): Observable<UserSettings> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
    const url = `${this.backUsersUrl}/config`;
    return this.http.get<UserSettings>(url, {
      headers: headers
    });
  }

  setConfig(token: string, userSettings: UserSettingsDetail): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    let userConfig = new UserConfig(
      userSettings
    );
    const url = `${this.backUsersUrl}/config`;
    return this.http.post<any>(url, userConfig, {
      headers: headers
    });
  }

}
