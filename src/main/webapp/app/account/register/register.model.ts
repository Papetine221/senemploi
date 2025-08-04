export class Registration {
  constructor(
    public login: string,
    public email: string,
    public password: string,
    public langKey: string,
    public telephone: string,
    public adresse: string,
    public cv: string,
    public type: string | null,
  ) {}
}
