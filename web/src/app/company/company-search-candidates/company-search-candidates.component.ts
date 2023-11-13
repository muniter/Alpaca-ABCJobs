import { Component, HostListener, Input, OnInit } from '@angular/core';
import { CandidateSearch } from 'src/app/candidate/candidate';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { MatDialog } from '@angular/material/dialog';
import { CompanySearchPreselectComponent } from '../company-search-preselect/company-search-preselect.component';

@Component({
  selector: 'app-company-search-candidates',
  templateUrl: './company-search-candidates.component.html',
  styleUrls: ['./company-search-candidates.component.css']
})
export class CompanySearchCandidatesComponent implements OnInit {

  @Input() candidates: CandidateSearch[] = [];
  token: string = "";
  page: number = 1;
  itemsInPage: number = 4;

  constructor(
    private activatedRouter: ActivatedRoute,
    private router: Router,
    public dialog: MatDialog,
  ) { 
    this.validateToken(this.activatedRouter.snapshot.params['userToken']);
  }

  validateToken(token:string) {
    this.token = "";
    if (!token) {
      this.router.navigateByUrl(`${AppRoutesEnum.company}/${AppRoutesEnum.companyLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }
  }

  ngOnInit() {
    
    this.onResize();
  }

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.itemsInPage = (window.innerWidth > 1500) ? 9 : 4;
  }

  showDialogInfo(candidateId: number) {
    const dialog = this.dialog.open(CompanySearchPreselectComponent, {
      data: { token: this.token, dialog: this.dialog, theme: 'company-theme', candidateId: candidateId },
      width: '40%'
    });
  }

}
