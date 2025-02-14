import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard extends KeycloakAuthGuard {
  constructor(
    protected override router: Router,
    protected override keycloakService: KeycloakService
  ) {
    super(router, keycloakService);
  }

  public async isAccessAllowed(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    // Get the roles required from the route data
    const requiredRoles = route.data['roles'] as Array<string>;

    // Allow the user to proceed if no additional roles are required to access the route
    if (!(requiredRoles instanceof Array) || requiredRoles.length === 0) {
      return true;
    }

    // Allow the user to proceed if all the required roles are present
    if (await this.keycloakService.isLoggedIn()) {
      const userRoles = this.keycloakService.getUserRoles();
      const hasRequiredRoles = requiredRoles.every(role => userRoles.includes(role));
      
      if (!hasRequiredRoles) {
        await this.router.navigate(['unauthorized']);
        return false;
      }
      
      return true;
    }

    // Redirect to login if not authenticated
    await this.router.navigate(['auth/login']);
    return false;
  }
}