/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyLoginComponent } from './company-login.component';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDividerModule } from '@angular/material/divider';
import { MatInputModule } from '@angular/material/input';
import { RouterTestingModule } from "@angular/router/testing";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { faker } from '@faker-js/faker';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CompanyService } from '../company.service';

describe('CompanyLoginComponent', () => {
  let component: CompanyLoginComponent;
  let companyService: CompanyService;
  let router: Router;
  let fixture: ComponentFixture<CompanyLoginComponent>;
  let debug: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule,
        SharedModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatDividerModule,
        MatInputModule,
        RouterTestingModule,
        BrowserAnimationsModule],
      declarations: [CompanyLoginComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyLoginComponent);
    companyService = TestBed.inject(CompanyService)
    router = TestBed.inject(Router)
    component = fixture.componentInstance;
    fixture.detectChanges();
    debug = fixture.debugElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate companyEmail', () => {
    const companyEmail = component.companyLoginForm.controls['companyEmail'];
    companyEmail.markAsTouched();

    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    companyEmail.setValue('');
    fixture.detectChanges();
    expect(companyEmail.hasError('required')).toBeTruthy();
    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyEmailFormField mat-error')).nativeElement.innerHTML).toContain('La dirección de correo electrónico no puede ser vacía');

    companyEmail.setValue('     ');
    fixture.detectChanges();
    expect(companyEmail.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyEmailFormField mat-error')).nativeElement.innerHTML).toContain('La dirección de correo electrónico no puede ser vacía');

    companyEmail.setValue(faker.lorem.word({ length: { min: 9, max: 99 } }));
    fixture.detectChanges();
    expect(companyEmail.hasError('email')).toBeTruthy();
    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyEmailFormField mat-error')).nativeElement.innerHTML).toContain('La dirección de correo electrónico ingresada es inválida');
  });

  it('should validate password', () => {
    const password = component.companyLoginForm.controls['password'];
    password.markAsTouched();

    expect(password.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    password.setValue('');
    fixture.detectChanges();
    expect(password.hasError('required')).toBeTruthy();
    expect(password.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#passwordFormField mat-error')).nativeElement.innerHTML).toContain('La contraseña no puede ser vacía');

    password.setValue('         ');
    fixture.detectChanges();
    expect(password.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(password.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#passwordFormField mat-error')).nativeElement.innerHTML).toContain('La contraseña ingresada es inválida');
  });

  it('should return empty on default error message', () => {
    var errorMessage = component.getErrorMessage("nonexistent");

    expect(errorMessage).toEqual("");
  });

  it('should enable button when valid', () => {
    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });

    const companyEmail = component.companyLoginForm.controls['companyEmail'];
    const passwordBase = component.companyLoginForm.controls['password'];

    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    companyEmail.setValue(faker.internet.email());
    passwordBase.setValue(pass);

    fixture.detectChanges();

    expect(companyEmail.valid).toBeTruthy();
    expect(passwordBase.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );

  });

  it('should navigate when service returns success', () => {
    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });

    const companyEmail = component.companyLoginForm.controls['companyEmail'];
    const passwordBase = component.companyLoginForm.controls['password'];

    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    companyEmail.setValue(faker.internet.email());
    passwordBase.setValue(pass);

    fixture.detectChanges();

    expect(companyEmail.valid).toBeTruthy();
    expect(passwordBase.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );

    let companySignUpSpy = spyOn(companyService, 'companyLogin').and.returnValue(of({ success: true }));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    component.loginCompany();

    expect(companySignUpSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });

  it('should set errors on service exception', async () => {
    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });

    const companyEmail = component.companyLoginForm.controls['companyEmail'];
    companyEmail.markAsTouched();
    const passwordBase = component.companyLoginForm.controls['password'];
    passwordBase.markAsTouched();

    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    companyEmail.setValue(faker.internet.email());
    passwordBase.setValue(pass);

    fixture.detectChanges();

    expect(companyEmail.valid).toBeTruthy();
    expect(passwordBase.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );

    let companySignUpSpy = spyOn(companyService, 'companyLogin').and.returnValue(throwError(() => ({
      error: {
        success: false,
        errors: {
          email: "Email no registrado"
        }
      }
    })));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    component.loginCompany();
    fixture.detectChanges();

    expect(companySignUpSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(0);
    expect(passwordBase.valid).toBeFalsy();

    expect(debug.query(By.css('#passwordFormField mat-error')).nativeElement.innerHTML).toContain('Falló el inicio de sesión, inténtelo de nuevo.');
  });

});


