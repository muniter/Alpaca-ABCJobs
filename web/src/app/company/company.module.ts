import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CompanyLoginComponent } from './company-login/company-login.component';
import { MatDividerModule } from '@angular/material/divider';
import { CompanyRegisterComponent } from './company-register/company-register.component';
import { CompanyHomeComponent } from './company-home/company-home.component';
import { CompanyPeopleComponent } from './company-people/company-people.component';
import { MatChipsModule } from '@angular/material/chips';
import { CompanyCreateEmployeeComponent } from './company-create-employee/company-create-employee.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSelectModule } from '@angular/material/select';
import { CompanyTeamsProjectsComponent } from './company-teams-projects/company-teams-projects.component';
import { CompanyCreateTeamComponent } from './company-create-team/company-create-team.component';
import { CompanySearchCandidatesComponent } from './company-search-candidates/company-search-candidates.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { CompanySearchParamsComponent } from './company-search-params/company-search-params.component';
import { CompanySearchPreselectComponent } from './company-search-preselect/company-search-preselect.component';
import { CompanyHiredComponent } from './company-hired/company-hired.component';
import { CompanyHiredEvaluationComponent } from './company-hired-evaluation/company-hired-evaluation.component';
import { DragScrollModule } from 'ngx-drag-scroll';
import { CompanyCreateProjectComponent } from './company-create-project/company-create-project.component';
import { CompanyPositionsComponent } from './company-positions/company-positions.component';
import { CompanyCreatePositionComponent } from './company-create-position/company-create-position.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MatFormFieldModule,
    MatIconModule,
    MatDividerModule,
    MatInputModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    FormsModule,
    MatChipsModule,
    MatDialogModule,
    MatAutocompleteModule,
    MatSelectModule,
    SharedModule,
    DragScrollModule,
    NgxPaginationModule
  ],
  declarations: [
    CompanyLoginComponent,
    CompanyRegisterComponent,
    CompanyLoginComponent,
    CompanyHomeComponent,
    CompanyPeopleComponent,
    CompanyCreateEmployeeComponent,
    CompanyTeamsProjectsComponent,
    CompanyCreateTeamComponent,
    CompanyCreateProjectComponent,
    CompanySearchCandidatesComponent,
    CompanySearchParamsComponent,
    CompanySearchPreselectComponent,
    CompanyHiredComponent,
    CompanyHiredEvaluationComponent,
    CompanyPositionsComponent,
    CompanyCreatePositionComponent,
  ],
  exports: [
    CompanyLoginComponent,
    CompanyRegisterComponent,
    CompanyLoginComponent,
    CompanyHomeComponent,
    CompanyPeopleComponent,
    CompanyCreateEmployeeComponent,
    CompanyTeamsProjectsComponent,
    CompanyCreateTeamComponent,
    CompanyCreateProjectComponent,
    CompanySearchCandidatesComponent,
    CompanySearchParamsComponent,
    CompanySearchPreselectComponent,
    CompanyHiredComponent,
    CompanyHiredEvaluationComponent,
    CompanyPositionsComponent,
    CompanyCreatePositionComponent,
  ]
})
export class CompanyModule { }
