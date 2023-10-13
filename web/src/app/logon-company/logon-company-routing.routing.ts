import { Routes, RouterModule } from '@angular/router';
import { LogonCompanyFormComponent } from './logon-company-form/logon-company-form.component';
import { NgModule } from '@angular/core';

const routes: Routes = [
  {
    path: 'company-logon',
    children: [
      {
        path: '',
        component: LogonCompanyFormComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LogonCompanyRoutingModule { }
