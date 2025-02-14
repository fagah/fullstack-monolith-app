import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import * as d3 from 'd3';
import { AppointmentStats } from '../../models/dashboard.model';

@Component({
  selector: 'app-appointment-chart',
  template: `
    <div #chartContainer class="w-full h-full"></div>
  `,
  styles: [
    `
      :host {
        display: block;
        width: 100%;
        height: 100%;
      }
    `
  ]
})
export class AppointmentChartComponent implements OnInit {
  @ViewChild('chartContainer', { static: true }) chartContainer!: ElementRef;
  @Input() data: AppointmentStats[] = [];

  private svg: any;
  private margin = { top: 20, right: 20, bottom: 30, left: 40 };
  private width = 0;
  private height = 0;
  private colors = ['#60A5FA', '#34D399', '#F87171'];

  ngOnInit(): void {
    this.createChart();
  }

  private createChart(): void {
    // Clear any existing SVG
    d3.select(this.chartContainer.nativeElement).select('svg').remove();

    // Set dimensions
    const element = this.chartContainer.nativeElement;
    this.width = element.offsetWidth - this.margin.left - this.margin.right;
    this.height = element.offsetHeight - this.margin.top - this.margin.bottom;

    // Create SVG
    this.svg = d3.select(element)
      .append('svg')
      .attr('width', element.offsetWidth)
      .attr('height', element.offsetHeight)
      .append('g')
      .attr('transform', `translate(${this.margin.left},${this.margin.top})`);

    // Create scales
    const x = d3.scaleBand()
      .range([0, this.width])
      .padding(0.1);

    const y = d3.scaleLinear()
      .range([this.height, 0]);

    // Set domains
    x.domain(this.data.map(d => d.status));
    y.domain([0, d3.max(this.data, d => d.count) || 0]);

    // Add X axis
    this.svg.append('g')
      .attr('transform', `translate(0,${this.height})`)
      .call(d3.axisBottom(x))
      .selectAll('text')
      .style('text-anchor', 'end')
      .attr('dx', '-.8em')
      .attr('dy', '.15em')
      .attr('transform', 'rotate(-45)');

    // Add Y axis
    this.svg.append('g')
      .call(d3.axisLeft(y));

    // Create bars
    this.svg.selectAll('.bar')
      .data(this.data)
      .enter()
      .append('rect')
      .attr('class', 'bar')
      .attr('x', (d: any) => x(d.status))
      .attr('width', x.bandwidth())
      .attr('y', (d: any) => y(d.count))
      .attr('height', (d: any) => this.height - y(d.count))
      .attr('fill', (d: any, i: number) => this.colors[i % this.colors.length])
      .attr('rx', 4)
      .attr('ry', 4);

    // Add labels
    this.svg.selectAll('.label')
      .data(this.data)
      .enter()
      .append('text')
      .attr('class', 'label')
      .attr('x', (d: any) => x(d.status)! + x.bandwidth() / 2)
      .attr('y', (d: any) => y(d.count) - 5)
      .attr('text-anchor', 'middle')
      .text((d: any) => d.count)
      .style('fill', '#4B5563')
      .style('font-size', '12px');
  }
}