import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakProfile } from 'keycloak-js';
import { from, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private keycloak: KeycloakService) {}

  init(): Promise<boolean> {
    return this.keycloak.init({
      config: {
        url: 'http://localhost:8180',
        realm: 'mobidoc-realm',
        clientId: 'mobidoc-client'
      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false,
        silentCheckSsoRedirectUri: 
          window.location.origin + '/assets/silent-check-sso.html'
      },
      enableBearerInterceptor: true,
      bearerPrefix: 'Bearer',
      bearerExcludedUrls: [
        '/assets'
      ]
    });
  }

  login(): Promise<void> {
    return this.keycloak.login();
  }

  logout(): Promise<void> {
    return this.keycloak.logout(window.location.origin);
  }

  isLoggedIn(): Promise<boolean> {
    return this.keycloak.isLoggedIn();
  }

  getUsername(): string {
    return this.keycloak.getUsername();
  }

  getUserProfile(): Observable<KeycloakProfile> {
    return from(this.keycloak.loadUserProfile());
  }

  getRoles(): string[] {
    return this.keycloak.getUserRoles();
  }

  hasRole(role: string): boolean {
    return this.keycloak.isUserInRole(role);
  }

  getToken(): Observable<string> {
    return from(this.keycloak.getToken());
  }

  updateToken(minValidity: number = 5): Observable<boolean> {
    return from(this.keycloak.updateToken(minValidity)).pipe(
      map(() => true)
    );
  }
}