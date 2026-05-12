import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

export interface Producto { id?: number; nombre: string; precio?: number; tipo?: string; categoria?: any; stockActual?: number; umbralAlerta?: number }

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list() {
    return this.http.get<Producto[]>(`${this.base}/productos`);
  }

  create(payload: any) {
    return this.http.post<Producto>(`${this.base}/productos`, payload);
  }

  update(payload: any) {
    return this.http.put<Producto>(`${this.base}/productos`, payload);
  }

  delete(payload: { id: number }) {
    return this.http.request('delete', `${this.base}/productos`, { body: payload });
  }

  deleteById(id: number) {
    return this.http.delete(`${this.base}/productos/${id}`);
  }
}
