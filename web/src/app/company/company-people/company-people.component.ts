import { Component, OnInit } from '@angular/core';
import { Employee } from '../Employee';
import { CompanyService } from '../company.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { CompanyCreateEmployeeComponent } from '../company-create-employee/company-create-employee.component';

@Component({
  selector: 'app-company-people',
  templateUrl: './company-people.component.html',
  styleUrls: ['./company-people.component.css']
})
export class CompanyPeopleComponent implements OnInit {

  dialogConfig = new MatDialogConfig();
  modalDialog: MatDialogRef<CompanyCreateEmployeeComponent, any> | undefined;

  employees!: Employee[]
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
    this.loadEmployees()
  }

  loadEmployees(){
    this.employees = []
    this.companyService.getEmployees(this.token).subscribe({
      next: (response) => this.employees = response.data
    })
  }

  public openAddModal(){
    
    this.dialogConfig.width = "45%";
    this.dialogConfig.data = {
      token: this.token
    }

    this.modalDialog = this.matDialog.open(CompanyCreateEmployeeComponent, this.dialogConfig);

    this.modalDialog.afterClosed().subscribe(x => this.loadEmployees())
  }
}
 