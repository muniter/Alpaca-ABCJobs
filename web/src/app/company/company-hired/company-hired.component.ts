import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Employee } from 'src/app/company/Employee';
import { CompanyService } from 'src/app/company/company.service';
import { AppRoutesEnum } from 'src/app/core/enums';
import { CompanyHiredEvaluationComponent } from '../company-hired-evaluation/company-hired-evaluation.component';

@Component({
  selector: 'app-company-hired',
  templateUrl: './company-hired.component.html',
  styleUrls: ['./company-hired.component.css']
})
export class CompanyHiredComponent implements OnInit {

  public token: string;
  public employees: Employee[] = [];
  public date = new Date();
  public page = 1;

  constructor(
    private companyService: CompanyService,
    public matDialog: MatDialog,
    private activatedRouter: ActivatedRoute,
    private router: Router,
  ) {
    this.token = ""
    if (!this.activatedRouter.snapshot.params['userToken']) {
      this.router.navigateByUrl(`${AppRoutesEnum.company}/${AppRoutesEnum.companyLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }
  }

  ngOnInit(): void {
    this.fetchEmployees();
  }

  fetchEmployees() {
    this.companyService.getEmployees(this.token, true).subscribe((response) => {
      this.employees = response.data;
    });
  }

  currentMonthEvaluation(employee: Employee): string {
    const currentMonthEvaluation = employee.evaluations.find((evaluation) => {
      const parts = evaluation.date.split('-');
      const parsedDate = new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
      return parsedDate.getMonth() === this.date.getMonth() && parsedDate.getFullYear() === this.date.getFullYear()
    })
    return currentMonthEvaluation ? currentMonthEvaluation.result.toString() : '';
  }

  evaluacionPendiente(employee: Employee): boolean {
    return this.currentMonthEvaluation(employee) === '';
  }

  evaluacionModal(employee: Employee) {
    const dialogConfig = {
      width: '45%',
      minWidth: '300px',
      data: {
        employee: employee,
        token: this.token,
      }
    };

    this.matDialog.open(CompanyHiredEvaluationComponent, dialogConfig)
      .afterClosed()
      .subscribe(() => {
        this.fetchEmployees();
      });
  }

  employeeTeam(employee: Employee): string {
    let teamName = '';
    if (employee.teams && employee.teams.length > 0) {
      teamName = employee.teams[0].name;
    }
    return teamName;
  }
}
