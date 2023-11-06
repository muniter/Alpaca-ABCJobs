import { Component, OnInit } from '@angular/core';
import { Team } from '../Team';
import { CompanyService } from '../company.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { CompanyCreateTeamComponent } from '../company-create-team/company-create-team.component';

@Component({
  selector: 'app-company-teams-projects',
  templateUrl: './company-teams-projects.component.html',
  styleUrls: ['./company-teams-projects.component.css']
})
export class CompanyTeamsProjectsComponent implements OnInit {

  dialogConfig = new MatDialogConfig();
  modalDialog: MatDialogRef<CompanyCreateTeamComponent, any> | undefined;

  teams!: Team[]
  token: string;

  constructor(
    private companyService: CompanyService,
    private activatedRouter: ActivatedRoute,
    private router: Router,
    public matDialog: MatDialog
  ) {
    this.token = ""
    if (!this.activatedRouter.snapshot.params['userToken']) {
      this.router.navigateByUrl(`${AppRoutesEnum.company}/${AppRoutesEnum.companyLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }
  }

  ngOnInit() {
    this.loadTeams();
  }


  private loadTeams() {
    this.teams = [];

    this.companyService.getTeams(this.token).subscribe({
      next: (response) => {
        this.teams = response.data;
      }
    });
  }

  public openAddTeamModal() {

    this.dialogConfig.width = "45%";
    this.dialogConfig.data = {
      token: this.token
    }

    this.modalDialog = this.matDialog.open(CompanyCreateTeamComponent, this.dialogConfig);

    this.modalDialog.afterClosed().subscribe(x => this.loadTeams())
  }
}
