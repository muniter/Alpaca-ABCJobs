/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, inject, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterTestingModule } from "@angular/router/testing";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { faker } from '@faker-js/faker';

import { CandidateEducationComponent } from './candidate-education.component';
import { CandidateService } from '../candidate.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CollegeDegree, CollegeDegreeResponse } from 'src/app/shared/CollegeDegree';
import { of } from 'rxjs';
import { CareerResponse, CareerServiceSchema } from '../career';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { DatePipe } from '@angular/common';

describe('CandidateEducationComponent', () => {
  let component: CandidateEducationComponent;
  let candidateService: CandidateService;
  let router: Router;
  let debug: DebugElement;
  let fixture: ComponentFixture<CandidateEducationComponent>;
  let collegeDegrees: CollegeDegree[];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        SharedModule,
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        MatIconModule,
        MatDividerModule,
        RouterTestingModule,
        BrowserAnimationsModule        
      ],
      declarations: [ 
        CandidateEducationComponent 
      ],
      providers: [DatePipe, {
        provide: ActivatedRoute,
        useValue: { snapshot: { params: { 'userToken': '123' } } }
      }]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateEducationComponent);

    collegeDegrees = [new CollegeDegree(1, 'Pregrado'),new CollegeDegree(2, 'Pregrado')];

    candidateService = TestBed.inject(CandidateService)
    spyOn(candidateService, 'getCollegeDegrees').and.returnValue(of(
      new CollegeDegreeResponse(true, collegeDegrees)
    ));
    router = TestBed.inject(Router)
    
    component = fixture.componentInstance;
    spyOn(candidateService, 'getCareersInfo').and.returnValue(of(
      new CareerResponse(
        true, 
        [ 
          new CareerServiceSchema(1, collegeDegrees[0],"Titulo 1","Universidad 1",2000,2005,null),
          new CareerServiceSchema(2, collegeDegrees[1],"Titulo 2","Universidad 2",2022,2023,"Logros alcanzados 2")
        ]
      )
    ));
    fixture.detectChanges();
    debug = fixture.debugElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should assign token', () => {
    expect(component.token == "123").toBeTruthy();
  });

  it('should retrieve careers', waitForAsync(inject([HttpTestingController, CandidateService], (httpMock: HttpTestingController, candidateService: CandidateService) => {
    
    expect(component.careersInfo[0]?.collegeDegree.id == 1).toBeTruthy();
    expect(component.careersInfo[0]?.careerTitle == "Titulo 1").toBeTruthy();
    expect(component.careersInfo[0]?.school == "Universidad 1").toBeTruthy();
    expect(component.careersInfo[0]?.careerStart == 2000).toBeTruthy();
    expect(component.careersInfo[0]?.careerEnd == 2005).toBeTruthy();
    expect(component.careersInfo[0]?.achievement).toBeNull();

  })));

  /* it('should validate collegeDegree', async () => {
    const collegeDegree = component.careers.at(0).get("collegeDegree");
    
    collegeDegree?.setValue(null);
    collegeDegree?.markAsUntouched();
    component.academicInformationForm.updateValueAndValidity();
    fixture.detectChanges();

    expect(collegeDegree?.hasError('required')).toBeTruthy();
    expect(collegeDegree?.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#collegeDegreeFormField mat-error')).nativeElement.innerHTML).toContain("Debe seleccionar");

  }) */

  it('should validate careerTitle', async () => {
    const careerTitle = component.careers.at(0).get("careerTitle");
    careerTitle?.markAsTouched();
    component.enableForm();

    careerTitle?.setValue('       ');
    fixture.detectChanges();
    expect(careerTitle?.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(careerTitle?.valid).toBeFalsy();
    expect(debug.query(By.css('#careerTitleFormField-0 mat-error')).nativeElement.innerHTML).toContain('El título obtenido no puede ser vacío');

    careerTitle?.setValue('a');
    fixture.detectChanges();

    expect(careerTitle?.hasError('minlength')).toBeTruthy();
    expect(careerTitle?.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#careerTitleFormField-0 mat-error')).nativeElement.innerHTML).toContain('El título obtenido debe tener entre 2 y 100 caracteres');

    careerTitle?.setValue(faker.lorem.words({ min: 101, max: 110 }));
    fixture.detectChanges();

    expect(careerTitle?.hasError('maxlength')).toBeTruthy();
    expect(careerTitle?.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#careerTitleFormField-0 mat-error')).nativeElement.innerHTML).toContain('El título obtenido debe tener entre 2 y 100 caracteres');

  })

  it('should validate school', async () => {
    const school = component.careers.at(0).get("school");
    school?.markAsTouched();
    component.enableForm();

    school?.setValue('       ');
    fixture.detectChanges();
    expect(school?.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(school?.valid).toBeFalsy();
    expect(debug.query(By.css('#schoolFormField-0 mat-error')).nativeElement.innerHTML).toContain('La institución educativa no puede ser vacía');

    school?.setValue('a');
    fixture.detectChanges();

    expect(school?.hasError('minlength')).toBeTruthy();
    expect(school?.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#schoolFormField-0 mat-error')).nativeElement.innerHTML).toContain('El nombre de la institución debe tener entre 2 y 255 caracteres');

    school?.setValue(faker.lorem.words({ min: 256, max: 259 }));
    fixture.detectChanges();

    expect(school?.hasError('maxlength')).toBeTruthy();
    expect(school?.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#schoolFormField-0 mat-error')).nativeElement.innerHTML).toContain('El nombre de la institución debe tener entre 2 y 255 caracteres');

  })

  it('should validate careerStart', async () => {
    const careerStart = component.careers.at(0).get("careerStart");
    careerStart?.markAsTouched();
    component.enableForm();

    careerStart?.setValue(null);
    fixture.detectChanges();
    
    expect(careerStart?.hasError('required')).toBeTruthy();
    expect(careerStart?.valid).toBeFalsy();
    expect(debug.query(By.css('#careerStartFormField-0 mat-error')).nativeElement.innerHTML).toContain('Debe seleccionar un año');

  })

  it('should validate careerEnd', async () => {
    const careerStart = component.careers.at(0).get("careerStart");
    const careerEnd = component.careers.at(0).get("careerEnd");
    careerEnd?.markAsTouched();
    component.enableForm();

    careerStart?.setValue(2020);
    careerEnd?.setValue(2019);
    fixture.detectChanges();
    
    expect(careerEnd?.hasError('greaterThan')).toBeTruthy();
    expect(careerEnd?.valid).toBeFalsy();
    expect(debug.query(By.css('#careerEndFormField-0 mat-error')).nativeElement.innerHTML).toContain('El año final debe ser mayor o igual al inicial');

  })

  it('should validate achievement', async () => {
    const achievement = component.careers.at(0).get("achievement");
    achievement?.markAsTouched();
    component.enableForm();

    achievement?.setValue('       ');
    fixture.detectChanges();
    expect(achievement?.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(achievement?.valid).toBeFalsy();
    expect(debug.query(By.css('#achievementFormField-0 mat-error')).nativeElement.innerHTML).toContain('Los logros no puede ser solo espacios en blanco');

    achievement?.setValue('a');
    fixture.detectChanges();

    expect(achievement?.hasError('minlength')).toBeTruthy();
    expect(achievement?.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#achievementFormField-0 mat-error')).nativeElement.innerHTML).toContain('Los logros puede ser vacio o deben tener entre 10 y 255 caracteres');

    achievement?.setValue(faker.lorem.words({ min: 256, max: 259 }));
    fixture.detectChanges();

    expect(achievement?.hasError('maxlength')).toBeTruthy();
    expect(achievement?.valid).toBeFalsy();
    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
    expect(debug.query(By.css('#achievementFormField-0 mat-error')).nativeElement.innerHTML).toContain('Los logros puede ser vacio o deben tener entre 10 y 255 caracteres');

  })

  it('should save academic info', waitForAsync(inject([CandidateService, HttpTestingController], (candidateService: CandidateService, httpMock: HttpTestingController) => {

    let candidateDeleteCareerInfo = spyOn(candidateService, 'deleteCareerInfo').and.returnValue(of(
      { success: true }));
      
    let candidateUpdateCareerInfo = spyOn(candidateService, 'updateCareerInfo').and.returnValue(of(
      { success: true }));
        
    let candidateAddCareerInfo = spyOn(candidateService, 'addCareerInfo').and.returnValue(of(
      { success: true }));

    let candidateCancelOrReload = spyOn(component, 'cancelOrReload');
    
    component.careers.clear();

    component.addCareer();
    let careerId = component.careers.at(-1).get("id");
    let collegeDegree = component.careers.at(-1).get("collegeDegree");
    let careerTitle = component.careers.at(-1).get("careerTitle");
    let school = component.careers.at(-1).get("school");
    let careerStart = component.careers.at(-1).get("careerStart");
    let careerEnd = component.careers.at(-1).get("careerEnd");
    let achievement = component.careers.at(-1).get("achievement");
    
    careerId?.setValue(1);
    collegeDegree?.setValue(collegeDegrees[0]);
    careerTitle?.setValue(faker.lorem.word({ length: { min: 5, max: 20 } }))
    school?.setValue(faker.lorem.word({ length: { min: 5, max: 20 } }))
    careerStart?.setValue(faker.number.int({ min: 2000, max: 2005 }))
    careerEnd?.setValue(faker.number.int({ min: 2005, max: 2010 }))
    achievement?.setValue(faker.lorem.words({ min: 2, max: 4 }))

    component.addCareer();
    careerId = component.careers.at(-1).get("id");
    collegeDegree = component.careers.at(-1).get("collegeDegree");
    careerTitle = component.careers.at(-1).get("careerTitle");
    school = component.careers.at(-1).get("school");
    careerStart = component.careers.at(-1).get("careerStart");
    careerEnd = component.careers.at(-1).get("careerEnd");
    achievement = component.careers.at(-1).get("achievement");
    
    careerId?.setValue(2);
    collegeDegree?.setValue(collegeDegrees[0]);
    careerTitle?.setValue(faker.lorem.word({ length: { min: 5, max: 20 } }))
    school?.setValue(faker.lorem.word({ length: { min: 5, max: 20 } }))
    careerStart?.setValue(faker.number.int({ min: 2000, max: 2005 }))
    careerEnd?.setValue(faker.number.int({ min: 2005, max: 2010 }))
    achievement?.setValue(faker.lorem.words({ min: 2, max: 4 }))

    component.addCareer();
    collegeDegree = component.careers.at(-1).get("collegeDegree");
    careerTitle = component.careers.at(-1).get("careerTitle");
    school = component.careers.at(-1).get("school");
    careerStart = component.careers.at(-1).get("careerStart");
    careerEnd = component.careers.at(-1).get("careerEnd");
    achievement = component.careers.at(-1).get("achievement");
    
    collegeDegree?.setValue(collegeDegrees[0]);
    careerTitle?.setValue(faker.lorem.word({ length: { min: 5, max: 20 } }))
    school?.setValue(faker.lorem.word({ length: { min: 5, max: 20 } }))
    careerStart?.setValue(faker.number.int({ min: 2000, max: 2005 }))
    careerEnd?.setValue(faker.number.int({ min: 2005, max: 2010 }))
    achievement?.setValue(faker.lorem.words({ min: 2, max: 4 }))
    
    component.deleteCareer(0);

    component.saveAcademicInfo();
    
    expect(candidateDeleteCareerInfo).toHaveBeenCalledTimes(1);
    expect(candidateUpdateCareerInfo).toHaveBeenCalledTimes(1);
    expect(candidateAddCareerInfo).toHaveBeenCalledTimes(1);

  })));
  
  it('should validate cancel operation', async () => {
    component.cancelOrReload();
    expect(component.deleteCareers).toHaveSize(0);
  })
});
