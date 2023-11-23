import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteSelectedEvent, MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialogModule } from '@angular/material/dialog';
import { CandidateRegisterComponent } from './candidate-register/candidate-register.component';
import { CandidateLoginComponent } from './candidate-login/candidate-login.component';
import { CandidateHomeComponent } from './candidate-home/candidate-home.component';
import { CandidateEducationComponent } from './candidate-education/candidate-education.component';
import { CandidateProfileComponent } from './candidate-profile/candidate-profile.component';
import { MatIconModule } from '@angular/material/icon';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { CandidateWorkComponent } from './candidate-work/candidate-work.component';
import { CandidateSkillsComponent } from './candidate-skills/candidate-skills.component';
import { CandidateInterviewsComponent } from './candidate-interviews/candidate-interviews.component';
import { DragScrollModule } from 'ngx-drag-scroll';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    MatCheckboxModule,
    MatDividerModule,
    MatIconModule,
    MatAutocompleteModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule, 
    MatCheckboxModule,
    MatDividerModule,
    DragScrollModule,
    MatDialogModule
  ],
  declarations: [
    CandidateRegisterComponent,
    CandidateLoginComponent,
    CandidateHomeComponent, 
    CandidateProfileComponent,
    CandidateEducationComponent,
    CandidateWorkComponent,
    CandidateSkillsComponent,
    CandidateInterviewsComponent
  ],
  exports: [
    CandidateRegisterComponent,
    CandidateLoginComponent,
    CandidateHomeComponent, 
    CandidateProfileComponent,
    CandidateEducationComponent,
    CandidateWorkComponent,
    CandidateSkillsComponent,
    CandidateInterviewsComponent
  ]
})
export class CandidateModule { }
