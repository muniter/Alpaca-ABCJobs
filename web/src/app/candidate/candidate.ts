import { Country } from "../shared/Country";
import { Language } from "../shared/Language";

export class Candidate {
  names: string;
  lastnames: string;
  email: string;

  public constructor(names: string, lastnames: string, email: string) {
    this.names = names;
    this.lastnames = lastnames;
    this.email = email;
  }
}

export class CandidateFormRegister extends Candidate {
  password: string;
  passwordConfirm: string;
  termsCheck: boolean;
  termsCheck2: boolean;

  public constructor(
    names: string,
    lastnames: string,
    email: string,
    password: string,
    passwordConfirm: string,
    termsCheck: boolean,
    termsCheck2: boolean
  ) {
    super(names, lastnames, email);
    this.password = password;
    this.passwordConfirm = passwordConfirm;
    this.termsCheck = termsCheck;
    this.termsCheck2 = termsCheck2;
  }
}

export class CandidateServiceSchema {
  nombres: string;
  apellidos: string;
  email: string;
  password: string;

  public constructor(
    nombres: string,
    apellidos: string,
    email: string,
    password: string
  ) {
    this.nombres = nombres;
    this.apellidos = apellidos;
    this.email = email;
    this.password = password
  }
}

export const mapKeys: { [index: string]: string } = {
  "nombres": "names",
  "apellidos": "lastnames",
  "email": "email",
  "password": "password"
};

export class CandidateLoginRequest {
  email: string;
  password: string;

  public constructor(
    email: string,
    password: string
  ) {
    this.email = email;
    this.password = password;
  }
}

export class PersonalInfo {
  names: string;
  last_names: string;
  full_name: string;
  email: string;
  birth_date: string;
  country_code: Number;
  country: string;
  city: string;
  address: string;
  phone: string;
  biography: string;
  languages: Language[];

  public constructor(
    names: string,
    last_names: string,
    full_name: string,
    email: string,
    birth_date: string,
    country_code: Number,
    country: string,
    city: string,
    address: string,
    phone: string,
    biography: string,
    languages: Language[]
  ) {
    this.names = names;
    this.last_names = last_names;
    this.full_name = full_name;
    this.email = email;
    this.birth_date = birth_date;
    this.country_code = country_code;
    this.country = country;
    this.city = city;
    this.address = address;
    this.phone = phone;
    this.biography = biography;
    this.languages = languages;
  }
  
}

export class PersonalInfoResponse {
  success: boolean;
  data: PersonalInfo;

  public constructor(
    success: boolean,
    data: PersonalInfo
  ) {
    this.success = success,
      this.data = data
  }
}

export class SavePersonalInfoRequest {
  birth_date: string;
  country_code: Number;
  city: string;
  address: string;
  phone: string;
  biography: string;
  languages: string[];

  public constructor(
    birth_date: string,
    country: Country,
    city: string,
    address: string,
    phone: string,
    biography: string,
    languages: Language[]
  ) {
    this.birth_date = birth_date;
    this.country_code = country.num_code;
    this.city = city;
    this.address = address;
    this.phone = phone;
    this.biography = biography;
    this.languages = languages.map(lang => lang.id);
  }
}