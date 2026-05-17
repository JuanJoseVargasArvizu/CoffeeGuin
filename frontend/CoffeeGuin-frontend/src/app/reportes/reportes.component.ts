import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import {
  AnaliticaGraficaDTO,
  ReporteService,
  VentaDetalleDTO,
  VentaResumenDTO,
} from '../services/reporte.service';

type PestañaReportes = 'historial' | 'pdfs' | 'analitica';
type TipoReportePdf = 'DIARIO' | 'SEMANAL' | 'MENSUAL';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.css',
})
export class ReportesComponent implements OnInit {
  private readonly reporteService = inject(ReporteService);

  pestanaActiva: PestañaReportes = 'historial';

  historialVentas: VentaResumenDTO[] = [];
  historialFechaInicio = '';
  historialFechaFin = '';
  historialCargando = false;
  historialError = '';

  detalleAbierto = false;
  detalleCargando = false;
  detalleError = '';
  ventaSeleccionada: VentaResumenDTO | null = null;
  detalleVenta: VentaDetalleDTO[] = [];

  pdfCargando = false;
  pdfError = '';

  analiticaFechaInicio = '';
  analiticaFechaFin = '';
  analiticaCriterio: 'PRODUCTO' | 'CATEGORIA' = 'PRODUCTO';
  analiticaCargando = false;
  analiticaError = '';
  analitica: AnaliticaGraficaDTO | null | undefined = null;
  totalUnidadesPeriodo = 0;

  ngOnInit(): void {
    const { inicio, fin } = this.obtenerRangoMesActual();
    this.historialFechaInicio = inicio;
    this.historialFechaFin = fin;
    this.analiticaFechaInicio = inicio;
    this.analiticaFechaFin = fin;

    this.cargarHistorialVentas();
    this.cargarAnalitica();
  }

  cambiarPestana(pestana: PestañaReportes): void {
    this.pestanaActiva = pestana;
  }

  cargarHistorialVentas(): void {
    const { inicio, fin } = this.normalizarRango(this.historialFechaInicio, this.historialFechaFin);
    this.historialFechaInicio = inicio;
    this.historialFechaFin = fin;
    this.historialCargando = true;
    this.historialError = '';

    this.reporteService
      .obtenerHistorialVentas(inicio, fin)
      .pipe(finalize(() => (this.historialCargando = false)))
      .subscribe({
        next: (ventas) => {
          this.historialVentas = ventas ?? [];
        },
        error: () => {
          this.historialVentas = [];
          this.historialError = 'No se pudo cargar el historial de ventas.';
        },
      });
  }

  abrirDetalle(venta: VentaResumenDTO): void {
    if (!venta?.idVenta) {
      return;
    }

    this.detalleAbierto = true;
    this.ventaSeleccionada = venta;
    this.detalleVenta = [];
    this.detalleError = '';
    this.detalleCargando = true;

    this.reporteService
      .obtenerDetalleVenta(venta.idVenta)
      .pipe(finalize(() => (this.detalleCargando = false)))
      .subscribe({
        next: (detalle) => {
          this.detalleVenta = detalle ?? [];
        },
        error: () => {
          this.detalleVenta = [];
          this.detalleError = 'No se pudo cargar el detalle de la venta.';
        },
      });
  }

  cerrarDetalle(): void {
    this.detalleAbierto = false;
    this.ventaSeleccionada = null;
    this.detalleVenta = [];
    this.detalleError = '';
    this.detalleCargando = false;
  }

  descargarReporte(tipo: TipoReportePdf): void {
    this.pdfCargando = true;
    this.pdfError = '';

    this.reporteService
      .descargarReportePdf(tipo)
      .pipe(finalize(() => (this.pdfCargando = false)))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const enlace = document.createElement('a');
          enlace.href = url;
          enlace.download = `reporte-financiero-${tipo.toLowerCase()}.pdf`;
          enlace.rel = 'noopener';
          document.body.appendChild(enlace);
          enlace.click();
          enlace.remove();
          window.setTimeout(() => window.URL.revokeObjectURL(url), 1000);
        },
        error: () => {
          this.pdfError = 'No se pudo descargar el reporte PDF.';
        },
      });
  }

  cargarAnalitica(): void {
    const { inicio, fin } = this.normalizarRango(this.analiticaFechaInicio, this.analiticaFechaFin);
    this.analiticaFechaInicio = inicio;
    this.analiticaFechaFin = fin;
    this.analiticaCargando = true;
    this.analiticaError = '';

    this.reporteService
      .obtenerAnalitica(this.analiticaCriterio, inicio, fin)
      .pipe(finalize(() => (this.analiticaCargando = false)))
      .subscribe({
        next: (resultado) => {
          this.analitica = resultado;
          this.totalUnidadesPeriodo = this.calcularTotalUnidadesPeriodo(resultado?.seriesCantidad);
        },
        error: () => {
          this.analitica = null;
          this.totalUnidadesPeriodo = 0;
          this.analiticaError = 'No se pudo cargar la analítica.';
        },
      });
  }

  alturaBarra(indice: number): number {
    const cantidad = this.cantidadAnalitica(indice);

    if (this.totalUnidadesPeriodo <= 0 || cantidad <= 0) {
      return 0;
    }

    return (cantidad / this.totalUnidadesPeriodo) * 100;
  }

  cantidadAnalitica(indice: number): number {
    return this.analitica?.seriesCantidad?.[indice] ?? 0;
  }

  ingresosAnalitica(indice: number): number {
    return this.analitica?.seriesIngresos?.[indice] ?? 0;
  }

  esEtiquetaLarga(etiqueta: string): boolean {
    return etiqueta.length > 14;
  }

  etiquetaVenta(venta: VentaResumenDTO): string {
    const mesa = venta.numeroMesa != null ? `Mesa ${venta.numeroMesa}` : 'Sin mesa';
    const cliente = venta.nombreCliente ? ` - ${venta.nombreCliente}` : '';
    return `${mesa}${cliente}`;
  }

  private obtenerRangoMesActual(): { inicio: string; fin: string } {
    const hoy = new Date();
    const inicio = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
    return {
      inicio: this.formatearFechaInput(inicio),
      fin: this.formatearFechaInput(hoy),
    };
  }

  private normalizarRango(inicio: string, fin: string): { inicio: string; fin: string } {
    if (inicio && fin && inicio > fin) {
      return { inicio: fin, fin: inicio };
    }

    return { inicio, fin };
  }

  private calcularTotalUnidadesPeriodo(seriesCantidad?: number[]): number {
    return (seriesCantidad ?? []).reduce((total, cantidad) => total + (Number(cantidad) || 0), 0);
  }

  private formatearFechaInput(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }
}