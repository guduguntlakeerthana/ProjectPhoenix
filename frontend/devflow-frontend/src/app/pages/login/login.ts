import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  email = '';
  password = '';
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login() {

    this.error = '';

    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({

      next: (response) => {

        this.authService.saveToken(response.token);

        alert("Login Successful!");

        this.router.navigate(['/dashboard']);
      },

      error: () => {

        this.error = "Invalid Email or Password";
      }

    });

  }

}
