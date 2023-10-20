import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppRoutesEnum } from '../core/enums';
import { CandidateRegisterComponent } from './candidate-register/candidate-register.component';
import { CandidateLoginComponent } from './candidate-login/candidate-login.component';

const routes: Routes = [
  {
    path: AppRoutesEnum.candidate,
    children: [
      {
        path: AppRoutesEnum.candidateRegister,
        component: CandidateRegisterComponent, 
      },
      {
        path: AppRoutesEnum.candidateLogin,
        component: CandidateLoginComponent, 
      }
    ]
  },
  {
    path: AppRoutesEnum.user,
    children: [
      {
        path: AppRoutesEnum.login,
        component: CandidateLoginComponent, 
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class CandidateRoutingModule {}
