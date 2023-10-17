import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CompanyLoginComponent } from './company-login/company-login.component';
import { CompanyRegisterComponent } from './company-register/company-register.component';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'company-login',
        component: CompanyLoginComponent
      },
      {
        path: 'company-register',
        component: CompanyRegisterComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class CompanyRoutingModule { }
