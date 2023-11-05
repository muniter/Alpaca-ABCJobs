import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-team-project-card',
  templateUrl: './team-project-card.component.html',
  styleUrls: ['./team-project-card.component.css']
})
export class TeamProjectCardComponent implements OnInit {

  @Input() title: string = "";
  @Input() subtitle: string = "";
  @Input() type: string = ""; //add //team //project
  
  constructor() { }

  ngOnInit() {
  }

}
