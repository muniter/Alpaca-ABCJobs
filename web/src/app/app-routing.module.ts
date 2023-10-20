import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppRoutesEnum } from './core/enums';

const routes: Routes = [
  { path: '', redirectTo: `/${AppRoutesEnum.company}/${AppRoutesEnum.companyLogin}`, pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

