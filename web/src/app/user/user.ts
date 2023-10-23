export class User {
}

export class UserSettingsDetail {
  languageApp: string;
  timeFormat: string;
  dateFormat: string;
  
  public constructor(
    languageApp: string, 
    timeFormat: string,
    dateFormat: string
  ) {
    this.languageApp = languageApp;
    this.timeFormat = timeFormat;
    this.dateFormat = dateFormat;
  }
}

export class UserConfig {
  config: UserSettingsDetail;

  public constructor(
    config: UserSettingsDetail
  ) {
    this.config = config;
  }
}

export class UserSettings {
  success: boolean;
  data: UserConfig;

  public constructor(
    success: boolean,
    data: UserConfig
  ) {
    this.success = success;
    this.data = data;
  }
}


