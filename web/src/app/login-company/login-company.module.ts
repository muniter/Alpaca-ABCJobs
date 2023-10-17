import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginCompanyFormComponent } from './login-company-form/login-company-form.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { SharedModule } from '../shared/shared.module';
import { RouterModule } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MatFormFieldModule,
    MatDividerModule,
    MatInputModule,
    SharedModule
  ],
  declarations: [
    LoginCompanyFormComponent
  ],
  exports: [LoginCompanyFormComponent]
})
export class LoginCompanyModule { }
