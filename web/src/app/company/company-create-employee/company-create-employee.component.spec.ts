/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyCreateEmployeeComponent } from './company-create-employee.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSelectModule } from '@angular/material/select';
import { SharedModule } from 'src/app/shared/shared.module';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { CompanyService } from '../company.service';
import { Skill, SkillResponse } from 'src/app/shared/skill';
import { of } from 'rxjs';
import { Personality, PersonalityResponse } from 'src/app/shared/Personality';
import { Employee } from '../Employee';
import { DialogRef } from '@angular/cdk/dialog';

describe('CompanyCreateEmployeeComponent', () => {
  let component: CompanyCreateEmployeeComponent;
  let fixture: ComponentFixture<CompanyCreateEmployeeComponent>;
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
      declarations: [CompanyCreateEmployeeComponent],
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
    fixture = TestBed.createComponent(CompanyCreateEmployeeComponent);
    
    companyService = TestBed.inject(CompanyService)
    spyOn(companyService, 'getSkills').and.returnValue(of(new SkillResponse(true, [new Skill(1,"Angular"), new Skill(2,"Python")])));
    spyOn(companyService, 'getPersonalities').and.returnValue(of(new PersonalityResponse(true, [new Personality(1,"Per1"), new Personality(2,"Per2")])));
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain skills', () => {
    expect(component.personalities.length).toBe(2);
  });

  it('should contain personalities', () => {
    expect(component.objectSkills.length).toBe(2);
  });


  it('should call service on employee creation', () => {

    const name = component.employeeCreateForm.controls['name'];
    const position = component.employeeCreateForm.controls['position'];
    const personality = component.employeeCreateForm.controls['personality'];

    component.selectedSkills = ["Angular"]

    name.setValue("Pepe")
    position.setValue("QAE")
    personality.setValue(2)

    let companyServiceSpy = spyOn(companyService, 'postEmployee').and.returnValue(of({ success: true, data: new Employee(1,"","", new Personality(1,""),[]) }));

    component.employeeCreation();

    expect(companyServiceSpy).toHaveBeenCalledTimes(1);
  });

  it('should not fail removing unexistent skill', () => {
    expect(component.selectedSkills.length).toBe(0);
    
    component.removeSkill("lalala")

    expect(component.selectedSkills.length).toBe(0);
  });

  it('should remove existent skill', () => {
    expect(component.selectedSkills.length).toBe(0);

    component.selectedSkills = ["lalala", "lelele"]
    expect(component.selectedSkills.length).toBe(2);
    
    component.removeSkill("lalala")

    expect(component.selectedSkills.length).toBe(1);
    expect(component.selectedSkills.indexOf("lelele")).toBe(0);
    expect(component.selectedSkills.indexOf("lalala")).toBe(-1);
  });

  it('should close', () => {
    component.onCancel()
  });

  it('should return empty on nonexistent field', () => {
    let err = component.getErrorMessage("some")

    expect(err).toBe("")
  });

});
