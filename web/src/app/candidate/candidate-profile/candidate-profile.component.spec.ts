/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed, waitForAsync, inject } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { SharedModule } from 'src/app/shared/shared.module';
import { MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from "@angular/router/testing";

import { CandidateProfileComponent } from './candidate-profile.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DatePipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSelectModule } from '@angular/material/select';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CandidateService } from '../candidate.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { PersonalInfo, PersonalInfoResponse } from '../candidate';
import { faker } from '@faker-js/faker';
import { HttpTestingController } from '@angular/common/http/testing';
import { C } from '@angular/cdk/keycodes';
import { Country, CountryResponse } from 'src/app/shared/Country';
import { Language, LanguageResponse } from 'src/app/shared/Language';

describe('CandidateProfileComponent', () => {
  let component: CandidateProfileComponent;
  let fixture: ComponentFixture<CandidateProfileComponent>;
  let candidateService: CandidateService;
  let router: Router;
  let debug: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        MatDialogModule,
        RouterTestingModule,
        HttpClientTestingModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatAutocompleteModule,
        MatSelectModule,
        MatChipsModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatIconModule,
        MatDividerModule,
        MatInputModule,
        BrowserAnimationsModule
      ],
      declarations: [CandidateProfileComponent],
      providers: [DatePipe, {
        provide: ActivatedRoute,
        useValue: { snapshot: { params: { 'userToken': '123' } } }
      }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateProfileComponent);
    
    candidateService = TestBed.inject(CandidateService)
    spyOn(candidateService, 'getCountries').and.returnValue(of(new CountryResponse(true, [new Country(1,"CO","CO","Colombia","Colombian")])));
    spyOn(candidateService, 'getLanguages').and.returnValue(of(new LanguageResponse(true, [new Language("ES", "Spanish")])));
    router = TestBed.inject(Router)

    component = fixture.componentInstance;
    spyOn(candidateService, 'getPersonalInfo').and.returnValue(of(new PersonalInfoResponse(true, new PersonalInfo("Pepe", "Perez", "Pepe Perez", "pepe@pepe.co", "2023-10-31", 1, null, null, null, null, null, []))));
    fixture.detectChanges();
    debug = fixture.debugElement;

  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should assign token', () => {
    expect(component.token == "123").toBeTruthy();
  });

  it('should retrieve userdata', waitForAsync(inject([HttpTestingController, CandidateService], (httpMock: HttpTestingController, candidateService: CandidateService) => {
    
    //expect(candidateGetPersonalData).toHaveBeenCalledTimes(1);

    expect(component.userPersonalInfo?.last_names == "Perez").toBeTruthy();
    expect(component.userPersonalInfo?.names == "Pepe").toBeTruthy();
    expect(component.userPersonalInfo?.email == "pepe@pepe.co").toBeTruthy();
    expect(component.userPersonalInfo?.biography).toBeNull();

  })));

  it('should validate address', async () => {
    const candidateAddress = component.personalInformationForm.controls['candidateAddress'];
    candidateAddress.markAsTouched();
    component.enableForm();

    candidateAddress.setValue('         ');
    fixture.detectChanges();

    expect(candidateAddress.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(candidateAddress.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#addressFormField mat-error')).nativeElement.innerHTML).toContain('La dirección no puede contener solo espacios');

    candidateAddress.setValue('a');
    fixture.detectChanges();

    expect(candidateAddress.hasError('minlength')).toBeTruthy();
    expect(candidateAddress.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#addressFormField mat-error')).nativeElement.innerHTML).toContain('La dirección debe tener entre 5 y 255 caracteres');

    candidateAddress.setValue(faker.lorem.words({ min: 256, max: 259 }));
    fixture.detectChanges();

    expect(candidateAddress.hasError('maxlength')).toBeTruthy();
    expect(candidateAddress.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#addressFormField mat-error')).nativeElement.innerHTML).toContain('La dirección debe tener entre 5 y 255 caracteres');
  })

  it('should validate bio', async () => {
    const candidateBio = component.personalInformationForm.controls['candidateBio'];
    candidateBio.markAsTouched();
    component.enableForm();

    candidateBio.setValue('              ');
    fixture.detectChanges();

    expect(candidateBio.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(candidateBio.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateBioFormField mat-error')).nativeElement.innerHTML).toContain('El texto ingresado no puede contener solo espacios');

    candidateBio.setValue('a');
    fixture.detectChanges();

    expect(candidateBio.hasError('minlength')).toBeTruthy();
    expect(candidateBio.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateBioFormField mat-error')).nativeElement.innerHTML).toContain('El texto ingresado debe tener entre 10 y 255 caracteres');

    candidateBio.setValue(faker.lorem.words({ min: 256, max: 259 }));
    fixture.detectChanges();

    expect(candidateBio.hasError('maxlength')).toBeTruthy();
    expect(candidateBio.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateBioFormField mat-error')).nativeElement.innerHTML).toContain('El texto ingresado debe tener entre 10 y 255 caracteres');
  })

  it('should validate mobile', async () => {
    const candidateMobile = component.personalInformationForm.controls['candidateMobile'];
    candidateMobile.markAsTouched();
    component.enableForm();

    candidateMobile.setValue('asd');
    fixture.detectChanges();

    expect(candidateMobile.hasError('pattern')).toBeTruthy();
    expect(candidateMobile.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateMobileFormField mat-error')).nativeElement.innerHTML).toContain('El número celular debe tener solamente números');

    candidateMobile.setValue('1');
    fixture.detectChanges();

    expect(candidateMobile.hasError('minlength')).toBeTruthy();
    expect(candidateMobile.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateMobileFormField mat-error')).nativeElement.innerHTML).toContain('El número celular debe tener entre 2 y 15 caracteres');

    candidateMobile.setValue("1234567890123456789");
    fixture.detectChanges();

    expect(candidateMobile.hasError('maxlength')).toBeTruthy();
    expect(candidateMobile.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#candidateMobileFormField mat-error')).nativeElement.innerHTML).toContain('El número celular debe tener entre 2 y 15 caracteres');
  })

  it('should save personal info', waitForAsync(inject([CandidateService, HttpTestingController], (candidateService: CandidateService, httpMock: HttpTestingController) => {

    let candidateSavePersonalInfo = spyOn(candidateService, 'updatePersonalInfo').and.returnValue(of({ success: true }));
    let navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    component.savePersonalInfo();

    expect(candidateSavePersonalInfo).toHaveBeenCalledTimes(1);

  })));

  it('should delete language', () => {

    component.selectedLanguages.push("testlang");
    component.removeLanguage("testlang");

    expect(component.selectedLanguages.includes("testLang")).toBeFalsy();

  });


  it('should delete not included language', async () => {
    let errorstring = component.getPersonalInfoErrorMessage("nonexistent");

    expect(errorstring == "").toBeTruthy()
  })

  it('should delete not included language', async () => {

    component.selectedLanguages.push("testlang");
    component.removeLanguage("testlangaa");

    expect(component.selectedLanguages.includes("testlang")).toBeTruthy();
    expect(component.selectedLanguages.includes("testlangaa")).toBeFalse();

  });


  const delay = (ms: number | undefined) => new Promise(res => setTimeout(res, ms));
});
