/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyPositionsComponent } from './company-positions.component';
import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { SharedModule } from 'src/app/shared/shared.module';
import { CompanyService } from '../company.service';
import { EMPTY, of } from 'rxjs';
import { Position, PositionsListResponse } from '../Position';
import { Company } from '../company';
import { Team } from '../Team';

describe('CompanyPositionsComponent', () => {
  let component: CompanyPositionsComponent;
  let fixture: ComponentFixture<CompanyPositionsComponent>;

  let companyService: CompanyService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule,
        SharedModule,
        FormsModule,
        MatDialogModule,
        MatChipsModule],
      declarations: [ CompanyPositionsComponent ],
      providers: [DatePipe, {
        provide: ActivatedRoute,
        useValue: { snapshot: { params: { 'userToken': '123' } } }
      }]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyPositionsComponent);

    companyService = TestBed.inject(CompanyService)
    spyOn(companyService, 'getPositions').and.returnValue(of(new PositionsListResponse(true, [new Position(1, "", "", false, new Company("", ""), new Team(1, "", new Company("",""), []), [])])))
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain positions', () => {
    expect(component.positions.length).toBe(1);
  });

  it('should open create modal', () => {
    const openDialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({ afterClosed: () => EMPTY } as any)

    component.openAddPositionModal()

    expect(openDialogSpy).toHaveBeenCalledTimes(1)
  });
});
