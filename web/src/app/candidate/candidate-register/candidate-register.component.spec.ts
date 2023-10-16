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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { faker } from '@faker-js/faker';

import { CandidateRegisterComponent } from './candidate-register.component';

describe('CandidateRegisterComponent', () => {
  let component: CandidateRegisterComponent;
  let fixture: ComponentFixture<CandidateRegisterComponent>;

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
        BrowserAnimationsModule
      ],
      declarations: [ CandidateRegisterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it("should register candidate", () => {
    spyOn(component, 'candidateRegister').and.callThrough();
    let pass = faker.lorem.word({ length: { min: 8, max: 20 }});
    component.candidateRegisterForm.controls['names'].setValue(faker.lorem.word({ length: { min: 2, max: 50 }}));
    component.candidateRegisterForm.controls['lastnames'].setValue(faker.lorem.word({ length: { min: 2, max: 50 }}));
    component.candidateRegisterForm.controls['email'].setValue(faker.internet.email());
    component.candidateRegisterForm.controls['password'].setValue(pass);
    component.candidateRegisterForm.controls['passwordConfirm'].setValue(pass);
    component.candidateRegisterForm.controls['termsCheck'].setValue(true);
    component.candidateRegisterForm.controls['termsCheck2'].setValue(true);

    component.candidateRegister(component.candidateRegisterForm.value);
    fixture.detectChanges();
    expect(component.candidateRegister).toHaveBeenCalled();
  });

  it("should validate required fields", () => {
    spyOn(component, 'candidateRegister').and.callThrough();
    component.candidateRegisterForm.controls['names'].setValue("");
    component.candidateRegisterForm.controls['lastnames'].setValue("");
    component.candidateRegisterForm.controls['email'].setValue("");
    component.candidateRegisterForm.controls['password'].setValue("");
    component.candidateRegisterForm.controls['passwordConfirm'].setValue("");
    component.candidateRegisterForm.controls['termsCheck'].setValue(false);
    component.candidateRegisterForm.controls['termsCheck2'].setValue(false);

    component.candidateRegister(component.candidateRegisterForm.value);
    fixture.detectChanges();
    expect(component.candidateRegisterForm.valid).toBeFalsy();
  });

  it('name field validity', () => {
    const names = component.candidateRegisterForm.controls['names'];
    expect(names.valid).toBeFalsy();

    names.setValue('');
    expect(names.hasError('required')).toBeTruthy();
    
    names.setValue(faker.lorem.word({ length: { min: 1, max: 1 }}));
    expect(names.hasError('minlength')).toBeTruthy();

    names.setValue(faker.lorem.words({min:101, max: 102}));
    expect(names.hasError('maxlength')).toBeTruthy();

    names.setValue('     ');
    expect(names.hasError('isOnlyWhiteSpace')).toBeTruthy();
  });

  it('lastnames field validity', () => {
    const lastnames = component.candidateRegisterForm.controls['lastnames'];
    expect(lastnames.valid).toBeFalsy();

    lastnames.setValue('');
    expect(lastnames.hasError('required')).toBeTruthy();

    lastnames.setValue(faker.lorem.word({ length: { min: 1, max: 1 }}));
    expect(lastnames.hasError('minlength')).toBeTruthy();

    lastnames.setValue(faker.lorem.words({min:101, max: 102}));
    expect(lastnames.hasError('maxlength')).toBeTruthy();

    lastnames.setValue('     ');
    expect(lastnames.hasError('isOnlyWhiteSpace')).toBeTruthy();
  });

  it('email field validity', () => {
    const email = component.candidateRegisterForm.controls['email'];
    expect(email.valid).toBeFalsy();

    email.setValue('');
    expect(email.hasError('required')).toBeTruthy();

    email.setValue(faker.lorem.word({ length: { min: 1, max: 1 }}));
    expect(email.hasError('minlength')).toBeTruthy();

    email.setValue(faker.lorem.words({min:256, max: 257}));
    expect(email.hasError('maxlength')).toBeTruthy();

    email.setValue('     ');
    expect(email.hasError('isOnlyWhiteSpace')).toBeTruthy();
    
    email.setValue(faker.lorem.word({length:5}));
    expect(email.hasError('email')).toBeTruthy();

  });

  it('password field validity', () => {
    const password = component.candidateRegisterForm.controls['password'];
    expect(password.valid).toBeFalsy();

    password.setValue('');
    expect(password.hasError('required')).toBeTruthy();

    password.setValue(faker.lorem.word({ length: { min: 1, max: 1 }}));
    expect(password.hasError('minlength')).toBeTruthy();

    password.setValue(faker.lorem.words({min:21, max: 22}));
    expect(password.hasError('maxlength')).toBeTruthy();

    password.setValue('     ');
    expect(password.hasError('isOnlyWhiteSpace')).toBeTruthy();
  });

  it('termsCheck field validity', () => {
    const termsCheck = component.candidateRegisterForm.controls['termsCheck'];
    expect(termsCheck.valid).toBeFalsy();
    
    termsCheck.setValue(true);
    expect(termsCheck.valid).toBeTruthy();

    termsCheck.setValue(false);
    expect(termsCheck.hasError('required')).toBeTruthy();
  });

});
