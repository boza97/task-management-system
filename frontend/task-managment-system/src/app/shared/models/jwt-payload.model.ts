export interface JwtPayload {
  sub: string;
  firstName: string;
  lastName: string;
  roles: string[];
  exp: number;
  iat: number;
}
