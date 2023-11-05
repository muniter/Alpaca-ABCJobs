import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-person-card',
  templateUrl: './person-card.component.html',
  styleUrls: ['./person-card.component.css']
})
export class PersonCardComponent implements OnInit {

  @Input() title: string = $localize`:@@insertPeople:Ingresar personal`;
  @Input() type: string = ""; //add / employee

  constructor() { }

  ngOnInit() {
  }

}
