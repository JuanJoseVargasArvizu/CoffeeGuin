import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export type EstadoMesa = 'Libre' | 'Ocupada' | 'Reservada' | string;

export interface Mesa {
  id?: number;
  numero: number;
  estado: EstadoMesa;
  cantidadAsientos: number;
  asientos?: Array<{ numero: number; ocupado: boolean; id: number }>;
}

export interface ActualizarAsientosResponse {
  id: number;
  numero: number;
  mensaje: string;
  nuevaCantidadAsientos: number;
}

@Injectable({
  providedIn: 'root'
})
export class MesaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/mesas`;

  list(): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(this.apiUrl);
  }

  listarPorEstado(estado: string): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(`${this.apiUrl}/${encodeURIComponent(estado)}`);
  }

  create(mesa: Mesa): Observable<Mesa> {
    return this.http.post<Mesa>(this.apiUrl, mesa);
  }

  actualizarAsientos(id: number, cantidad: number): Observable<ActualizarAsientosResponse> {
    const params = new HttpParams().set('cantidad', cantidad.toString());

    return this.http.put<ActualizarAsientosResponse>(`${this.apiUrl}/${id}/asientos`, null, { params });
  }

  actualizarEstado(id: number, estado: string): Observable<Mesa> {
      // Configura el query param para que se vea como ?nuevoEstado=Ocupada
      const params = new HttpParams().set('nuevoEstado', estado);

      // El segundo argumento es el BODY. Como este endpoint no usa body, pasamos null.
      return this.http.put<Mesa>(`${this.apiUrl}/${id}/estado`, null, { params });
  }
}