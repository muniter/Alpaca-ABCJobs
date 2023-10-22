import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { CompanyModule } from './company/company.module';
import { CompanyRoutingModule } from './company/company-routing.routing';
import { CandidateModule } from './candidate/candidate.module';
import { CandidateRoutingModule } from './candidate/candidate-routing.routing';
import { UserModule } from './user/user.module';
import { UserRoutingModule } from './user/user-routing.routing';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    CompanyModule,
    CompanyRoutingModule,
    CandidateModule,
    CandidateRoutingModule,
    UserModule,
    UserRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
