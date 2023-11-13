import { da } from "@faker-js/faker";
import { Country } from "../shared/Country";
import { Language } from "../shared/Language";
import { Skill } from "../shared/skill";

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
  names: string | null;
  last_names: string | null;
  full_name: string | null;
  email: string | null;
  birth_date: string | null;
  country_code: Number | null;
  country: string | null;
  city: string | null;
  address: string | null;
  phone: string | null;
  biography: string | null;
  languages: Language[] | null;

  public constructor(
    names: string | null,
    last_names: string | null,
    full_name: string | null,
    email: string | null,
    birth_date: string | null,
    country_code: Number | null,
    country: string | null,
    city: string | null,
    address: string | null,
    phone: string | null,
    biography: string | null,
    languages: Language[] | null
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
  birth_date: string | null;
  country_code: Number | null;
  city: string | null;
  address: string | null;
  phone: string | null;
  biography: string | null;
  languages: string[] | null;

  public constructor(
    birth_date: string | null,
    country: Country | null,
    city: string | null,
    address: string | null,
    phone: string | null,
    biography: string | null,
    languages: Language[] | null
  ) {
    this.birth_date = birth_date;
    this.country_code = country ? country.num_code : null;
    this.city = city;
    this.address = address;
    this.phone = phone;
    this.biography = biography;
    this.languages = languages ? languages.map(lang => lang.id) : null;
  }
}

export class CandidateSearch {
  id: number;
  nombres: string;
  apellidos: string;
  email: string;
  country: Country;
  city: string;
  skills: Skill[];
  skills_related: Skill[];
  technical_info_types: Skill[];
  technical_info_types_related: Skill[];
  languages: Language[];
  languages_related: Language[];

  public constructor(
    id: number,
    nombres: string,
    apellidos: string,
    email: string,
    country: Country,
    city: string,
    skills: Skill[],
    skills_related: Skill[],
    technical_info_types: Skill[],
    technical_info_types_related: Skill[],
    languages: Language[],
    languages_related: Language[]
  ) {
    this.id = id;
    this.nombres = nombres;
    this.apellidos = apellidos;
    this.email = email;
    this.country = country;
    this.city = city;
    this.skills = skills;
    this.skills_related = skills_related;
    this.technical_info_types = technical_info_types;
    this.technical_info_types_related = technical_info_types_related;
    this.languages = languages;
    this.languages_related = languages_related;
  }

}

export class CandidateSearchResponse {
  success: boolean;
  data: CandidateSearch[]

  public constructor(
    success: boolean,
    data: CandidateSearch[]
  ) {
    this.success = success;
    this.data = data
  }
}
