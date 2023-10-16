import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginCompanyModule } from './login-company/login-company.module';
import { LogonCompanyModule } from './logon-company/logon-company.module';
import { LoginCompanyRoutingModule } from './login-company/login-company-routing.routing';
import { LogonCompanyRoutingModule } from './logon-company/logon-company-routing.routing';
import { CandidateModule } from './candidate/candidate.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    LoginCompanyModule,
    LoginCompanyRoutingModule,
    LogonCompanyModule,
    LogonCompanyRoutingModule,
    CandidateModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
