import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbcButtonComponent } from './abc-button/abc-button.component';
import { MatButtonModule } from '@angular/material/button';
import { LoginLogonContainerComponent } from './login-logon-container/login-logon-container.component';
import { FormHeaderComponent } from './form-header/form-header.component';
import { MatDividerModule } from '@angular/material/divider';

@NgModule({
  imports: [
    CommonModule,
    MatButtonModule,
    MatDividerModule
  ],
  declarations: [AbcButtonComponent, LoginLogonContainerComponent, FormHeaderComponent],
  exports: [AbcButtonComponent, LoginLogonContainerComponent, FormHeaderComponent]
})
export class SharedModule { }
