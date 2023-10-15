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
