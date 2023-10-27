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
    collegeDegree?.markAsTouched();
    collegeDegree?.markAsUntouched();
    component.enableForm();
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

   

  })

});
