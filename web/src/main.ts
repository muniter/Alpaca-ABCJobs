import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
