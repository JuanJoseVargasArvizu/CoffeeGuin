import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

export interface Categoria { id?: number; nombre: string }

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list() {
    return this.http.get<Categoria[]>(`${this.base}/categorias`);
  }

  create(payload: { nombre: string }) {
    return this.http.post<Categoria>(`${this.base}/categorias`, payload);
  }

  update(payload: { id: number; nombre: string }) {
    return this.http.put<Categoria>(`${this.base}/categorias`, payload);
  }

  delete(payload: { id: number }) {
    return this.http.request('delete', `${this.base}/categorias`, { body: payload });
  }
}
