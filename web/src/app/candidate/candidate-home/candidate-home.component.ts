import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-candidate-home',
  templateUrl: './candidate-home.component.html',
  styleUrls: ['./candidate-home.component.css']
})
export class CandidateHomeComponent implements OnInit {

  userFullName: string = "Maria Camila López";

  constructor() { }

  ngOnInit() {
  }

}
