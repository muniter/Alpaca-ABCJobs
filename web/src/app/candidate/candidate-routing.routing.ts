import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppRoutesEnum } from '../core/enums';
import { CandidateRegisterComponent } from './candidate-register/candidate-register.component';
import { CandidateLoginComponent } from './candidate-login/candidate-login.component';
import { CandidateHomeComponent } from './candidate-home/candidate-home.component';
import { CandidateProfileComponent } from './candidate-profile/candidate-profile.component';
import { CandidateInterviewsComponent } from './candidate-interviews/candidate-interviews.component';

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
        path: `${AppRoutesEnum.candidateProfile}/:userToken`,
        component: CandidateProfileComponent
      },
      {
        path: `${AppRoutesEnum.candidateInterviews}/:userToken`,
        component: CandidateInterviewsComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class CandidateRoutingModule {}
