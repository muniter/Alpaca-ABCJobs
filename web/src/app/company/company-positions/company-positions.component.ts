import { Component, OnInit } from '@angular/core';
import { Position } from '../Position';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { CompanyService } from '../company.service';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { CompanyCreateTeamComponent } from '../company-create-team/company-create-team.component';
import { CompanyCreatePositionComponent } from '../company-create-position/company-create-position.component';
import { CompanyPositionDetailComponent } from '../company-position-detail/company-position-detail.component';

@Component({
  selector: 'app-company-positions',
  templateUrl: './company-positions.component.html',
  styleUrls: ['./company-positions.component.css']
})
export class CompanyPositionsComponent implements OnInit {

  dialogConfig = new MatDialogConfig();
  modalPositionCreateDialog: MatDialogRef<CompanyCreatePositionComponent, any> | undefined;
  modalPositionDetailDialog: MatDialogRef<CompanyPositionDetailComponent, any> | undefined;

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

    this.modalPositionCreateDialog = this.matDialog.open(CompanyCreatePositionComponent, this.dialogConfig);

    this.modalPositionCreateDialog.afterClosed().subscribe(x => this.loadPositions())
  }

  public openPositionDetailModal(position: Position) {

    this.dialogConfig.width = "45%";
    this.dialogConfig.data = {
      token: this.token,
      position: position
    }

    this.modalPositionDetailDialog = this.matDialog.open(CompanyPositionDetailComponent, this.dialogConfig);

    this.modalPositionDetailDialog.afterClosed().subscribe(x => this.loadPositions())
  }
}
