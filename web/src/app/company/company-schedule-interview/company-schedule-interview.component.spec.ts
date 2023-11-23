/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyScheduleInterviewComponent } from './company-schedule-interview.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { PreselectedCandidate } from 'src/app/candidate/candidate';
import { SharedModule } from 'src/app/shared/shared.module';
import { Position } from '../Position';
import { Team } from '../Team';
import { Company } from '../company';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { CompanyService } from '../company.service';
import { of } from 'rxjs';

describe('CompanyScheduleInterviewComponent', () => {
  let component: CompanyScheduleInterviewComponent;
  let fixture: ComponentFixture<CompanyScheduleInterviewComponent>;

  let companyService: CompanyService;

  const dialogMock = {
    close: () => { }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        MatFormFieldModule,
        RouterModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatAutocompleteModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        SharedModule
      ],
      declarations: [CompanyScheduleInterviewComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { token: "123abc", position: new Position(1, "", "", true, new Company("", ""), new Team(1, "", new Company("", ""), []), null, [new PreselectedCandidate(1, 1, "pepe", "", "", 23), new PreselectedCandidate(2, 2, "lala", "", "", 32)]) }
        },
        {
          provide: MatDialogRef,
          useValue: dialogMock
        },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyScheduleInterviewComponent);

    companyService = TestBed.inject(CompanyService)
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call service on save', () => {

    const date = component.scheduleInterviewForm.controls['date'];
    const hour = component.scheduleInterviewForm.controls['hour'];

    date.setValue(new Date(11, 11, 2022))
    hour.setValue("13:45")

    let companyServiceSpy = spyOn(companyService, 'scheduleInterview').and.returnValue(of({ success: true }));

    component.scheduleInterview();

    expect(companyServiceSpy).toHaveBeenCalledTimes(1);
  });

  it('should close', () => {
    component.onCancel()
  });
});
