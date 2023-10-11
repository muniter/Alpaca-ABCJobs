import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LogonCompanyFormComponent } from './logon-company-form/logon-company-form.component';
import { SharedModule } from '../shared/shared.module';
import { RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatCheckboxModule,
    SharedModule
  ],
  declarations: [LogonCompanyFormComponent],
  exports: [LogonCompanyFormComponent]
})
export class LogonCompanyModule { }
