import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, from, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Check if the request is to an excluded URL
    if (this.isExcludedUrl(request.url)) {
      return next.handle(request);
    }

    return from(this.authService.getToken()).pipe(
      switchMap(token => {
        if (token) {
          request = request.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          });
        }
        return next.handle(request);
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token might be expired, try to refresh
          return from(this.authService.updateToken()).pipe(
            switchMap(updated => {
              if (updated) {
                // Retry the request with the new token
                return from(this.authService.getToken()).pipe(
                  switchMap(newToken => {
                    const newRequest = request.clone({
                      setHeaders: {
                        Authorization: `Bearer ${newToken}`
                      }
                    });
                    return next.handle(newRequest);
                  })
                );
              } else {
                // Couldn't refresh token, redirect to login
                this.router.navigate(['/auth/login']);
                return throwError(() => error);
              }
            })
          );
        }
        return throwError(() => error);
      })
    );
  }

  private isExcludedUrl(url: string): boolean {
    const excludedUrls = [
      '/assets/',
      '/auth/login',
      // Add other URLs that don't need authentication
    ];
    return excludedUrls.some(excludedUrl => url.includes(excludedUrl));
  }
}