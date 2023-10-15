import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-unlogged-lang-picker',
  templateUrl: './unlogged-lang-picker.component.html',
  styleUrls: ['./unlogged-lang-picker.component.css']
})
export class UnloggedLangPickerComponent implements OnInit {

  @Input() route: string = "";

  constructor() {
    console.log("route:" + this.route);

    if (this.route.startsWith('en/') || this.route.startsWith('es/')) {
      this.route = this.route.substring(this.route.indexOf("/") + 1);
    }

  }

  ngOnInit() {
  }

}
