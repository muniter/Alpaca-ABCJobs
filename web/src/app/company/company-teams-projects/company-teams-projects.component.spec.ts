/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyTeamsProjectsComponent } from './company-teams-projects.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatDialog, MatDialogConfig, MatDialogModule } from '@angular/material/dialog';
import { DragScrollModule } from 'ngx-drag-scroll';
import { CompanyService } from '../company.service';
import { Team, TeamsListResponse } from '../Team';
import { Company } from '../company';
import { EMPTY, of } from 'rxjs';
import { Project, ProjectsListResponse } from '../Project';
import { MatFormFieldModule } from '@angular/material/form-field';

describe('CompanyTeamsProjectsComponent', () => {
  let component: CompanyTeamsProjectsComponent;
  let fixture: ComponentFixture<CompanyTeamsProjectsComponent>;

  let companyService: CompanyService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        MatFormFieldModule,
        SharedModule,
        FormsModule,
        MatDialogModule,
        DragScrollModule
      ],
      declarations: [CompanyTeamsProjectsComponent],
      providers: [
        DatePipe, {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        }
      ],
      teardown: {destroyAfterEach: false}
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyTeamsProjectsComponent);

    companyService = TestBed.inject(CompanyService)
    spyOn(companyService, 'getTeams').and.returnValue(of(new TeamsListResponse(true, [new Team(1, "", new Company("", ""), []), new Team(2, "", new Company("", ""), [])])));
    spyOn(companyService, 'getProjects').and.returnValue(of(new ProjectsListResponse(true, [new Project(1, "", "desc", new Team(2, "", new Company("", ""), [])), 
                                                                                            new Project(1, "", "desc", new Team(3, "", new Company("", ""), []))])));
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be true', () => {
    expect(true).toBeTruthy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain teams', () => {
    expect(component.teams.length).toBe(2);
  });

  it('should contain projects', () => {
    expect(component.projects.length).toBe(2);
  });

  it('should open projects modal', () => {
    const openDialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({afterClosed: () => EMPTY} as any)
    
    component.openAddProjectModal()

    expect(openDialogSpy).toHaveBeenCalledTimes(1)
  });

  it('should open teams modal', () => {
    const openDialogSpy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({afterClosed: () => EMPTY} as any)
    
    component.openAddTeamModal()

    expect(openDialogSpy).toHaveBeenCalledTimes(1)
  });
});
