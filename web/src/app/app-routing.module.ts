import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppRoutesEnum } from './core/enums';
import { CandidateRegisterComponent } from './candidate/candidate-register/candidate-register.component';

const routes: Routes = [
  { path: '', redirectTo: '/company-login', pathMatch: 'full' },
  //{ path: 'candidate-register', component: CandidateRegisterComponent, pathMatch: 'full' }
  {
    path: AppRoutesEnum.candidateRegister,
    component: CandidateRegisterComponent, 
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

