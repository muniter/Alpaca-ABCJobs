import { Component, Input, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router, ActivatedRoute } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { UserSettingsComponent } from 'src/app/user/user-settings/user-settings.component';

@Component({
  selector: 'app-candidate-header',
  templateUrl: './candidate-header.component.html',
  styleUrls: ['./candidate-header.component.css']
})
export class CandidateHeaderComponent implements OnInit {

  @Input() username: string = "";

  appRoutes = AppRoutesEnum;
  token: string;

  constructor(
    public dialog: MatDialog,
    private activatedRouter: ActivatedRoute,
    private router: Router
  ) {
    this.token = "";
  }

  ngOnInit() {
    if(!this.activatedRouter.snapshot.params['userToken']) {
      this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }
  }

  openUserSettingsDialog() {
    this.dialog.open(UserSettingsComponent, {
      data: { token: this.token, dialog: this.dialog, theme: 'candidate-theme'},
      width: '40%'
    });
  }

  openTest() {
    this.router.navigateByUrl(`${AppRoutesEnum.technicalTest}/${AppRoutesEnum.technicalTestCandidate}/${this.token}`)
  }

}
