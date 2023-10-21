import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-candidate-header',
  templateUrl: './candidate-header.component.html',
  styleUrls: ['./candidate-header.component.css']
})
export class CandidateHeaderComponent implements OnInit {

  @Input() username: string = ""; 

  constructor() { }

  ngOnInit() {
  }

}
