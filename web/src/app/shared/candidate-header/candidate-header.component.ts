import { Component, Input, OnInit } from '@angular/core';
import { AppRoutesEnum } from 'src/app/core/enums';

@Component({
  selector: 'app-candidate-header',
  templateUrl: './candidate-header.component.html',
  styleUrls: ['./candidate-header.component.css']
})
export class CandidateHeaderComponent implements OnInit {

  @Input() username: string = ""; 

  appRoutes = AppRoutesEnum;

  constructor() { }

  ngOnInit() {
  }

}
