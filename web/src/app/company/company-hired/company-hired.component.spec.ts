import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyHiredComponent } from './company-hired.component';
import { CompanyService } from '../company.service';
import { Employee } from '../Employee';
import { faker } from '@faker-js/faker';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from 'src/app/shared/shared.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { NgxPaginationModule } from 'ngx-pagination';
import { of } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';

describe('CompanyHiredComponent', () => {
  let component: CompanyHiredComponent;
  let fixture: ComponentFixture<CompanyHiredComponent>;
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

  const matDialogMock = {
    open: () => {
      return {
        afterClosed: () => of(true)
      }
    }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CompanyHiredComponent],
      imports: [
        HttpClientTestingModule,
        SharedModule,
        BrowserAnimationsModule,
        MatIconModule,
        NgxPaginationModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        },
        {
          provide: MatDialog,
          useValue: matDialogMock
        }
      ],
    });
    fixture = TestBed.createComponent(CompanyHiredComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(CompanyService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getHiredEmployees', () => {
    spyOn(service, 'getEmployees').and.returnValue(of({ success: true, data: [employee] }));
    component.ngOnInit();
    expect(service.getEmployees).toHaveBeenCalled();
  });

  it('should know if there\'s an evaluation for the current month', () => {
    const cemployee = Object.assign({}, employee);
    cemployee.evaluations = [];
    component.employees = [cemployee];
    fixture.detectChanges();
    expect(component.currentMonthEvaluation(employee)).toEqual('');

    const curDate = new Date();
    cemployee.evaluations = [
      {
        id: 1,
        date: curDate.toISOString().split('T')[0],
        result: 1,
      },
    ];
    component.employees = [cemployee];
    fixture.detectChanges();
    expect(component.currentMonthEvaluation(employee)).not.toEqual('1');
  });

  it('should know if there\'s a pending evaluation', () => {
    expect(component.evaluacionPendiente(employee)).toBeTruthy();
  });

  it('should know the employee team', () => {
    const cemployee = Object.assign({}, employee);
    cemployee.teams = [
      {
        id: 1,
        name: faker.lorem.word(),
      },
    ];
    expect(component.employeeTeam(cemployee)).toEqual(cemployee.teams[0].name);
  })

  it('should open the evaluation modal', () => {
    spyOn(component.matDialog, 'open').and.callThrough(); // Use callThrough to use the mock implementation
    component.evaluacionModal(employee);
    expect(component.matDialog.open).toHaveBeenCalled();
  });
});
