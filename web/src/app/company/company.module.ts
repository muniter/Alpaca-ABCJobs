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
    SharedModule
  ],
  declarations: [CompanyLoginComponent, CompanyRegisterComponent, CompanyLoginComponent, CompanyHomeComponent, CompanyPeopleComponent, CompanyCreateEmployeeComponent],
  exports: [CompanyLoginComponent, CompanyRegisterComponent, CompanyLoginComponent, CompanyHomeComponent, CompanyPeopleComponent, CompanyCreateEmployeeComponent]
})
export class CompanyModule { }
