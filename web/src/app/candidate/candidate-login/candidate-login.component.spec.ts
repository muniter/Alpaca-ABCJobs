/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { RouterTestingModule } from "@angular/router/testing";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { faker } from '@faker-js/faker';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { CandidateLoginComponent } from './candidate-login.component';
import { CandidateService } from '../candidate.service';

describe('CandidateLoginComponent', () => {
  let component: CandidateLoginComponent;
  let candidateService: CandidateService;
  let router: Router;
  let fixture: ComponentFixture<CandidateLoginComponent>;
  let debug: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        SharedModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatCheckboxModule,
        MatDividerModule,
        RouterTestingModule,
        BrowserAnimationsModule
      ],
      declarations: [ CandidateLoginComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateLoginComponent);
    candidateService = TestBed.inject(CandidateService)
    router = TestBed.inject(Router)
    component = fixture.componentInstance;
    fixture.detectChanges();
    debug = fixture.debugElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate companyEmail', () => {
    const email = component.candidateLoginForm.controls['email'];
    email.markAsTouched();

    expect(email.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    email.setValue('');
    fixture.detectChanges();
    expect(email.hasError('required')).toBeTruthy();
    expect(email.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateEmailFormField mat-error')).nativeElement.innerHTML).toContain('La dirección de correo electrónico no puede ser vacía');

    email.setValue('     ');
    fixture.detectChanges();
    expect(email.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(email.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateEmailFormField mat-error')).nativeElement.innerHTML).toContain('La dirección de correo electrónico no puede ser vacía');

    email.setValue(faker.lorem.word({ length: { min: 9, max: 99 } }));
    fixture.detectChanges();
    expect(email.hasError('email')).toBeTruthy();
    expect(email.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateEmailFormField mat-error')).nativeElement.innerHTML).toContain('La dirección de correo electrónico ingresada es inválida');
  });

  it('should validate password', () => {
    const password = component.candidateLoginForm.controls['password'];
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

    const email = component.candidateLoginForm.controls['email'];
    const password = component.candidateLoginForm.controls['password'];

    expect(email.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    email.setValue(faker.internet.email());
    password.setValue(pass);

    fixture.detectChanges();

    expect(email.valid).toBeTruthy();
    expect(password.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );
  });

  it('should navigate when service returns success', () => {
    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });

    const email = component.candidateLoginForm.controls['email'];
    const password = component.candidateLoginForm.controls['password'];

    expect(email.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    email.setValue(faker.internet.email());
    password.setValue(pass);

    fixture.detectChanges();

    expect(email.valid).toBeTruthy();
    expect(password.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );

    let candidateSignUpSpy = spyOn(candidateService, 'login').and.returnValue(of({ success: true, data: {token: "asd"} }));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    component.candidateLogin(component.candidateLoginForm.value);

    expect(candidateSignUpSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });

  it('should set errors on service exception', async () => {
    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });

    const email = component.candidateLoginForm.controls['email'];
    email.markAsTouched();
    const password = component.candidateLoginForm.controls['password'];
    password.markAsTouched();

    expect(email.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    email.setValue(faker.internet.email());
    password.setValue(pass);

    fixture.detectChanges();

    expect(email.valid).toBeTruthy();
    expect(password.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );

    let candidateSignUpSpy = spyOn(candidateService, 'login').and.returnValue(throwError(() => ({
      error: {
        success: false,
        errors: {
          email: "Dirección de correo o contraseña incorrectos"
        }
      }
    })));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    component.candidateLogin(component.candidateLoginForm.value);
    fixture.detectChanges();

    expect(candidateSignUpSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(0);
    expect(email.valid).toBeFalsy();

    expect(debug.query(By.css('#candidateEmailFormField mat-error')).nativeElement.innerHTML).toContain('inicio de sesión fallido');
  });
});
