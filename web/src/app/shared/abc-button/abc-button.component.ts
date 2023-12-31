import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-abc-button',
  templateUrl: './abc-button.component.html',
  styleUrls: ['./abc-button.component.css']
})
export class AbcButtonComponent implements OnInit {

  @Input() color: string = "primary"; //primary / accent / basic
  @Input() disabled: boolean = false;
  @Input() bordered: boolean = true;
  @Input() type: string = "button"; //button, submit
  @Input() class!: string;
  @Input() routerLink!: string;

  constructor() { }

  ngOnInit() {
  }

}
