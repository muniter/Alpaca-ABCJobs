import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbcButtonComponent } from './abc-button/abc-button.component';
import { MatButtonModule } from '@angular/material/button';
import { LoginLogonContainerComponent } from './login-logon-container/login-logon-container.component';
import { FormHeaderComponent } from './form-header/form-header.component';
import { MatDividerModule } from '@angular/material/divider';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { UnloggedLangPickerComponent } from './unlogged-lang-picker/unlogged-lang-picker.component';
import { CandidateHeaderComponent } from './candidate-header/candidate-header.component';
import { CompanyHeaderComponent } from './company-header/company-header.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatDividerModule,
    MatSelectModule,
    MatToolbarModule,
    MatIconModule
  ],
  declarations: [AbcButtonComponent, LoginLogonContainerComponent, FormHeaderComponent, UnloggedLangPickerComponent, CandidateHeaderComponent, CompanyHeaderComponent],
  exports: [AbcButtonComponent, LoginLogonContainerComponent, FormHeaderComponent, UnloggedLangPickerComponent, CandidateHeaderComponent, CompanyHeaderComponent]
})
export class SharedModule { }
