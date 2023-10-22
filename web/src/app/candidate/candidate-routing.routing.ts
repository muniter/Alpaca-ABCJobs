import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppRoutesEnum } from '../core/enums';
import { CandidateRegisterComponent } from './candidate-register/candidate-register.component';
import { CandidateLoginComponent } from './candidate-login/candidate-login.component';
import { CandidateHomeComponent } from './candidate-home/candidate-home.component';
import { CandidateProfileComponent } from './candidate-profile/candidate-profile.component';

const routes: Routes = [
  {
    path: AppRoutesEnum.candidate,
    children: [
      {
        path: AppRoutesEnum.candidateRegister,
        component: CandidateRegisterComponent
      },
      {
        path: AppRoutesEnum.candidateLogin,
        component: CandidateLoginComponent
      },
      { 
        path: AppRoutesEnum.candidateHome,
        component: CandidateHomeComponent
      },
      { 
        path: `${AppRoutesEnum.candidateHome}/:userToken`,
        component: CandidateHomeComponent
      },
      {
        path: AppRoutesEnum.candidateProfile,
        component: CandidateProfileComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class CandidateRoutingModule {}
