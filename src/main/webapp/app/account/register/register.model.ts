export class Registration {
  constructor(
    public login: string,
    public email: string,
    public password: string,
    public langKey: string,
    public telephone: string,
    public adresse: string,
    public cv: string | null,
    public cvContentType: string | null,
    public type: string | null,
    public nomEntreprise: string | null,
    public secteur: string | null,
  
  ) {}
}
