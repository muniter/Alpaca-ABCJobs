/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyCreatePositionComponent } from './company-create-position.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { SharedModule } from 'src/app/shared/shared.module';
import { CompanyService } from '../company.service';
import { of } from 'rxjs';
import { TeamsListResponse, Team } from '../Team';
import { Company } from '../company';
import { Position } from '../Position';

describe('CompanyCreatePositionComponent', () => {
  let component: CompanyCreatePositionComponent;
  let fixture: ComponentFixture<CompanyCreatePositionComponent>;
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
        MatChipsModule,
        MatAutocompleteModule,
        MatInputModule,
        FormsModule,
        MatSelectModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        SharedModule
      ],
      declarations: [ CompanyCreatePositionComponent ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { token: "123abc" }
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
    fixture = TestBed.createComponent(CompanyCreatePositionComponent);

    companyService = TestBed.inject(CompanyService)
    spyOn(companyService, 'getTeams').and.returnValue(of(new TeamsListResponse(true, [new Team(1, "", new Company("", ""), []), new Team(2, "", new Company("", ""), [])])));
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain teams', () => {
    expect(component.allTeams.length).toBe(2);
  });

  it('should call service on position creation', () => {

    const name = component.positionCreateForm.controls['name'];
    const description = component.positionCreateForm.controls['description'];
    const team = component.positionCreateForm.controls['team'];

    name.setValue("prj")
    description.setValue("desc")
    team.setValue(new Team(1, "team", new Company("name", "email"), []))

    let companyServiceSpy = spyOn(companyService, 'postPosition').and.returnValue(of({ success: true, data: new Position(1, "", "", false, new Company("", ""), new Team(1, "", new Company("",""), []), null, []) }));

    component.positionCreation();

    expect(companyServiceSpy).toHaveBeenCalledTimes(1);
  });
});
