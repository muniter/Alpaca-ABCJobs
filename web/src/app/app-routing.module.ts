import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginCompanyFormComponent } from './login-company/login-company-form/login-company-form.component';

const routes: Routes = [
  { path: '', component: LoginCompanyFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

