import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AppRoutesEnum } from '../core/enums';
import { UserSettingsComponent } from './user-settings/user-settings.component';

const routes: Routes = [
  {  
    path: AppRoutesEnum.user,
    children: [
      {
        path: AppRoutesEnum.settings,
        component: UserSettingsComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
