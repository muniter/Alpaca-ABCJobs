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