import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'ABCJobs';
  localesList = [
    { code: 'es', label: 'Español' },
    { code: 'en', label: 'English' }
  ];
}
