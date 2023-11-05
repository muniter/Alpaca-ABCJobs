import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TechnicalTestCandidateComponent } from './technical-test-candidate/technical-test-candidate.component';
import { SharedModule } from '../shared/shared.module';
import { DragScrollModule } from 'ngx-drag-scroll';
import { MatIconModule } from '@angular/material/icon';
import { TechnicalTestExamComponent } from './technical-test-exam/technical-test-exam.component';
@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    DragScrollModule,
    MatIconModule
  ],
  declarations: [
    TechnicalTestCandidateComponent,
    TechnicalTestExamComponent
  ],
  exports: [
    TechnicalTestCandidateComponent,
    TechnicalTestExamComponent
  ]
})
export class TechnicalTestModule { }
