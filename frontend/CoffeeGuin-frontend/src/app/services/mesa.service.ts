import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export interface Mesa {
  id?: number;
  estado: string;
}

@Injectable({
  providedIn: 'root'
})
export class MesaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/mesas`;

  list(): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(this.apiUrl);
  }

  create(mesa: Mesa): Observable<Mesa> {
    return this.http.post<Mesa>(this.apiUrl, mesa);
  }

  actualizarEstado(id: number, nuevoEstado: string): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.apiUrl}/${id}/estado?nuevoEstado=${nuevoEstado}`, {});
  }
}