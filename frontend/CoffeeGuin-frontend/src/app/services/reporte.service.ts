import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface VentaResumenDTO {
  idVenta: number;
  fecha: string;
  subtotal: number;
  totalFinal: number;
  numeroMesa?: number;
  nombreCliente?: string;
}

export interface VentaDetalleDTO {
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  importe: number;
}

export interface AnaliticaGraficaDTO {
  labels: string[];
  seriesCantidad: number[];
  seriesIngresos: number[];
}

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  obtenerHistorialVentas(fechaInicio?: string, fechaFin?: string): Observable<VentaResumenDTO[]> {
    let params = new HttpParams();

    if (fechaInicio) {
      params = params.set('fechaInicio', fechaInicio);
    }

    if (fechaFin) {
      params = params.set('fechaFin', fechaFin);
    }

    return this.http.get<VentaResumenDTO[]>(`${this.baseUrl}/api/ventas`, { params });
  }

  obtenerDetalleVenta(id: number): Observable<VentaDetalleDTO[]> {
    return this.http.get<VentaDetalleDTO[]>(`${this.baseUrl}/api/ventas/${id}/detalle`);
  }

  descargarReportePdf(tipo: 'DIARIO' | 'SEMANAL' | 'MENSUAL'): Observable<Blob> {
    const params = new HttpParams().set('tipo', tipo);

    return this.http.get(`${this.baseUrl}/api/reportes/financiero/pdf`, {
      params,
      responseType: 'blob',
    });
  }

  obtenerAnalitica(
    criterio: 'PRODUCTO' | 'CATEGORIA',
    inicio: string,
    fin: string,
  ): Observable<AnaliticaGraficaDTO> {
    const params = new HttpParams()
      .set('criterio', criterio)
      .set('fechaInicio', inicio)
      .set('fechaFin', fin);

    return this.http.get<AnaliticaGraficaDTO>(`${this.baseUrl}/api/reportes/analitica`, { params });
  }
}