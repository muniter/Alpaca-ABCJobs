/* tslint:disable:no-unused-variable */
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { RouterTestingModule } from "@angular/router/testing";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { faker } from '@faker-js/faker';

import { CandidateRegisterComponent } from './candidate-register.component';
import { CandidateService } from '../candidate.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CandidateFormRegister } from '../candidate';

describe('CandidateRegisterComponent', () => {
  let component: CandidateRegisterComponent;
  let candidateService: CandidateService;
  let router: Router;
  let fixture: ComponentFixture<CandidateRegisterComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        SharedModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatCheckboxModule,
        RouterTestingModule,
        BrowserAnimationsModule
      ],
      declarations: [CandidateRegisterComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateRegisterComponent);
    candidateService = TestBed.inject(CandidateService)
    router = TestBed.inject(Router)
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it("should register candidate", () => {
    let candidateSignUpSpy = spyOn(candidateService, 'userSignUp').and.returnValue(of({ success: true, data:{token: "abc123"}  }));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    spyOn(component, 'candidateRegister').and.callThrough();
    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });
    component.candidateRegisterForm.controls['names'].setValue(faker.lorem.word({ length: { min: 2, max: 50 } }));
    component.candidateRegisterForm.controls['last_names'].setValue(faker.lorem.word({ length: { min: 2, max: 50 } }));
    component.candidateRegisterForm.controls['email'].setValue(faker.internet.email());
    component.candidateRegisterForm.controls['password'].setValue(pass);
    component.candidateRegisterForm.controls['passwordConfirm'].setValue(pass);
    component.candidateRegisterForm.controls['termsCheck'].setValue(true);
    component.candidateRegisterForm.controls['termsCheck2'].setValue(true);

    component.candidateRegister(component.candidateRegisterForm.value);
    fixture.detectChanges();

    expect(component.candidateRegister).toHaveBeenCalled();
    expect(candidateSignUpSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(component.registerSucess).toBeTruthy();
  });

  it("should put error on exception registering candidate", () => {
    let candidateSignUpSpy = spyOn(candidateService, 'userSignUp').and.returnValue(throwError(() => ({
      error: {
        success: false,
        errors: {
          password: "String should have at least 8 characters",
          nombres: "String should have at least 2 characters",
          apellidos: "String should have at least 2 characters",
          email: "String should match pattern '.*@.*'"
        }
      }
    })));

    spyOn(component, 'candidateRegister').and.callThrough();
    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });
    component.candidateRegisterForm.controls['names'].setValue(faker.lorem.word({ length: { min: 2, max: 50 } }));
    component.candidateRegisterForm.controls['last_names'].setValue(faker.lorem.word({ length: { min: 2, max: 50 } }));
    component.candidateRegisterForm.controls['email'].setValue(faker.internet.email());
    component.candidateRegisterForm.controls['password'].setValue(pass);
    component.candidateRegisterForm.controls['passwordConfirm'].setValue(pass);
    component.candidateRegisterForm.controls['termsCheck'].setValue(true);
    component.candidateRegisterForm.controls['termsCheck2'].setValue(true);

    component.candidateRegister(component.candidateRegisterForm.value);
    fixture.detectChanges();

    expect(component.candidateRegister).toHaveBeenCalled();
    expect(candidateSignUpSpy).toHaveBeenCalledTimes(1);
    expect(component.registerSucess).toBeFalsy();
  });

  it("should validate required fields", () => {
    spyOn(component, 'candidateRegister').and.callThrough();
    component.candidateRegisterForm.controls['names'].setValue("");
    component.candidateRegisterForm.controls['last_names'].setValue("");
    component.candidateRegisterForm.controls['email'].setValue("");
    component.candidateRegisterForm.controls['password'].setValue("");
    component.candidateRegisterForm.controls['passwordConfirm'].setValue("");
    component.candidateRegisterForm.controls['termsCheck'].setValue(false);
    component.candidateRegisterForm.controls['termsCheck2'].setValue(false);
    component.MarkTouchedAux();

    component.candidateRegister(component.candidateRegisterForm.value);
    fixture.detectChanges();
    expect(component.candidateRegisterForm.valid).toBeFalsy();
  });

  it('name field validity', () => {
    const names = component.candidateRegisterForm.controls['names'];
    expect(names.valid).toBeFalsy();

    names.setValue('');
    expect(names.hasError('required')).toBeTruthy();

    names.setValue(faker.lorem.word({ length: { min: 1, max: 1 } }));
    expect(names.hasError('minlength')).toBeTruthy();

    names.setValue(faker.lorem.words({ min: 101, max: 102 }));
    expect(names.hasError('maxlength')).toBeTruthy();

    names.setValue('     ');
    expect(names.hasError('isOnlyWhiteSpace')).toBeTruthy();
  });

  it('last_names field validity', () => {
    const lastnames = component.candidateRegisterForm.controls['last_names'];
    expect(lastnames.valid).toBeFalsy();

    lastnames.setValue('');
    expect(lastnames.hasError('required')).toBeTruthy();

    lastnames.setValue(faker.lorem.word({ length: { min: 1, max: 1 } }));
    expect(lastnames.hasError('minlength')).toBeTruthy();

    lastnames.setValue(faker.lorem.words({ min: 101, max: 102 }));
    expect(lastnames.hasError('maxlength')).toBeTruthy();

    lastnames.setValue('     ');
    expect(lastnames.hasError('isOnlyWhiteSpace')).toBeTruthy();
  });

  it('email field validity', () => {
    const email = component.candidateRegisterForm.controls['email'];
    expect(email.valid).toBeFalsy();

    email.setValue('');
    expect(email.hasError('required')).toBeTruthy();

    email.setValue(faker.lorem.word({ length: { min: 1, max: 1 } }));
    expect(email.hasError('minlength')).toBeTruthy();

    email.setValue(faker.lorem.words({ min: 256, max: 257 }));
    expect(email.hasError('maxlength')).toBeTruthy();

    email.setValue('     ');
    expect(email.hasError('isOnlyWhiteSpace')).toBeTruthy();

    email.setValue(faker.lorem.word({ length: 5 }));
    expect(email.hasError('email')).toBeTruthy();

  });

  it('password field validity', () => {
    const password = component.candidateRegisterForm.controls['password'];
    expect(password.valid).toBeFalsy();

    password.setValue('');
    expect(password.hasError('required')).toBeTruthy();

    password.setValue(faker.lorem.word({ length: { min: 1, max: 1 } }));
    expect(password.hasError('minlength')).toBeTruthy();

    password.setValue(faker.lorem.words({ min: 21, max: 22 }));
    expect(password.hasError('maxlength')).toBeTruthy();

    password.setValue('     ');
    expect(password.hasError('isOnlyWhiteSpace')).toBeTruthy();
  });

  it('get empty on default error message', () => {
    var errorMessage = component.getErrorMessage("nonexistent");

    expect(errorMessage).toEqual("");
  });

  it('termsCheck field validity', () => {
    const termsCheck = component.candidateRegisterForm.controls['termsCheck'];
    expect(termsCheck.valid).toBeFalsy();

    termsCheck.setValue(true);
    expect(termsCheck.valid).toBeTruthy();

    termsCheck.setValue(false);
    expect(termsCheck.hasError('required')).toBeTruthy();
  });

  it('candidateRegister with custom object', () => {
    let candidateSignUpSpy = spyOn(candidateService, 'userSignUp').and.returnValue(of({ success: true, data:{token: "abc123"}  }));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    let pass = faker.lorem.word({ length: { min: 8, max: 20 } });

    let data = new CandidateFormRegister(faker.lorem.word({ length: { min: 2, max: 50 } }),
                                        faker.lorem.word({ length: { min: 2, max: 50 } }),
                                        faker.internet.email(),
                                        pass,
                                        pass,
                                        true,
                                        true)

    component.candidateRegister(data);
    fixture.detectChanges();

    expect(candidateSignUpSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(component.registerSucess).toBeTruthy();
  });

});
