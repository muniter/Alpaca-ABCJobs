import { Routes, RouterModule } from '@angular/router';
import { AppRoutesEnum } from '../core/enums';
import { TechnicalTestCandidateComponent } from './technical-test-candidate/technical-test-candidate.component';
import { NgModule } from '@angular/core';

const routes: Routes = [
  { 
    path: AppRoutesEnum.technicalTest,
    children: [
      {
        path: AppRoutesEnum.technicalTestCandidate,
        component: TechnicalTestCandidateComponent
      },
      {
        path: `${AppRoutesEnum.technicalTestCandidate}/:userToken`,
        component: TechnicalTestCandidateComponent
      }
    ]
  }
];

/* export const TechnicalTestRoutingRoutes = RouterModule.forChild(routes); */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class TechnicalTestRoutingModule {}
