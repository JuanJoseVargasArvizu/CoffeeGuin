import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { environment } from '../../environments/environment';
import { ClienteService } from '../services/cliente.service';
import { Mesa, MesaService } from '../services/mesa.service';
import { PendienteMesaProducto, PendienteMesaResponse, Producto, ProductoService } from '../services/producto.service';

interface Cliente {
  id: number;
  nombre: string;
  email?: string;
  activo?: boolean;
}

interface ComandaLinea {
  producto: Producto;
  cantidad: number;
}

interface DescuentoPreview {
  totalOriginal: number;
  montoDescuento: number;
  totalConDescuento: number;
}

interface VentaMesaResponse {
  ventaId: number;
  totalOriginal: number;
  montoDescuento: number;
  totalFinal: number;
  status: string;
}

interface TicketLinea {
  nombre: string;
  cantidad: number;
  subtotal: number;
}

@Component({
  selector: 'app-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pedido.html',
  styleUrl: './pedido.css',
})
export class PedidoComponent implements OnInit {
  private http = inject(HttpClient);
  private clienteService = inject(ClienteService);
  private mesaService = inject(MesaService);
  private productoService = inject(ProductoService);

  mesas: Mesa[] = [];
  selectedMesa: Mesa | null = null;
  categorias: Categoria[] = [];
  productos: Producto[] = [];
  selectedCategoryId: number | null = null;
  error = '';
  loadingMesas = false;
  loadingMenu = false;
  cuentaMesaData: PendienteMesaResponse | null = null;
  comandaNueva: ComandaLinea[] = [];
  clientes: Cliente[] = [];
  selectedClientId: number | null = null;
  showCobroModal = false;
  showTicketModal = false;
  descuentoPreview: DescuentoPreview | null = null;
  ventaFinalizada: VentaMesaResponse | null = null;
  ticketLineas: TicketLinea[] = [];

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.error = '';
    this.loadingMesas = true;

