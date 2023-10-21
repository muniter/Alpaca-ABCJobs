import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { CandidateRegisterComponent } from './candidate-register/candidate-register.component';
import { CandidateHomeComponent } from './candidate-home/candidate-home.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule, 
    MatCheckboxModule 
  ],
  exports: [CandidateRegisterComponent, CandidateHomeComponent],
  declarations: [CandidateRegisterComponent, CandidateHomeComponent]
})
export class CandidateModule { }
