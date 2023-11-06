import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CompanyLoginComponent } from './company-login/company-login.component';
import { CompanyRegisterComponent } from './company-register/company-register.component';
import { AppRoutesEnum } from '../core/enums';
import { CompanyHomeComponent } from './company-home/company-home.component';
import { CompanyPeopleComponent } from './company-people/company-people.component';
import { CompanyTeamsProjectsComponent } from './company-teams-projects/company-teams-projects.component';

const routes: Routes = [
  {
    path: AppRoutesEnum.company,
    children: [
      {
        path: AppRoutesEnum.companyLogin,
        component: CompanyLoginComponent
      },
      {
        path: AppRoutesEnum.companyRegister,
        component: CompanyRegisterComponent
      },
      {
        path: `${AppRoutesEnum.companyHome}/:userToken`,
        component: CompanyHomeComponent
      },
      {
        path: `${AppRoutesEnum.companyPeople}/:userToken`,
        component: CompanyPeopleComponent
      },
      {
        path: `${AppRoutesEnum.companyTeamsProjects}/:userToken`,
        component: CompanyTeamsProjectsComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class CompanyRoutingModule { }
