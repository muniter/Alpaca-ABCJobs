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
    SharedModule
  ],
  declarations: [CompanyLoginComponent, CompanyRegisterComponent],
  exports: [CompanyLoginComponent, CompanyRegisterComponent]
})
export class CompanyModule { }
