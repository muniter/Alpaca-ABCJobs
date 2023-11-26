/* tslint:disable:no-unused-variable */
import { ComponentFixture, TestBed, fakeAsync, inject, tick, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyRegisterComponent } from './company-register.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { RouterTestingModule } from "@angular/router/testing";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { faker } from '@faker-js/faker';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CompanyService } from '../company.service';
import { environment } from 'src/environments/environment';
import { Observable, of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('CompanyRegisterComponent', () => {
  let component: CompanyRegisterComponent;
  let companyService: CompanyService;
  let router: Router;
  let fixture: ComponentFixture<CompanyRegisterComponent>;
  let debug: DebugElement;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule,
        SharedModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatCheckboxModule,
        RouterTestingModule,
        BrowserAnimationsModule],
      declarations: [CompanyRegisterComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyRegisterComponent);
    companyService = TestBed.inject(CompanyService)
    router = TestBed.inject(Router)
    component = fixture.componentInstance;
    fixture.detectChanges();
    debug = fixture.debugElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should enable button when valid', () => {
    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });

    const companyName = component.companyRegisterForm.controls['companyName'];
    const companyEmail = component.companyRegisterForm.controls['companyEmail'];
    const passwordBase = component.companyRegisterForm.controls['password'];
    const passwordConfirm = component.companyRegisterForm.controls['passwordConfirm'];
    const termsCheck = component.companyRegisterForm.controls['termsCheck'];

    expect(companyName.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    companyName.setValue(faker.lorem.word({ length: { min: 9, max: 99 } }));
    companyEmail.setValue(faker.internet.email());
    passwordBase.setValue(pass);
    passwordConfirm.setValue(pass);
    termsCheck.setValue(true);
    component.MarkTouchedAux();

    fixture.detectChanges();

    expect(companyName.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );

  });

  it('should validate companyName', () => {
    const companyName = component.companyRegisterForm.controls['companyName'];
    companyName.markAsTouched();

    expect(companyName.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    companyName.setValue('');
    fixture.detectChanges();
    expect(companyName.hasError('required')).toBeTruthy();
    expect(companyName.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyNameFormField mat-error')).nativeElement.innerHTML).toContain('El nombre de la empresa no puede ser vacío');

    companyName.setValue(faker.lorem.word({ length: { min: 1, max: 1 } }));
    fixture.detectChanges();
    expect(companyName.hasError('minlength')).toBeTruthy();
    expect(companyName.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyNameFormField mat-error')).nativeElement.innerHTML).toContain('El nombre de la empresa debe tener entre 2 y 255 caracteres');

    companyName.setValue(faker.lorem.words({ min: 101, max: 102 }));
    fixture.detectChanges();
    expect(companyName.hasError('maxlength')).toBeTruthy();
    expect(companyName.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyNameFormField mat-error')).nativeElement.innerHTML).toContain('El nombre de la empresa debe tener entre 2 y 255 caracteres');

    companyName.setValue('     ');
    fixture.detectChanges();
    expect(companyName.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(companyName.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyNameFormField mat-error')).nativeElement.innerHTML).toContain('El nombre de la empresa no puede ser vacío');

    companyName.setValue(faker.lorem.word({ length: { min: 3, max: 10 } }));
    fixture.detectChanges();
    expect(companyName.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    var errorMessage = component.getErrorMessage("nonexistent");

    expect(errorMessage).toEqual("");
  });

  it('should validate companyEmail', () => {
    const companyEmail = component.companyRegisterForm.controls['companyEmail'];
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

    companyEmail.setValue(faker.lorem.word({ length: { min: 1, max: 1 } }));
    fixture.detectChanges();
    expect(companyEmail.hasError('minlength')).toBeTruthy();
    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyEmailFormField mat-error')).nativeElement.innerHTML).toContain('La dirección de correo electrónico debe tener entre 2 y 255 caracteres');

    companyEmail.setValue(faker.lorem.words({ min: 256, max: 257 }));
    fixture.detectChanges();
    expect(companyEmail.hasError('maxlength')).toBeTruthy();
    expect(companyEmail.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#companyEmailFormField mat-error')).nativeElement.innerHTML).toContain('La dirección de correo electrónico debe tener entre 2 y 255 caracteres');

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
    const password = component.companyRegisterForm.controls['password'];
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

    password.setValue(faker.lorem.word({ length: { min: 1, max: 7 } }));
    fixture.detectChanges();
    expect(password.hasError('minlength')).toBeTruthy();
    expect(password.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#passwordFormField mat-error')).nativeElement.innerHTML).toContain('La contraseña debe tener entre 8 y 20 caracteres');

    password.setValue(faker.lorem.words({ min: 21, max: 23 }));
    fixture.detectChanges();
    expect(password.hasError('maxlength')).toBeTruthy();
    expect(password.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#passwordFormField mat-error')).nativeElement.innerHTML).toContain('La contraseña debe tener entre 8 y 20 caracteres');

    password.setValue('         ');
    fixture.detectChanges();
    expect(password.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(password.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#passwordFormField mat-error')).nativeElement.innerHTML).toContain('La contraseña ingresada es inválida');
  });

  it('should validate password confirmation', () => {
    const passwordConfirm = component.companyRegisterForm.controls['passwordConfirm'];
    passwordConfirm.markAsTouched();

    expect(passwordConfirm.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    passwordConfirm.setValue('');
    fixture.detectChanges();
    expect(passwordConfirm.hasError('required')).toBeTruthy();
    expect(passwordConfirm.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#passwordConfirmFormField mat-error')).nativeElement.innerHTML).toContain('La confirmación de contraseña es obligatoria');

    passwordConfirm.setValue(faker.lorem.word({ length: { min: 8, max: 20 } }));
    fixture.detectChanges();
    expect(passwordConfirm.hasError('confirmedValidator')).toBeTruthy();
    expect(passwordConfirm.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#passwordConfirmFormField mat-error')).nativeElement.innerHTML).toContain('Las contraseñas no coinciden');
  });

  it('should validate termsCheck', () => {
    const termsCheck = component.companyRegisterForm.controls['termsCheck'];
    termsCheck.markAsTouched();
    component.MarkTouchedAux();
    expect(termsCheck.valid).toBeFalsy();

    termsCheck.setValue(true);
    fixture.detectChanges();
    expect(termsCheck.valid).toBeTruthy();
    expect(termsCheck.hasError('required')).toBeFalsy();
    var errorMessage = component.getErrorMessage("termsCheck");

    expect(errorMessage).toEqual("");

    termsCheck.setValue(false);
    fixture.detectChanges();
    expect(termsCheck.valid).toBeFalsy();
    expect(termsCheck.hasError('required')).toBeTruthy();

    expect(debug.query(By.css('#termsCheckFormField mat-error')).nativeElement.innerHTML).toContain('Debe aceptar los términos y condiciones');
  });

  it('get empty on default error message', () => {
    var errorMessage = component.getErrorMessage("nonexistent");

    expect(errorMessage).toEqual("");
  });

  it('should navigate on service success', fakeAsync(inject([CompanyService, HttpTestingController], (companyService: CompanyService, httpMock: HttpTestingController) => {

    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });

    const companyName = component.companyRegisterForm.controls['companyName'];
    const companyEmail = component.companyRegisterForm.controls['companyEmail'];
    const passwordBase = component.companyRegisterForm.controls['password'];
    const passwordConfirm = component.companyRegisterForm.controls['passwordConfirm'];
    const termsCheck = component.companyRegisterForm.controls['termsCheck'];

    expect(companyName.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    companyName.setValue(faker.lorem.word({ length: { min: 9, max: 99 } }));
    companyEmail.setValue(faker.internet.email());
    passwordBase.setValue(pass);
    passwordConfirm.setValue(pass);
    termsCheck.setValue(true);
    component.MarkTouchedAux();

    fixture.detectChanges();

    expect(companyName.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );

    let companySignUpSpy = spyOn(companyService, 'companySignUp').and.returnValue(of({ success: true, data:{token: "abc123"} }));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    component.registerCompany();

    expect(companySignUpSpy).toHaveBeenCalledTimes(1);
    tick(3000);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  })));

  it('should set errors on service exception', waitForAsync(inject([CompanyService, HttpTestingController], (companyService: CompanyService, httpMock: HttpTestingController) => {

    let pass = faker.lorem.word({ length: { min: 8, max: 18 } });

    const companyName = component.companyRegisterForm.controls['companyName'];
    const companyEmail = component.companyRegisterForm.controls['companyEmail'];
    const passwordBase = component.companyRegisterForm.controls['password'];
    const passwordConfirm = component.companyRegisterForm.controls['passwordConfirm'];
    const termsCheck = component.companyRegisterForm.controls['termsCheck'];

    expect(companyName.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );

    companyName.setValue(faker.lorem.word({ length: { min: 9, max: 99 } }));
    companyEmail.setValue(faker.internet.email());
    passwordBase.setValue(pass);
    passwordConfirm.setValue(pass);
    termsCheck.setValue(true);
    component.MarkTouchedAux();

    fixture.detectChanges();

    expect(companyName.valid).toBeTruthy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "false"
    );

    let companySignUpSpy = spyOn(companyService, 'companySignUp').and.returnValue(throwError(() => ({
      error: {
        success: false,
        errors: {
          password: "String should have at least 8 characters",
          nombre: "String should have at least 2 characters",
          email: "String should match pattern '.*@.*'"
        }
      }
    })));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    component.registerCompany();
    fixture.detectChanges();

    expect(companySignUpSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(0);
    expect(companyName.valid).toBeFalsy();
    expect(companyEmail.valid).toBeFalsy();
    expect(passwordBase.valid).toBeFalsy();
  })));
});
