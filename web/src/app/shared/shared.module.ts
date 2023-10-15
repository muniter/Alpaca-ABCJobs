import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbcButtonComponent } from './abc-button/abc-button.component';
import { MatButtonModule } from '@angular/material/button';
import { LoginLogonContainerComponent } from './login-logon-container/login-logon-container.component';
import { FormHeaderComponent } from './form-header/form-header.component';
import { MatDividerModule } from '@angular/material/divider';
import { RouterModule } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { UnloggedLangPickerComponent } from './unlogged-lang-picker/unlogged-lang-picker.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatDividerModule,
    MatSelectModule
  ],
  declarations: [AbcButtonComponent, LoginLogonContainerComponent, FormHeaderComponent, UnloggedLangPickerComponent],
  exports: [AbcButtonComponent, LoginLogonContainerComponent, FormHeaderComponent, UnloggedLangPickerComponent]
})
export class SharedModule { }
