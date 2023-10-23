/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'src/app/shared/shared.module';
import { RouterTestingModule } from "@angular/router/testing";
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA,} from "@angular/material/dialog";
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { UserSettingsComponent } from './user-settings.component';
import { UserService } from '../user.service';
import { UserLanguageApp } from '../userLanguageApp.enum';
import { UserTimeFormat } from '../userTimeFormat.enum';
import { UserDateFormat } from '../userDateFormat.enum';
import { UserConfig, UserSettings, UserSettingsDetail } from '../user';

describe('UserSettingsComponent', () => {
  let component: UserSettingsComponent;
  let userService: UserService;
  let router: Router;
  let fixture: ComponentFixture<UserSettingsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        SharedModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        MatFormFieldModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: undefined }, 
        { provide: MAT_DIALOG_DATA, 
          useValue: { 
            token: 'fake-jwt-token', 
            dialog: undefined, 
            theme: 'candidate-theme' 
          }
        }
      ],
      declarations: [ UserSettingsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserSettingsComponent);
    userService = TestBed.inject(UserService);
    router = TestBed.inject(Router)
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get and load settings', () => {
    const languageApp = component.userSettingsForm.controls['languageApp'];
    const timeFormat = component.userSettingsForm.controls['timeFormat'];
    const dateFormat = component.userSettingsForm.controls['dateFormat'];

    let userSettingsDetail = new UserSettingsDetail(
      UserLanguageApp.ES, 
      UserTimeFormat.FORMATOHORAS24,
      UserDateFormat.AAAAMMDDSLASH
    );

    let userConfig = new UserConfig(userSettingsDetail);    
    let userSettings = new UserSettings(true, userConfig);

    let userGetConfigSpy = spyOn(userService, 'getConfig').and.returnValue(of(userSettings));
    
    component.getSettings();
    fixture.detectChanges();

    expect(userGetConfigSpy).toHaveBeenCalledTimes(1);
    expect(languageApp.getRawValue()).toEqual(UserLanguageApp.ES);
    expect(timeFormat.getRawValue()).toEqual(UserTimeFormat.FORMATOHORAS24);
    expect(dateFormat.getRawValue()).toEqual(UserDateFormat.AAAAMMDDSLASH);

  });

  it('should handle exceptions when getSettings fail', fakeAsync(() => {

    expect(component.setConfigError).toBeUndefined();

    let error = {
      error: { 
        detail: "Invalid authorization code"
      }
    };

    let userGetConfigSpy = spyOn(userService, 'getConfig').and.returnValue(throwError(() => error));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();
    
    component.getSettings();
    fixture.detectChanges();

    expect(userGetConfigSpy).toHaveBeenCalledTimes(1);
    expect(component.setConfigError).not.toEqual("");
    tick(3000);
    expect(component.setConfigError).toEqual("");
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  }));

  it('should set settings', fakeAsync(() => {
    const languageApp = component.userSettingsForm.controls['languageApp'];
    const timeFormat = component.userSettingsForm.controls['timeFormat'];
    const dateFormat = component.userSettingsForm.controls['dateFormat'];

    languageApp.setValue(UserLanguageApp.EN);
    timeFormat.setValue(UserTimeFormat.FORMATOHORAS12);
    dateFormat.setValue(UserDateFormat.AAAAMMDDHYPHEN);

    expect(languageApp.valid).toBeTruthy();
    expect(timeFormat.valid).toBeTruthy();
    expect(dateFormat.valid).toBeTruthy();

    let userSettingsDetail = new UserSettingsDetail(
      languageApp.getRawValue(), 
      timeFormat.getRawValue(),
      dateFormat.getRawValue()
    );

    let userConfig = new UserConfig(userSettingsDetail);    
    let userSettings = new UserSettings(true, userConfig);

    let userSetConfigSpy = spyOn(userService, 'setConfig').and.returnValue(of(userSettings));
    
    component.setSettings(component.userSettingsForm.value);
    fixture.detectChanges();

    expect(userSetConfigSpy).toHaveBeenCalledTimes(1);
    expect(component.setConfigSucess).toBeTrue();
    tick(3000);
    expect(component.setConfigSucess).toBeFalse();

  }));

  it('should handle exceptions when setSettings fail', fakeAsync(() => {
    expect(component.setConfigError).toBeUndefined();

    let error = {
      error: { 
        detail: "Invalid authorization code"
      }
    };

    let userSetConfigSpy = spyOn(userService, 'setConfig').and.returnValue(throwError(() => error));
    
    component.setSettings(component.userSettingsForm.value);
    fixture.detectChanges();

    expect(userSetConfigSpy).toHaveBeenCalledTimes(1);
    expect(component.setConfigError).not.toEqual("");
    tick(3000);
    expect(component.setConfigError).toEqual("");

  }));

  it('should cancel settings', () => {
    const languageApp = component.userSettingsForm.controls['languageApp'];
    const timeFormat = component.userSettingsForm.controls['timeFormat'];
    const dateFormat = component.userSettingsForm.controls['dateFormat'];

    languageApp.setValue(UserLanguageApp.ES);
    timeFormat.setValue(UserTimeFormat.FORMATOHORAS24);
    dateFormat.setValue(UserDateFormat.AAAAMMDDSLASH);
    
    expect(languageApp.getRawValue()).toEqual(UserLanguageApp.ES);
    expect(timeFormat.getRawValue()).toEqual(UserTimeFormat.FORMATOHORAS24);
    expect(dateFormat.getRawValue()).toEqual(UserDateFormat.AAAAMMDDSLASH);

    const spyformReset = spyOn(component.userSettingsForm, 'reset').and.callThrough();
    component.cancel();
    expect(spyformReset).toHaveBeenCalled();
  });

  it('should handle exceptions when not token', fakeAsync(() => {
    expect(component.setConfigError).toBeUndefined();

    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();
    component.token = "";
    let validToken = component.validateToken();

    expect(validToken).toBeFalse();
    expect(component.setConfigError).not.toEqual("");
    tick(3000);
    expect(component.setConfigError).toEqual("");
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  }));

});
