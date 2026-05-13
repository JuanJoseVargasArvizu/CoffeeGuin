import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
  showModal = false;
  clientes: any[] = [];
  selectedClientId: number | null = null;
  calculoDescuento: any = null;
  showTicket = false;
  ventaExitosa: any = null; // Guardará la respuesta del servidor para el recibo
  pedidoActualBackup: Array<{ producto: Producto; cantidad: number }> = []; 

  confirmarPedido(): void {
    if (this.pedidoActual.length === 0) return;

    // 1. Preparar la lista de productos (aplanar por cantidad)
    const listaProductos: any[] = [];
    this.pedidoActual.forEach(item => {
      for (let i = 0; i < item.cantidad; i++) {
        listaProductos.push({
          id: item.producto.id,
          nombre: item.producto.nombre,
          precio: item.producto.precio
        });
      }
    });

    // 2. Buscar datos del cliente seleccionado si existe
    const clienteSeleccionado = this.clientes.find(c => c.id == this.selectedClientId);

    // 3. Construir el objeto Venta
    const nuevaVenta = {
      fecha: new Date().toISOString(),
      subtotalCalculado: this.total,
      totalFinal: this.calculoDescuento ? this.calculoDescuento.totalConDescuento : this.total,
      productos: listaProductos,
      cliente: clienteSeleccionado ? { id: clienteSeleccionado.id, nombre: clienteSeleccionado.nombre } : null,
      mesa: { id: 1, estado: "Ocupada" } // Mesa hardcodeada
    };

    // 4. Enviar al servidor
    this.http.post<any>(`${environment.apiUrl}/ventas`, nuevaVenta).subscribe({
      next: (res) => {
        this.ventaExitosa = res;
        this.showModal = false; // Cierra el formulario de cliente
        this.showTicket = true; // Abre el recibo virtual
        this.pedidoActual = []; // Limpia el carrito
      },
      error: (err) => {
        console.error('Error al registrar venta', err);
        alert('Hubo un error al procesar la venta');
      }
    });
  }

  cerrarRecibo(): void {
    this.showTicket = false;
    this.ventaExitosa = null;
    this.selectedClientId = null;
    this.calculoDescuento = null;
  }
  

  openCheckout(): void {
    if (this.pedidoActual.length === 0) return;
    this.showModal = true;
    this.http.get<any[]>(`${environment.apiUrl}/clientes`).subscribe({
      next: (res) => this.clientes = res.filter(c => c.activo),
      error: () => console.error('Error cargando clientes')
    });
  }

  onClientChange(): void {
    if (!this.selectedClientId) {
      this.calculoDescuento = null;
      return;
    }

    const currentTotal = this.total;
    this.http.get<any>(`${environment.apiUrl}/clientes/${this.selectedClientId}/precio-descuento?total=${currentTotal}`)
      .subscribe({
        next: (res) => this.calculoDescuento = res,
        error: () => this.calculoDescuento = null
      });
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedClientId = null;
    this.calculoDescuento = null;
  }


  ngOnInit(): void {
    this.loadMenu();
  }

  loadMenu(): void {
    this.error = '';
    this.http.get<unknown>(`${environment.apiUrl}/menu`).subscribe({
      next: (res: any) => {
        // New API: returns array of { categoria, disponibles, noDisponibles }
        if (Array.isArray(res)) {
          this.categorias = res.map((item: any) => {
            const cat = item.categoria || {};
            return {
              id: cat.id,
              nombre: cat.nombre ?? 'Sin nombre',
              disponibles: Array.isArray(item.disponibles) ? item.disponibles : [],
              noDisponibles: Array.isArray(item.noDisponibles) ? item.noDisponibles : []
            } as any;
          });
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
    this.http.get<unknown>(`${environment.apiUrl}/menu/categorias/${categoryId}/productos`).subscribe({
      next: (res: any) => {
        // New API: returns { categoria, disponibles, noDisponibles }
        let disponibles: any[] = [];
        let noDisponibles: any[] = [];
        if (Array.isArray(res)) {
          // array -> assume all available
          disponibles = res.map((p: any) => ({ ...p, disponible: true }));
        } else if (res && Array.isArray(res.disponibles)) {
          disponibles = res.disponibles.map((p: any) => ({ ...p, disponible: true }));
          noDisponibles = Array.isArray(res.noDisponibles) ? res.noDisponibles.map((p: any) => ({ ...p, disponible: false })) : [];
        } else if (res && Array.isArray(res.productos)) {
          // fallback older format
          disponibles = res.productos.map((p: any) => ({ ...p, disponible: true }));
        }

        // Put disponibles first, then not available (visual cue)
        this.productos = [...disponibles, ...noDisponibles];
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

interface Categoria { id?: number; nombre: string; productos?: any[]; icon?: string; disponibles?: Producto[]; noDisponibles?: Producto[] }
interface Producto { id?: number; nombre: string; precio?: number; descripcion?: string; ingredientes?: any[]; disponible?: boolean }
