/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyPeopleComponent } from './company-people.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EMPTY, of } from 'rxjs';
import { TeamsListResponse, Team } from '../Team';
import { Company } from '../company';
import { CompanyService } from '../company.service';
import { Employee, EmployeesListResponse } from '../Employee';
import { Personality } from 'src/app/shared/Personality';
import { MatChipsModule } from '@angular/material/chips';

describe('CompanyPeopleComponent', () => {
  let component: CompanyPeopleComponent;
  let fixture: ComponentFixture<CompanyPeopleComponent>;

  let companyService: CompanyService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule,
        SharedModule,
        FormsModule,
        MatDialogModule,
        MatChipsModule],
      declarations: [CompanyPeopleComponent],
      providers: [DatePipe, {
        provide: ActivatedRoute,
        useValue: { snapshot: { params: { 'userToken': '123' } } }
      }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyPeopleComponent);

    companyService = TestBed.inject(CompanyService)
    spyOn(companyService, 'getEmployees').and.returnValue(of(new EmployeesListResponse(true, [new Employee(1, "", "", new Personality(1, ""), []),
    new Employee(2, "", "", new Personality(2, ""), [])])));
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain employees', () => {
    expect(component.employees.length).toBe(2);
  });

  it('should open create modal', () => {
    const openDialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({ afterClosed: () => EMPTY } as any)

    component.openAddModal()

    expect(openDialogSpy).toHaveBeenCalledTimes(1)
  });
});
