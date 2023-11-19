import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyHiredEvaluationComponent } from './company-hired-evaluation.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { SharedModule } from 'src/app/shared/shared.module';
import { ActivatedRoute } from '@angular/router';
import { Employee } from '../Employee';
import { faker } from '@faker-js/faker';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CompanyService } from '../company.service';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

describe('CompanyHiredEvaluationComponent', () => {
  let component: CompanyHiredEvaluationComponent;
  let fixture: ComponentFixture<CompanyHiredEvaluationComponent>;
  let service: CompanyService;
  const employee = new Employee(
    1,
    faker.person.fullName(),
    faker.person.jobTitle(),
    {
      id: 1,
      name: faker.lorem.word(),
    },
    [],
    [],
    [{ id: 1, name: faker.lorem.word() }]
  );

  const dialogMock = {
    close: () => { }
  };

  beforeEach(() => {


    TestBed.configureTestingModule({
      declarations: [CompanyHiredEvaluationComponent],
      imports: [
        HttpClientTestingModule,
        MatFormFieldModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        BrowserAnimationsModule,
      ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { token: "123abc", employee }
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
    });
    fixture = TestBed.createComponent(CompanyHiredEvaluationComponent);
    service = TestBed.inject(CompanyService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close dialog', () => {
    spyOn(component.dialogRef, 'close');
    component.onCancel();
    expect(component.dialogRef.close).toHaveBeenCalled();
  })

  it('should submit form', () => {
    // Validates min
    component.form.controls['result'].setValue('-1');
    expect(component.form.valid).toBeFalsy();
    expect(component.form.controls['result'].valid).toBeFalsy();
    fixture.detectChanges();

    // Validates max
    component.form.controls['result'].setValue('1000');
    expect(component.form.valid).toBeFalsy();
    expect(component.form.controls['result'].valid).toBeFalsy();
    fixture.detectChanges();

    // Validates required
    component.form.controls['result'].setValue('');
    expect(component.form.valid).toBeFalsy();
    expect(component.form.controls['result'].valid).toBeFalsy();
    fixture.detectChanges();
  })

  it('should submit form', () => {
    // Mock the service
    spyOn(service, 'postEmployeeEvaluation').and.returnValue(of({
      success: true,
      data: employee,
    }));

    component.form.controls['result'].setValue('1');
    fixture.detectChanges();
    expect(component.form.valid).toBeTruthy();
    component.onSave();
    expect(service.postEmployeeEvaluation).toHaveBeenCalled();
    fixture.detectChanges();
  })

  it('should show server errors', () => {
    // Mock the service
    const errorResponse = new HttpErrorResponse({
      error: {
        errors: {
          date: 'Evaluation already exists for this date',
        }
      },
      status: 400
    });
    spyOn(service, 'postEmployeeEvaluation').and.returnValue(throwError(errorResponse));

    component.form.controls['result'].setValue('1');
    expect(component.form.valid).toBeTruthy();
    component.onSave();
    expect(service.postEmployeeEvaluation).toHaveBeenCalled();
    expect(component.form.valid).toBeFalsy();
    fixture.detectChanges();
  })
});
