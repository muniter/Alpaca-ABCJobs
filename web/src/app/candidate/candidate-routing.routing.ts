import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppRoutesEnum } from '../core/enums';
import { CandidateRegisterComponent } from './candidate-register/candidate-register.component';

const routes: Routes = [
  {
    path: AppRoutesEnum.candidate,
    children: [
      {
        path: AppRoutesEnum.candidateRegister,
        component: CandidateRegisterComponent, 
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class CandidateRoutingModule {}
