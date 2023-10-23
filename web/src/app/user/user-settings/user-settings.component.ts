import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup,  } from '@angular/forms';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserService } from '../user.service';
import { UserSettings, UserSettingsDetail } from '../user';
import { UserLanguageApp } from '../userLanguageApp.enum';
import { UserTimeFormat } from '../userTimeFormat.enum';
import { UserDateFormat } from '../userDateFormat.enum';
import { AppRoutesEnum } from 'src/app/core/enums';

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.css']
})
export class UserSettingsComponent implements OnInit {

  userSettingsForm!: FormGroup;
  userSettings!: UserSettings;
  token: string;
  dialog!: MatDialog;
  theme : string;
  setConfigSucess: boolean = false;
  setConfigError!: string;

  languagesApp: Array<UserLanguageApp> = [
    UserLanguageApp.ES, 
    UserLanguageApp.EN
  ];
  timeFormats: Array<UserTimeFormat> = [
    UserTimeFormat.FORMATOHORAS12, 
    UserTimeFormat.FORMATOHORAS24
  ];
  dateFormats: Array<UserDateFormat> = [
    UserDateFormat.AAAAMMDDSLASH, 
    UserDateFormat.AAAAMMDDHYPHEN,
    UserDateFormat.DDMMAAAASLASH,
    UserDateFormat.DDMMAAAAHYPHEN
  ];

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    @Inject(MAT_DIALOG_DATA) public data: {token: string, dialog: MatDialog, theme: string}
  ) { 
    this.token = data.token;
    this.dialog = data.dialog;
    this.theme = data.theme;
  }

  ngOnInit() {

    if(this.validateToken()) {
      this.getSettings();
      this.userSettingsForm = this.formBuilder.group({
        languageApp: [""],
        timeFormat: [""],
        dateFormat: [""]
      });
    }
    
  }

  validateToken(): boolean {
    if(!this.token || this.token == "") {
      this.setConfigError = 
        $localize`:@@errortoken:Token invalido, inicie sesion nuevamente`
      setTimeout(() => {
        this.setConfigError = "";
        this.dialog?.closeAll();
        this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
      }, 3000);
      return false;
    }
    return true;
  }

  getSettings(): void {
    this.userService
      .getConfig(this.token)
      .subscribe({
        next: (userSettings) => {
          this.userSettings = userSettings;
          this.userSettingsForm.patchValue(this.userSettings.data.config);
        },
        error: (exception) => {
          this.setConfigError = 
            $localize`:@@responsemessageerrorgetuserconfig:Token invalido, inicie sesión nuevamente: 
                      ${exception.error.detail}`
          setTimeout(() => {
            this.setConfigError = "";
            this.dialog?.closeAll();
            this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
          }, 3000);
        }
      });
  }

  setSettings(userSettings: UserSettingsDetail) {
    this.userService
      .setConfig(this.token, userSettings)
      .subscribe({
        error: (exception) => { 
          this.setConfigError = 
            $localize`:@@responsemessageerrorsetuserconfig:Error al guardar la configuración: 
                      ${exception.error.detail}`
          setTimeout(() => {
            this.setConfigError = "";
          }, 3000);
        },
        complete: () => { 
          this.setConfigSucess = true;
          setTimeout(() => {
            this.setConfigSucess = false;
          }, 3000);
        }
      })
  }

  cancel() {
    this.userSettingsForm.reset();    
    this.dialog?.closeAll();
  }

}
