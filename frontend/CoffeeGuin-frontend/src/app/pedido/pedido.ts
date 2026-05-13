import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-pedido',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedido.html',
  styleUrl: './pedido.css',
})
export class PedidoComponent implements OnInit {
  private http = inject(HttpClient);
  categorias: Categoria[] = [];
  productos: Producto[] = [];
  selectedCategoryId: number | null = null;
  error = '';
  loadingProductos = false;
  pedidoActual: Array<{ producto: Producto; cantidad: number }> = [];

  ngOnInit(): void {
    this.loadMenu();
  }

  loadMenu(): void {
    this.error = '';
    this.http.get<unknown>(`${environment.apiUrl}/menu`).subscribe({
      next: (res: any) => {
        if (Array.isArray(res)) {
          this.categorias = res;
        } else if (res && Array.isArray(res.categorias)) {
          this.categorias = res.categorias;
        } else {
          this.categorias = [];
        }
        // If we don't have a selected category yet, auto-select the first one
        if (this.categorias.length > 0 && this.selectedCategoryId == null) {
          const firstId = this.categorias[0].id;
          if (firstId != null) this.selectCategory(firstId);
        }
        // assign a simple geometric icon type to each category for visuals
        const shapes = ['circle', 'square', 'triangle', 'hexagon'];
        this.categorias = this.categorias.map((c: any, i: number) => ({ ...c, icon: shapes[i % shapes.length] }));
      },
      error: () => (this.error = 'No se pudo cargar el menu')
    });
  }

  selectCategory(categoryId: number): void {
    this.selectedCategoryId = categoryId;
    this.loadingProductos = true;
    this.productos = [];
    this.http.get<Producto[]>(`${environment.apiUrl}/menu/categorias/${categoryId}/productos`).subscribe({
      next: (res: any) => {
        this.productos = Array.isArray(res) ? res : (res && Array.isArray(res.productos) ? res.productos : []);
        this.loadingProductos = false;
      },
      error: () => {
        this.productos = [];
        this.loadingProductos = false;
      }
    });
  }

  addToOrder(producto: Producto): void {
    if (!producto || producto.id == null) return;
    const existing = this.pedidoActual.find(p => p.producto.id === producto.id);
    if (existing) {
      existing.cantidad += 1;
    } else {
      this.pedidoActual.push({ producto, cantidad: 1 });
    }
    console.log('Pedido actual:', this.pedidoActual);
  }

  removeFromOrder(producto: Producto): void {
    if (!producto || producto.id == null) return;
    const idx = this.pedidoActual.findIndex(p => p.producto.id === producto.id);
    if (idx === -1) return;
    const entry = this.pedidoActual[idx];
    if (entry.cantidad > 1) {
      entry.cantidad -= 1;
    } else {
      this.pedidoActual.splice(idx, 1);
    }
  }

  get total(): number {
    return this.pedidoActual.reduce((sum, item) => {
      const precio = Number(item.producto.precio ?? 0);
      return sum + precio * item.cantidad;
    }, 0);
  }
}

interface Categoria { id?: number; nombre: string; productos?: any[]; icon?: string }
interface Producto { id?: number; nombre: string; precio?: number }
