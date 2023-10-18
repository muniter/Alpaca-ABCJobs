export class Company {
    nombre: string;
    email: string;

    public constructor(nombre: string, email: string) {
        this.nombre = nombre;
        this.email = email;
    }
}

export class CompanyRegisterRequest extends Company {
    password: string;

    public constructor(nombre: string, email: string, password: string) {
        super(nombre, email);

        this.password = password;
    }
}

export class CompanyLoginRequest {
    email: string;
    password: string;

    public constructor(email: string, password: string) {
        this.email = email;
        this.password = password;
    }
}