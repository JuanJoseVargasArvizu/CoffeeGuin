import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Producto { id?: number; nombre: string; precio?: number; tipo?: string; categoria?: any; stockActual?: number; umbralAlerta?: number; disponible?: boolean }

export interface IngredienteAlerta {
  id: number;
  nombre: string;
  stockActual: number;
  umbralMinimo: number;
  mensaje: string;
}

export interface PendienteMesaProducto {
  productoId: number;
  nombre: string;
  cantidad: number;
  precioUnitario: number;
  subtotalEstimado: number;
}

export interface PendienteMesaResponse {
  mesaId: number;
  productos: PendienteMesaProducto[];
  totalEstimado: number;
}

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private http = inject(HttpClient);
  private base = environment.apiUrl;

  list() {
    return this.http.get<Producto[]>(`${this.base}/productos`);
  }

  listIngredientes() {
    return this.http.get<any[]>(`${this.base}/productos/ingredientes`);
  }

  createIngrediente(payload: { nombre: string; stockActual: number; umbralAlerta: number }) {
    return this.http.post<Producto>(`${this.base}/api/ingredientes`, payload);
  }

  listarAlertasStockBajo() {
    return this.http.get<IngredienteAlerta[]>(`${this.base}/api/ingredientes/alertas`);
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

  registrarPedidoMesa(payload: any): Observable<any> {
    return this.http.post(`${this.base}/api/pedidos-mesa`, payload);
  }

  obtenerPendientesMesa(mesaId: number): Observable<PendienteMesaResponse> {
    return this.http.get<PendienteMesaResponse>(`${this.base}/api/pedidos-mesa/mesa/${mesaId}/pendientes`);
  }

  registrarVentaMesa(payload: any): Observable<any> {
    return this.http.post(`${this.base}/api/ventas/mesa`, payload);
  }
}
