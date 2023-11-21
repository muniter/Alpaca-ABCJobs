/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyPositionCloseComponent } from './company-position-close.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { PreselectedCandidate } from 'src/app/candidate/candidate';
import { Position } from '../Position';
import { Team } from '../Team';
import { Company } from '../company';
import { CompanyService } from '../company.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SharedModule } from 'src/app/shared/shared.module';
import { MatSelectModule } from '@angular/material/select';
import { of } from 'rxjs';

describe('CompanyPositionCloseComponent', () => {
  let component: CompanyPositionCloseComponent;
  let fixture: ComponentFixture<CompanyPositionCloseComponent>;
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
        MatSelectModule,
        MatAutocompleteModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        SharedModule
      ],
      declarations: [CompanyPositionCloseComponent],
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
    fixture = TestBed.createComponent(CompanyPositionCloseComponent);

    companyService = TestBed.inject(CompanyService)
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call service on position close', () => {

    const candidate = component.closePositionForm.controls['candidate'];

    candidate.setValue(new PreselectedCandidate(1, 1, "", "", "", 32))

    let companyServiceSpy = spyOn(companyService, 'selectCandidate').and.returnValue(of({ success: true }));

    component.selectCandidate();

    expect(companyServiceSpy).toHaveBeenCalledTimes(1);
  });

  it('should close', () => {
    component.onCancel()
  });
});
