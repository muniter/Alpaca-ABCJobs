import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CandidateModule } from './candidate/candidate.module';
import { CommonModule } from '@angular/common';
import { CompanyModule } from './company/company.module';
import { CompanyRoutingModule } from './company/company-routing.routing';
import { CandidateRoutingModule } from './candidate/candidate-routing.routing';

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
    CandidateRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
