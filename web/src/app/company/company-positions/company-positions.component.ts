import { Component, OnInit } from '@angular/core';
import { Position } from '../Position';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { CompanyService } from '../company.service';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { CompanyCreateTeamComponent } from '../company-create-team/company-create-team.component';
import { CompanyCreatePositionComponent } from '../company-create-position/company-create-position.component';

@Component({
  selector: 'app-company-positions',
  templateUrl: './company-positions.component.html',
  styleUrls: ['./company-positions.component.css']
})
export class CompanyPositionsComponent implements OnInit {

  dialogConfig = new MatDialogConfig();
  modalPositionDialog: MatDialogRef<CompanyCreatePositionComponent, any> | undefined;
  positions!: Position[]
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
    this.loadPositions()
  }

  loadPositions() {
    this.positions = []
    this.companyService.getPositions(this.token).subscribe({
      next: (response) => this.positions = response.data
    })
  }

  public openAddPositionModal() {

    this.dialogConfig.width = "45%";
    this.dialogConfig.data = {
      token: this.token
    }

    this.modalPositionDialog = this.matDialog.open(CompanyCreatePositionComponent, this.dialogConfig);

    this.modalPositionDialog.afterClosed().subscribe(x => this.loadPositions())
  }
}
