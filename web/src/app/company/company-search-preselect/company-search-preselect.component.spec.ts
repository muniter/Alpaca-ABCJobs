/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanySearchPreselectComponent } from './company-search-preselect.component';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateSearch } from 'src/app/candidate/candidate';
import { faker } from '@faker-js/faker';
import { Country } from 'src/app/shared/Country';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { CompanyService } from '../company.service';
import { of, throwError } from 'rxjs';
import { Vacancy, VacancyResponse } from '../vacancy';

export class MatDialogMock {
  closeAll() {
    return true;
  }
}

function getCandidate(): CandidateSearch {
  return new CandidateSearch(
    faker.number.int(),
    faker.person.firstName(),
    faker.person.lastName(),
    faker.internet.email(),
    new Country(
      parseInt(faker.location.countryCode('numeric')), 
      faker.location.countryCode('alpha-2'), 
      faker.location.countryCode('alpha-3'), 
      faker.location.country(),
      ""
    ),
    faker.location.city(),
    [],
    [],
    [],
    [],
    [],
    [] 
  )
}

describe('CompanySearchPreselectComponent', () => {
  let component: CompanySearchPreselectComponent;
  let fixture: ComponentFixture<CompanySearchPreselectComponent>;
  let navigateSpy: any;
  let router: Router;
  let companyService: CompanyService;
  let vacancyResponse: VacancyResponse;
  let vacancy1: Vacancy;
  let vacancy2: Vacancy;
  let vacancies: Vacancy[];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        ReactiveFormsModule,
        FormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatSelectModule,
        SharedModule,
        BrowserAnimationsModule,
        RouterTestingModule
      ],
      providers: [
        DatePipe,
        { 
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        },
        { provide: MatDialogRef, useValue: undefined }, 
        { provide: MAT_DIALOG_DATA, 
          useValue: { 
            token: 'fake-jwt-token', 
            dialog: new MatDialogMock(), 
            theme: 'company-theme',
            candidateId: getCandidate().id
          }
        }
      ],
      declarations: [ CompanySearchPreselectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanySearchPreselectComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router)
    companyService = TestBed.inject(CompanyService);
    navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    vacancy1 = new Vacancy(faker.number.int(), faker.person.jobTitle());
    vacancy2 = new Vacancy(faker.number.int(), faker.person.jobTitle());
    vacancies = [vacancy1, vacancy2];
    vacancyResponse = new VacancyResponse(true, vacancies);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect when user token not found', () => {
    let token = '';
    component.validateToken(token);
    expect(component.token).toEqual('');
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });

  it('should retrieve vacancies', () => {
    let getVacanciesSpy = spyOn(companyService, 'getVacancies').and.returnValue(of(vacancyResponse));    
    component.getVacancies();
    expect(getVacanciesSpy).toHaveBeenCalledTimes(1);
    expect(component.vacancies).toEqual(vacancies);
    expect(component.vacancies[0]?.id).toEqual(vacancy1.id);
    expect(component.vacancies[0]?.name).toEqual(vacancy1.name);
    expect(component.vacancies[1]?.id).toEqual(vacancy2.id);
    expect(component.vacancies[1]?.name).toEqual(vacancy2.name);
  });

  it('should handle global exceptions', () => {
    expect([null,'',undefined]).toContain(component.globalError);
    component.preselectCandidateForm.get('vacancy')?.setValue(vacancy1.id);
    let error = { error: { errors: { global: "Invalid authorization code" } } };
    let preselectVacancySpyFail = spyOn(companyService, 'preselectCandidate').and.returnValue(throwError(() => error));
    component.preselectCandidate();
    fixture.detectChanges();
    expect(preselectVacancySpyFail).toHaveBeenCalledTimes(1);
    expect(component.globalError).not.toBeNull();
  });

  it('should handle crash exceptions', () => {
    component.preselectCandidateForm.get('vacancy')?.setValue(vacancy1.id);
    let error = { error: "service unavailable" };
    let preselectVacancySpyFail = spyOn(companyService, 'preselectCandidate').and.returnValue(throwError(() => error));
    component.preselectCandidate();
    component.getErrorMessage("");
    fixture.detectChanges();
    expect(preselectVacancySpyFail).toHaveBeenCalledTimes(1);
  });

  it('should handle specific exceptions', () => {
    const vacancyField = component.preselectCandidateForm.get('vacancy');
    expect(vacancyField?.hasError('responseMessageError')).toBeFalsy()
    vacancyField?.setValue(vacancy1.id);
    let error = { error: { errors: { vacancy: "Candidate already preselected" } } };
    let preselectVacancySpyFail = spyOn(companyService, 'preselectCandidate').and.returnValue(throwError(() => error));
    component.preselectCandidate();
    fixture.detectChanges();
    expect(preselectVacancySpyFail).toHaveBeenCalledTimes(1);
    expect(vacancyField?.hasError('responseMessageError')).toBeTruthy();
  });
  
  it('should preselect candidate', fakeAsync(() => {
    expect([null,'',undefined]).toContain(component.globalMessage);
    const vacancyField = component.preselectCandidateForm.get('vacancy');
    vacancyField?.setValue(vacancy1.id);
    let preselectVacancySpy = spyOn(companyService, 'preselectCandidate').and.returnValue(of(vacancyResponse));    
    const closeAllDialogSpy = spyOn(component.dialog, 'closeAll');
    component.preselectCandidate();
    fixture.detectChanges();
    expect(preselectVacancySpy).toHaveBeenCalledTimes(1);
    expect(component.globalMessage).not.toBeNull();
    tick(3000);
    expect([null,'',undefined]).toContain(component.globalMessage);
    expect(closeAllDialogSpy).toHaveBeenCalledTimes(1);
  }));
  
  it('should cancel operation', () => {
    const closeAllDialogSpy = spyOn(component.dialog, 'closeAll');
    component.cancel();
    expect(closeAllDialogSpy).toHaveBeenCalledTimes(1);
  });

  
});
