import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

export interface Cliente {
  id?: number;
  nombre: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  preferencias?: string;
  alergias?: string;
  bebidaFavorita?: string;
  platoFavorito?: string;
  activa?: boolean;
  estrategia?: any;
}

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list() {
    return this.http.get<Cliente[]>(`${this.base}/clientes`);
  }

  listActivos() {
    return this.http.get<Cliente[]>(`${this.base}/clientes/activos`);
  }

  getById(id: number) {
    return this.http.get<Cliente>(`${this.base}/clientes/${id}`);
  }

  getByEmail(email: string) {
    return this.http.get<Cliente>(`${this.base}/clientes/email/${encodeURIComponent(email)}`);
  }

  getByTelefono(telefono: string) {
    return this.http.get<Cliente>(`${this.base}/clientes/telefono/${encodeURIComponent(telefono)}`);
  }

  listByEstrategia(estrategiaId: number) {
    return this.http.get<Cliente[]>(`${this.base}/clientes/estrategia/${estrategiaId}`);
  }

  precioDescuento(id: number, total: number) {
    return this.http.get<any>(`${this.base}/clientes/${id}/precio-descuento?total=${total}`);
  }

  create(payload: any) {
    return this.http.post<Cliente>(`${this.base}/clientes`, payload);
  }

  update(payload: any) {
    return this.http.put<Cliente>(`${this.base}/clientes/${payload.id}`, payload);
  }

  patchContacto(id: number, payload: any) {
    return this.http.patch<Cliente>(`${this.base}/clientes/${id}/contacto`, payload);
  }

  patchPreferencias(id: number, payload: any) {
    return this.http.patch<Cliente>(`${this.base}/clientes/${id}/preferencias`, payload);
  }

  assignDescuento(clienteId: number, estrategiaId: number) {
    const payload = { estrategiaId };
    return this.http.post<Cliente>(`${this.base}/clientes/${clienteId}/descuentos`, payload);
  }

  deleteDescuento(id: number) {
    return this.http.delete<Cliente>(`${this.base}/clientes/${id}/descuentos`);
  }

  patchDesactivar(id: number) {
    return this.http.patch<Cliente>(`${this.base}/clientes/${id}/desactivar`, {});
  }

  patchReactivar(id: number) {
    return this.http.patch<Cliente>(`${this.base}/clientes/${id}/reactivar`, {});
  }

  deleteById(id: number) {
    return this.http.delete(`${this.base}/clientes/${id}`);
  }

  // Estrategias
  createEstrategiaPorcentaje(payload: any) {
    return this.http.post<any>(`${this.base}/estrategias-descuento/porcentaje`, payload);
  }
  listEstrategias() {
    return this.http.get<any[]>(`${this.base}/estrategias-descuento`);
  }
}