    forkJoin({
      libres: this.mesaService.listarPorEstado('Libre'),
      ocupadas: this.mesaService.listarPorEstado('Ocupada'),
    }).subscribe({
      next: ({ libres, ocupadas }) => {
        this.mesas = [...libres, ...ocupadas].sort((a, b) => {
          const numeroA = a.numero ?? a.id ?? 0;
          const numeroB = b.numero ?? b.id ?? 0;
          return numeroA - numeroB;
        });
        this.loadingMesas = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar las mesas';
        this.mesas = [];
        this.loadingMesas = false;
      },
    });
  }

  abrirMesa(mesa: Mesa): void {
    if (!mesa.id) return;

    this.error = '';
    this.mesaService.actualizarEstado(mesa.id, 'Ocupada').subscribe({
      next: () => {
        this.loadAll();
      },
      error: () => {
        this.error = 'No se pudo cambiar el estado de la mesa';
      },
    });
  }

  seleccionarMesaParaComanda(mesa: Mesa): void {
    if (!mesa.id || mesa.estado !== 'Ocupada') return;

    this.selectedMesa = { ...mesa };
    this.selectedCategoryId = null;
    this.productos = [];
    this.comandaNueva = [];
    this.cuentaMesaData = null;
    this.showCobroModal = false;
    this.showTicketModal = false;
    this.descuentoPreview = null;
    this.ventaFinalizada = null;
    this.ticketLineas = [];
    this.selectedClientId = null;

    this.loadMenu();
    this.cargarPendientesMesa(mesa.id);
  }

  volverAlMapaDeMesas(): void {
    this.selectedMesa = null;
    this.selectedCategoryId = null;
    this.productos = [];
    this.cuentaMesaData = null;
    this.comandaNueva = [];
    this.showCobroModal = false;
    this.showTicketModal = false;
    this.descuentoPreview = null;
    this.ventaFinalizada = null;
    this.ticketLineas = [];
    this.selectedClientId = null;
    this.clientes = [];
    this.error = '';
  }

  loadMenu(): void {
    this.loadingMenu = true;
    this.error = '';

    this.http.get<unknown>(`${environment.apiUrl}/menu`).subscribe({
      next: (res: any) => {
        if (Array.isArray(res)) {
          this.categorias = res.map((item: any) => {
            const categoria = item.categoria || {};
            return {
              id: categoria.id,
              nombre: categoria.nombre ?? 'Sin nombre',
              disponibles: Array.isArray(item.disponibles) ? item.disponibles : [],
              noDisponibles: Array.isArray(item.noDisponibles) ? item.noDisponibles : [],
            } as Categoria;
          });
        } else if (res && Array.isArray(res.categorias)) {
          this.categorias = res.categorias;
        } else {
          this.categorias = [];
        }

        if (this.categorias.length > 0 && this.selectedCategoryId == null) {
          const firstId = this.categorias[0].id;
          if (firstId != null) {
            this.selectCategory(firstId);
          }
        }

        const shapes = ['circle', 'square', 'triangle', 'hexagon'];
        this.categorias = this.categorias.map((categoria: any, index: number) => ({
          ...categoria,
          icon: shapes[index % shapes.length],
        }));
        this.loadingMenu = false;
      },
      error: () => {
        this.error = 'No se pudo cargar el menu';
        this.categorias = [];
        this.loadingMenu = false;
      },
    });
  }

  selectCategory(categoryId: number): void {
    this.selectedCategoryId = categoryId;
    this.loadingMenu = true;
    this.productos = [];

    this.http.get<unknown>(`${environment.apiUrl}/menu/categorias/${categoryId}/productos`).subscribe({
      next: (res: any) => {
        let disponibles: any[] = [];
        let noDisponibles: any[] = [];

        if (Array.isArray(res)) {
          disponibles = res.map((producto: any) => ({ ...producto, disponible: true }));
        } else if (res && Array.isArray(res.disponibles)) {
          disponibles = res.disponibles.map((producto: any) => ({ ...producto, disponible: true }));
          noDisponibles = Array.isArray(res.noDisponibles)
            ? res.noDisponibles.map((producto: any) => ({ ...producto, disponible: false }))
            : [];
        } else if (res && Array.isArray(res.productos)) {
          disponibles = res.productos.map((producto: any) => ({ ...producto, disponible: true }));
        }

        this.productos = [...disponibles, ...noDisponibles];
        this.loadingMenu = false;
      },
      error: () => {
        this.productos = [];
        this.loadingMenu = false;
      },
    });
  }

  agregarProductoAComandaTemporal(producto: Producto, cantidad: number): void {
    if (!this.selectedMesa?.id || !producto.id) return;

    const cantidadNormalizada = Math.max(1, Math.floor(Number(cantidad) || 1));
    const existente = this.comandaNueva.find((linea) => linea.producto.id === producto.id);

    if (existente) {
      existente.cantidad += cantidadNormalizada;
    } else {
      this.comandaNueva.push({ producto, cantidad: cantidadNormalizada });
    }
  }

  quitarProductoDeComandaTemporal(producto: Producto): void {
    if (!producto.id) return;

    const indice = this.comandaNueva.findIndex((linea) => linea.producto.id === producto.id);
    if (indice === -1) return;

    const linea = this.comandaNueva[indice];
    if (linea.cantidad > 1) {
      linea.cantidad -= 1;
    } else {
      this.comandaNueva.splice(indice, 1);
    }
  }

  enviarComandaABackend(): void {
    if (!this.selectedMesa?.id || this.comandaNueva.length === 0) return;

    const productos = this.comandaNueva
      .filter((linea) => linea.producto.id != null)
      .map((linea) => ({ id: linea.producto.id as number, cantidad: linea.cantidad }));

    if (productos.length === 0) {
      this.error = 'No hay productos válidos para enviar';
      return;
    }

    const payload = {
      mesaId: this.selectedMesa.id,
      productos,
      estado_pago: 'pendiente',
    };

    this.productoService.registrarPedidoMesa(payload).subscribe({
      next: () => {
        this.comandaNueva = [];
        this.cargarPendientesMesa(this.selectedMesa!.id!);
      },
      error: () => {
        this.error = 'No se pudo registrar la comanda';
      },
    });
  }

  abrirModalCobro(): void {
    if (!this.selectedMesa?.id) return;

    this.showCobroModal = true;
    this.selectedClientId = null;
    this.descuentoPreview = null;
    this.http.get<any[]>(`${environment.apiUrl}/clientes`).subscribe({
      next: (res) => {
        this.clientes = Array.isArray(res) ? res.filter((cliente) => cliente.activo !== false) : [];
      },
      error: () => {
        this.clientes = [];
      },
    });
  }

  cerrarModalCobro(): void {
    this.showCobroModal = false;
    this.selectedClientId = null;
    this.descuentoPreview = null;
  }

  onClienteSeleccionadoChange(): void {
    this.verificarDescuentoCliente();
  }

  cobrarCuentaMesa(clienteId: number | null): void {
    if (!this.selectedMesa?.id) return;

    const payload = {
      mesaId: this.selectedMesa.id,
      clienteId,
    };

    this.productoService.registrarVentaMesa(payload).subscribe({
      next: (res) => {
        this.showCobroModal = false;
        this.ventaFinalizada = res;
        this.ticketLineas = this.construirLineasTicket();
        this.showTicketModal = true;
        this.selectedClientId = null;
        this.descuentoPreview = null;
      },
      error: () => {
        this.error = 'No se pudo cerrar la cuenta de la mesa';
      },
    });
  }

  verificarDescuentoCliente(): void {
    const totalBase = this.totalMesa;

    if (!this.selectedClientId) {
      this.descuentoPreview = {
        totalOriginal: totalBase,
        montoDescuento: 0,
        totalConDescuento: totalBase,
      };
      return;
    }

    this.clienteService.precioDescuento(this.selectedClientId, totalBase).subscribe({
      next: (res: any) => {
        this.descuentoPreview = {
          totalOriginal: Number(res?.totalOriginal ?? totalBase),
          montoDescuento: Number(res?.montoDescuento ?? 0),
          totalConDescuento: Number(res?.totalConDescuento ?? totalBase),
        };
      },
      error: () => {
        this.error = 'No se pudo verificar el descuento del cliente';
      },
    });
  }

  cargarPendientesMesa(mesaId: number): void {
    this.productoService.obtenerPendientesMesa(mesaId).subscribe({
      next: (res) => {
        this.cuentaMesaData = res;
      },
      error: () => {
        this.cuentaMesaData = null;
        this.error = 'No se pudo cargar la cuenta pendiente de la mesa';
      },
    });
  }

  etiquetaMesa(mesa: Mesa): number | string {
    return mesa.numero ?? mesa.id ?? '-';
  }

  nombrePendiente(item: any): string {
    return item?.nombre ?? 'Producto';
  }

  cantidadPendiente(item: PendienteMesaProducto): number {
    return item?.cantidad ?? 0;
  }

  subtotalPendiente(item: PendienteMesaProducto): number {
    return item?.subtotalEstimado ?? 0;
  }

  get totalPendienteMesa(): number {
    return this.cuentaMesaData?.totalEstimado ?? 0;
  }

  subtotalComanda(linea: ComandaLinea): number {
    return this.normalizarNumero(linea.producto.precio, 0) * linea.cantidad;
  }

  private normalizarNumero(valor: unknown, fallback: number): number {
    const numero = Number(valor);
    return Number.isFinite(numero) ? numero : fallback;
  }

  get totalComandaNueva(): number {
    return this.comandaNueva.reduce((total, linea) => total + this.subtotalComanda(linea), 0);
  }

  get totalVentaPreview(): number {
    return this.totalMesa;
  }

  get totalOriginalPreview(): number {
    return this.descuentoPreview?.totalOriginal ?? this.totalVentaPreview;
  }

  get montoDescuentoPreview(): number {
    return this.descuentoPreview?.montoDescuento ?? 0;
  }

  get totalConDescuentoPreview(): number {
    return this.descuentoPreview?.totalConDescuento ?? this.totalVentaPreview;
  }

  get clienteSeleccionado(): Cliente | null {
    return this.clientes.find((cliente) => cliente.id === this.selectedClientId) ?? null;
  }

  construirLineasTicket(): TicketLinea[] {
    const pendientes = (this.cuentaMesaData?.productos ?? []).map((item) => ({
      nombre: item.nombre,
      cantidad: item.cantidad,
      subtotal: item.subtotalEstimado,
    }));

    const nuevas = this.comandaNueva.map((item) => ({
      nombre: item.producto.nombre ?? 'Producto',
      cantidad: item.cantidad,
      subtotal: this.subtotalComanda(item),
    }));

    return [...pendientes, ...nuevas];
  }

  cerrarTicket(): void {
    this.showTicketModal = false;
    this.ventaFinalizada = null;
    this.ticketLineas = [];
    this.comandaNueva = [];
    this.cuentaMesaData = null;
    this.productos = [];
    this.categorias = [];
    this.selectedCategoryId = null;
    this.selectedMesa = null;
    this.loadAll();
  }


  get totalMesa(): number {
    return this.totalPendienteMesa + this.totalComandaNueva;
  }
}

interface Categoria {
  id?: number;
  nombre: string;
  productos?: any[];
  icon?: string;
  disponibles?: Producto[];
  noDisponibles?: Producto[];
}