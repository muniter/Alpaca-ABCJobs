/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyCreateTeamComponent } from './company-create-team.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { SharedModule } from 'src/app/shared/shared.module';
import { ActivatedRoute } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { CompanyService } from '../company.service';
import { Employee, EmployeesListResponse } from '../Employee';
import { Personality } from 'src/app/shared/Personality';
import { of, throwError } from 'rxjs';
import { Team, TeamCreateResponse } from '../Team';
import { Company } from '../company';

describe('CompanyCreateTeamComponent', () => {
  let component: CompanyCreateTeamComponent;
  let fixture: ComponentFixture<CompanyCreateTeamComponent>;
  let companyService: CompanyService;

  const dialogMock = {
    close: () => { }
  };


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        MatFormFieldModule,
        MatSelectModule,
        FormsModule,
        MatInputModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        SharedModule
      ],
      declarations: [CompanyCreateTeamComponent],
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
    fixture = TestBed.createComponent(CompanyCreateTeamComponent);

    companyService = TestBed.inject(CompanyService)
    spyOn(companyService, 'getEmployees').and.returnValue(of(new EmployeesListResponse(true, [new Employee(1, "Pepe", "cajero", new Personality(1, "tranqui"), [])])));
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain employees', () => {
    expect(component.allEmployees.length).toBe(1);
  });

  it('should verify repeated', () => {
    let baseEmployee = new Employee(1, "Pepe", "cajero", new Personality(1, "tranqui"), [])

    component.selectedEmployees.push(baseEmployee)
    component.selectedEmployees.push(baseEmployee)

    component.checkRepeated()
    expect(component.repeatedMembers).toBeTruthy()
  })

  it('should verify no repeated', () => {
    let baseEmployee = new Employee(1, "Pepe", "cajero", new Personality(1, "tranqui"), [])

    component.selectedEmployees.push(baseEmployee)
    component.checkRepeated()
    expect(component.repeatedMembers).toBeFalsy()
  })

  it('should create team', () => {
    const name = component.teamCreateForm.controls['name'];
    name.setValue("Equipo A")

    let baseEmployee = new Employee(1, "Pepe", "cajero", new Personality(1, "tranqui"), [])
    component.selectedEmployees.push(baseEmployee)

    let companyServiceSpy = spyOn(companyService, 'postTeam').and.returnValue(of(new TeamCreateResponse(true, new Team(1, "", new Company("", ""), []))));

    component.teamCreation();

    expect(companyServiceSpy).toHaveBeenCalledTimes(1);
  })

  it('should handle error creating team', () => {
    const name = component.teamCreateForm.controls['name'];
    name.setValue("Equipo A")

    let baseEmployee = new Employee(1, "Pepe", "cajero", new Personality(1, "tranqui"), [])
    component.selectedEmployees.push(baseEmployee)

    let companyServiceSpy = spyOn(companyService, 'postTeam').and.returnValue(throwError("err"));

    component.teamCreation();

    expect(companyServiceSpy).toHaveBeenCalledTimes(1);
  })

  it('should close', () => {
    component.onCancel()
  });
});
