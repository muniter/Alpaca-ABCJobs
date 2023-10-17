export class Company {
    nombre: string;
    email: string;

    public constructor(nombre: string, email: string) {
        this.nombre = nombre;
        this.email = email;
    }
}

export class CompanyRegisterForm extends Company {
    password: string;

    public constructor(nombre: string, email: string, password: string) {
        super(nombre, email);

        this.password = password;
    }
}