import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-unlogged-lang-picker',
  templateUrl: './unlogged-lang-picker.component.html',
  styleUrls: ['./unlogged-lang-picker.component.css']
})
export class UnloggedLangPickerComponent implements OnInit {

  @Input() route: string = "";

  constructor() { }

  ngOnInit() { }

}
