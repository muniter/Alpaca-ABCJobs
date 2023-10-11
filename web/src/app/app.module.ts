import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginCompanyModule } from './login-company/login-company.module';
import { LogonCompanyModule } from './logon-company/logon-company.module';
import { LoginCompanyRoutingModule } from './login-company/login-company-routing.routing';
import { LogonCompanyRoutingModule } from './logon-company/logon-company-routing.routing';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    LoginCompanyModule,
    LoginCompanyRoutingModule,
    LogonCompanyModule,
    LogonCompanyRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
