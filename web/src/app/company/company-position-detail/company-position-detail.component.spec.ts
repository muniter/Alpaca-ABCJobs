/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyPositionDetailComponent } from './company-position-detail.component';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CompanyService } from '../company.service';
import { Position } from '../Position';
import { Company } from '../company';
import { Team } from '../Team';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SharedModule } from 'src/app/shared/shared.module';
import { PreselectedCandidate } from 'src/app/candidate/candidate';
import { EMPTY, of } from 'rxjs';

describe('CompanyPositionDetailComponent', () => {
  let component: CompanyPositionDetailComponent;
  let fixture: ComponentFixture<CompanyPositionDetailComponent>;
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
        MatAutocompleteModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatDialogModule,
        SharedModule
      ],
      declarations: [ CompanyPositionDetailComponent ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { token: "123abc", position: new Position(1, "", "", true, new Company("", ""), new Team( 1, "", new Company("",""), []), null, [new PreselectedCandidate(1,1,"pepe","","",23)]) }
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
    fixture = TestBed.createComponent(CompanyPositionDetailComponent);

    companyService = TestBed.inject(CompanyService)
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close', () => {
    component.onCancel()
  });

  it('should save scores', () => {
    let companyServiceSpy = spyOn(companyService, 'saveScores').and.returnValue(of(true));

    component.saveScores()

    expect(companyServiceSpy).toHaveBeenCalledTimes(1);

  });

  it('should open PositionClose modal', () => {
    const openDialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({ afterClosed: () => EMPTY } as any)

    component.openPositionCloseModal(new Position(1, "", "", true, new Company("", ""), new Team(1, "", new Company("",""), []), null, []))

    expect(openDialogSpy).toHaveBeenCalledTimes(1)
  });

  it('should open ScheduleInterview modal', () => {
    const openDialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({ afterClosed: () => EMPTY } as any)

    component.openScheduleInterviewModal(new Position(1, "", "", true, new Company("", ""), new Team(1, "", new Company("",""), []), null, []))

    expect(openDialogSpy).toHaveBeenCalledTimes(1)
  });
});
