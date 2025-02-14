import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../shared/shared.module';

import { DashboardComponent } from './components/dashboard/dashboard.component';
import { StatsCardComponent } from './components/stats-card/stats-card.component';
import { AppointmentChartComponent } from './components/appointment-chart/appointment-chart.component';
import { PatientChartComponent } from './components/patient-chart/patient-chart.component';
import { DoctorChartComponent } from './components/doctor-chart/doctor-chart.component';
import { TimelineChartComponent } from './components/timeline-chart/timeline-chart.component';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent
  }
];

@NgModule({
  declarations: [
    DashboardComponent,
    StatsCardComponent,
    AppointmentChartComponent,
    PatientChartComponent,
    DoctorChartComponent,
    TimelineChartComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild(routes)
  ]
})
export class DashboardModule { }