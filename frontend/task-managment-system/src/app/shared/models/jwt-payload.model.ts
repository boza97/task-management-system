export interface JwtPayload {
  sub: string;
  firstName: string;
  lastName: string;
  email: string;
  roles: string[];
  exp: number;
  iat: number;
}
