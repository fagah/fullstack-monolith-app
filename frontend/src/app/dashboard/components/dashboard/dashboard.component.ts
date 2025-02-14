import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardStats } from '../../models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  template: `
    <div class="container mx-auto px-4 py-6">
      <h1 class="text-3xl font-bold text-gray-900 mb-8">Dashboard</h1>
      
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <app-stats-card
          title="Total Patients"
          [value]="stats?.totalPatients || 0"
          trend="+5.2%"
          icon="users"
          color="blue"></app-stats-card>
        
        <app-stats-card
          title="Total Doctors"
          [value]="stats?.totalDoctors || 0"
          trend="+2.1%"
          icon="user-md"
          color="green"></app-stats-card>
        
        <app-stats-card
          title="Appointments Today"
          [value]="stats?.todayAppointments || 0"
          trend="+12.5%"
          icon="calendar"
          color="purple"></app-stats-card>
        
        <app-stats-card
          title="Revenue This Month"
          [value]="stats?.monthlyRevenue || 0"
          trend="+8.4%"
          icon="dollar-sign"
          color="yellow"
          isCurrency="true"></app-stats-card>
      </div>

      <!-- Charts -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <div class="bg-white rounded-lg shadow p-6">
          <h2 class="text-xl font-semibold mb-4">Appointments Overview</h2>
          <app-appointment-chart
            [data]="stats?.appointmentStats"
            class="h-64"></app-appointment-chart>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <h2 class="text-xl font-semibold mb-4">Patient Demographics</h2>
          <app-patient-chart
            [data]="stats?.patientDemographics"
            class="h-64"></app-patient-chart>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="bg-white rounded-lg shadow p-6">
          <h2 class="text-xl font-semibold mb-4">Doctor Specialties</h2>
          <app-doctor-chart
            [data]="stats?.doctorSpecialties"
            class="h-64"></app-doctor-chart>
        </div>

        <div class="bg-white rounded-lg shadow p-6">
          <h2 class="text-xl font-semibold mb-4">Appointments Timeline</h2>
          <app-timeline-chart
            [data]="stats?.appointmentTimeline"
            class="h-64"></app-timeline-chart>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      :host {
        display: block;
        min-height: 100vh;
        background-color: #f9fafb;
      }
    `
  ]
})
export class DashboardComponent implements OnInit {
  stats?: DashboardStats;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.loadDashboardStats();
  }

  private loadDashboardStats(): void {
    this.dashboardService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
      }
    });
  }
}