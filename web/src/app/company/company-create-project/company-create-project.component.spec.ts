/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyCreateProjectComponent } from './company-create-project.component';
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
import { Personality } from 'src/app/shared/Personality';
import { Employee } from '../Employee';
import { TeamsListResponse, Team } from '../Team';
import { Company } from '../company';
import { Project } from '../Project';

describe('CompanyCreateProjectComponent', () => {
  let component: CompanyCreateProjectComponent;
  let fixture: ComponentFixture<CompanyCreateProjectComponent>;
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
      declarations: [CompanyCreateProjectComponent],
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
    fixture = TestBed.createComponent(CompanyCreateProjectComponent);

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

  it('should call service on team creation', () => {

    const name = component.projectCreateForm.controls['name'];
    const description = component.projectCreateForm.controls['description'];
    const team = component.projectCreateForm.controls['team'];

    name.setValue("prj")
    description.setValue("desc")
    team.setValue(new Team(1, "team", new Company("name", "email"), []))

    let companyServiceSpy = spyOn(companyService, 'postProject').and.returnValue(of({ success: true, data: new Project(1, "prj", "desc", new Team(1, "team", new Company("name", "email"), [])) }));

    component.projectCreation();

    expect(companyServiceSpy).toHaveBeenCalledTimes(1);
  });
});
