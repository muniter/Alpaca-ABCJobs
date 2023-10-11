import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { LoginCompanyFormComponent } from './login-company-form/login-company-form.component';

const routes: Routes = [
  {
    path: 'company-login',
    children: [
      {
        path: '',
        component: LoginCompanyFormComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LoginCompanyRoutingModule { }
