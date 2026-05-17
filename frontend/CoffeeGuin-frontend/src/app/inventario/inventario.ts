import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription, forkJoin, timer } from 'rxjs';
import { CategoriaService, Categoria } from '../services/categoria.service';
import { IngredienteAlerta, ProductoService, Producto } from '../services/producto.service';
import { ClienteService, Cliente } from '../services/cliente.service';
import { MesaService, Mesa } from '../services/mesa.service';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario.html',
  styleUrls: ['./inventario.css'],
})
export class InventarioComponent implements OnInit {
  private categoriaService = inject(CategoriaService);
  private productoService = inject(ProductoService);
  private clienteService = inject(ClienteService);
  private mesaService = inject(MesaService); 

  categorias: Categoria[] = [];
  productos: Producto[] = [];
  ingredientes: any[] = [];
  alertasStock: IngredienteAlerta[] = [];
  alertasMinimizadas = false;
  clientes: Cliente[] = [];
  mesas: Mesa[] = [];
  showMesaModal = false;
  mesaForm: any = { numero: null, estado: 'Libre', cantidadAsientos: null };
  private alertasPolling?: Subscription;

  categoriaForm: any = { nombre: '' };
  productoForm: any = { 
    nombre: '', 
    precio: 0, 
    tipo: null, 
    categoriaId: null, 
    stockActual: 0, 
    umbralAlerta: 0, 
    ingredienteIds: [],
    cantidadesIngredientes: {}
  };  
  clienteForm: any = { nombre: '', email: '', telefono: '', direccion: '', preferencias: '', alergias: '', bebidaFavorita: '', platoFavorito: '', activa: true };

  showCategoriaModal = false;
  showProductoModal = false;
  showClienteModal = false;
  // estrategias
  estrategias: any[] = [];
  showEstrategiaModal = false; // create estrategia
  estrategiaForm: any = { nombre: '', descripcion: '', porcentaje: 0 };
  // assign modal
  showAssignModal = false;
  selectedClientForAssign: Cliente | null = null;

  ngOnInit(): void {
    this.loadAll();
    this.alertasPolling = timer(60000, 60000).subscribe(() => this.cargarAlertasStock());
  }

  ngOnDestroy(): void {
    this.alertasPolling?.unsubscribe();
  }

  loadAll(): void {
    this.categoriaService.list().subscribe({ next: (v: any) => (this.categorias = v || []), error: () => (this.categorias = []) });
    this.productoService.list().subscribe({ next: (v: any) => (this.productos = v || []), error: () => (this.productos = []) });
    this.productoService.listIngredientes().subscribe({ next: (v: any) => (this.ingredientes = v || []), error: () => (this.ingredientes = []) });
    this.clienteService.list().subscribe({ next: (v: any) => (this.clientes = v || []), error: () => (this.clientes = []) });
    this.mesaService.list().subscribe({ next: (v) => (this.mesas = v || []), error: () => (this.mesas = []) });
    this.cargarAlertasStock();
    this.loadEstrategias();
  }

  cargarAlertasStock(): void {
    this.productoService.listarAlertasStockBajo().subscribe({
      next: (v) => (this.alertasStock = v || []),
      error: () => (this.alertasStock = []),
    });
  }

  alternarAlertas(): void {
    this.alertasMinimizadas = !this.alertasMinimizadas;
  }

  loadEstrategias() { this.clienteService.listEstrategias().subscribe({ next: (v: any) => (this.estrategias = v || []), error: () => (this.estrategias = []) }); }

  startEditCategoria(c: Categoria) { this.categoriaForm = { ...c }; this.openCategoriaModal(); }
  cancelCategoria() { this.categoriaForm = { nombre: '' }; this.closeCategoriaModal(); }

  // Métodos para Mesas
  openMesaModal(newOne = false) {
    if (newOne) this.mesaForm = { numero: null, estado: 'Libre', cantidadAsientos: null };
    this.showMesaModal = true;
  }

  closeMesaModal() {
    this.showMesaModal = false;
    this.mesaForm = { numero: null, estado: 'Libre', cantidadAsientos: null };
  }

  saveMesa() {
    if (this.mesaForm.id) {
      const requests = [];

      const cantidadAsientos = Number(this.mesaForm.cantidadAsientos ?? 0);
      if (Number.isFinite(cantidadAsientos) && cantidadAsientos > 0 && cantidadAsientos !== Number(this.mesaForm.originalCantidadAsientos ?? 0)) {
        requests.push(this.mesaService.actualizarAsientos(this.mesaForm.id, cantidadAsientos));
      }

      if (this.mesaForm.estado && this.mesaForm.estado !== this.mesaForm.originalEstado) {
        requests.push(this.mesaService.actualizarEstado(this.mesaForm.id, this.mesaForm.estado));
      }

      if (requests.length === 0) {
        this.closeMesaModal();
        return;
      }

      forkJoin(requests).subscribe({
        next: () => {
          this.loadAll();
          this.closeMesaModal();
        },
        error: (err) => console.error('Error al actualizar mesa:', err),
      });
    } else {
      const payload = {
        numero: Number(this.mesaForm.numero),
        estado: 'Libre',
        cantidadAsientos: Number(this.mesaForm.cantidadAsientos ?? 0),
      };

      this.mesaService.create(payload as Mesa).subscribe({
        next: () => {
          this.loadAll();
          this.closeMesaModal();
        },
        error: (err) => console.error('Error al crear mesa:', err),
      });
    }
  }

  startEditMesa(m: Mesa) {
    const cantidadAsientos = this.cantidadAsientosMesa(m);
    this.mesaForm = {
      ...m,
      cantidadAsientos,
      originalCantidadAsientos: cantidadAsientos,
      originalEstado: m.estado,
    };
    this.showMesaModal = true;
  }

  openCategoriaModal(newOne = false) {
    if (newOne) this.categoriaForm = { nombre: '' };
    this.showCategoriaModal = true;
  }
  closeCategoriaModal() {
    this.showCategoriaModal = false;
    this.categoriaForm = { nombre: '' };
  }

  saveCategoria() {
    if (this.categoriaForm.id) {
      this.categoriaService.update({ id: this.categoriaForm.id, nombre: this.categoriaForm.nombre }).subscribe(() => this.loadAll());
    } else {
      this.categoriaService.create({ nombre: this.categoriaForm.nombre }).subscribe(() => this.loadAll());
    }
    this.categoriaForm = { nombre: '' };
    this.closeCategoriaModal();
  }

  delCategoria(c: Categoria) {
    if (!confirm('Eliminar categoría?')) return;
    this.categoriaService.delete({ id: c.id! }).subscribe(() => this.loadAll());
  }

  startEditProducto(p: Producto) {
    const lineas = (p as any).lineasReceta || [];
    const ingredienteIds = lineas.map((linea: any) => linea.id?.ingredienteId || linea.ingrediente?.id);
    
    const cantidadesIngredientes: { [key: number]: number } = {};
    lineas.forEach((linea: any) => {
      const ingId = linea.id?.ingredienteId || linea.ingrediente?.id;
      if (ingId) {
        cantidadesIngredientes[ingId] = linea.cantidad || 0;
      }
    });

    this.productoForm = { 
      ...p, 
      tipo: (p as any).tipo ?? null, 
      precio: (p as any).precio ?? 0, 
      categoriaId: p.categoria?.id ?? p.categoria, 
      umbralAlerta: (p as any).umbralAlerta ?? 0,
      ingredienteIds: ingredienteIds,
      cantidadesIngredientes: cantidadesIngredientes
    };
    this.openProductoModal();
  }

  cancelProducto() {
    this.productoForm = { nombre: '', precio: 0, tipo: null, categoriaId: null, stockActual: 0, umbralAlerta: 0, ingredienteIds: [], cantidadesIngredientes: {} };
  }

  openProductoModal(newOne = false) {
    if (newOne) this.productoForm = { nombre: '', precio: 0, tipo: null, categoriaId: null, stockActual: 0, umbralAlerta: 0, ingredienteIds: [] };
    this.showProductoModal = true;
  }

  closeProductoModal() {
    this.showProductoModal = false;
    this.productoForm = { nombre: '', precio: 0, tipo: null, categoriaId: null, stockActual: 0, umbralAlerta: 0, ingredienteIds: [], cantidadesIngredientes: {} };
  }

  setProductoTipo(t: string) {
    this.productoForm.tipo = t;
    if (t === 'ingrediente') {
      this.productoForm.precio = 0;
      this.productoForm.ingredienteIds = [];
      this.productoForm.stockActual = this.productoForm.stockActual ?? 0;
      this.productoForm.umbralAlerta = this.productoForm.umbralAlerta ?? 0;
    } else {
      this.productoForm.precio = this.productoForm.precio ?? 0;
      this.productoForm.ingredienteIds = this.productoForm.ingredienteIds ?? [];
      this.productoForm.stockActual = null;
    }
  }

  toggleIngrediente(id: number, checked: boolean) {
    if (!this.productoForm.ingredienteIds) this.productoForm.ingredienteIds = [];
    if (!this.productoForm.cantidadesIngredientes) this.productoForm.cantidadesIngredientes = {};

    if (checked) {
      if (!this.productoForm.ingredienteIds.includes(id)) {
        this.productoForm.ingredienteIds.push(id);
        this.productoForm.cantidadesIngredientes[id] = 0.0; // Cantidad inicial por defecto
      }
    } else {
      this.productoForm.ingredienteIds = this.productoForm.ingredienteIds.filter((x: any) => x !== id);
      delete this.productoForm.cantidadesIngredientes[id]; // Eliminamos el rastro de cantidad
    }
  }

  isIngredienteSelected(id: number) {
    return (this.productoForm.ingredienteIds || []).includes(id);
  }

  saveProducto() {
    let payload: any;
    if (this.productoForm.tipo === 'ingrediente') {
      payload = {
        nombre: this.productoForm.nombre,
        stockActual: this.productoForm.stockActual ?? 0,
        umbralAlerta: this.productoForm.umbralAlerta ?? 0
      };
    } else {
      // Id del producto actual (si es edición usa el ID real, si es creación usamos 0 o null como placeholder)
      const actualProductoId = this.productoForm.id || 0;

      // Construimos el arreglo con el formato exacto que Postman validó con éxito
      const lineasReceta = (this.productoForm.ingredienteIds || []).map((id: number) => {
        const cantidadAsignada = Number(this.productoForm.cantidadesIngredientes[id] || 0.0);
        return {
          id: { 
            productoId: actualProductoId, // Mapeo de la llave compuesta productoId
            ingredienteId: id             // Mapeo de la llave compuesta ingredienteId
          },
          ingrediente: { 
            id: id,
            tipo: 'ingrediente' // <-- INDISPENSABLE: Evita el HttpMessageNotReadableException de Jackson
          },
          cantidad: cantidadAsignada
        };
      });

      payload = {
        nombre: this.productoForm.nombre,
        precio: this.productoForm.precio ?? 0,
        tipo: this.productoForm.tipo,
        categoria: this.productoForm.categoriaId ? { id: this.productoForm.categoriaId } : null,
        lineasReceta: lineasReceta.length > 0 ? lineasReceta : [],
        stockActual: this.productoForm.stockActual ?? 0,
        umbralAlerta: this.productoForm.umbralAlerta ?? 0
      };
    }

    if (this.productoForm.id) {
      payload.id = this.productoForm.id;
      this.productoService.update(payload).subscribe({
        next: () => this.loadAll(),
        error: (err) => console.error('Error al actualizar producto:', err)
      });
    } else {
      const createRequest = this.productoForm.tipo === 'ingrediente'
        ? this.productoService.createIngrediente(payload)
        : this.productoService.create(payload);

      createRequest.subscribe({
        next: () => this.loadAll(),
        error: (err) => console.error('Error al crear producto:', err)
      });
    }

    this.cancelProducto();
    this.closeProductoModal();
  }
  delProducto(p: Producto) {
    if (!confirm('Eliminar producto?')) return;
    if (p.id) {
      this.productoService.deleteById(p.id).subscribe(() => this.loadAll());
    } else {
      this.productoService.delete({ id: p.id! }).subscribe(() => this.loadAll());
    }
  }

  /* Clientes */
  openClienteModal(newOne = false) {
    if (newOne) this.clienteForm = { nombre: '', email: '', telefono: '', direccion: '', preferencias: '', alergias: '', bebidaFavorita: '', platoFavorito: '', activa: true };
    this.showClienteModal = true;
  }

  closeClienteModal() {
    this.showClienteModal = false;
    this.clienteForm = { nombre: '', email: '', telefono: '', direccion: '', preferencias: '', alergias: '', bebidaFavorita: '', platoFavorito: '', activa: true };
  }

  startEditCliente(c: Cliente) { this.clienteForm = { ...c }; this.openClienteModal(); }

  saveCliente() {
    const payload: any = {
      nombre: this.clienteForm.nombre,
      email: this.clienteForm.email,
      telefono: this.clienteForm.telefono,
      direccion: this.clienteForm.direccion,
      preferencias: this.clienteForm.preferencias,
      alergias: this.clienteForm.alergias,
      bebidaFavorita: this.clienteForm.bebidaFavorita,
      platoFavorito: this.clienteForm.platoFavorito,
      activa: this.clienteForm.activa ?? true
    };

    if (this.clienteForm.id) {
      payload.id = this.clienteForm.id;
      this.clienteService.update(payload).subscribe(() => this.loadAll());
    } else {
      this.clienteService.create(payload).subscribe(() => this.loadAll());
    }

    this.closeClienteModal();
  }

  delCliente(c: Cliente) {
    if (!confirm('Eliminar cliente?')) return;
    if (c.id) {
      this.clienteService.deleteById(c.id).subscribe(() => this.loadAll());
    }
  }

  /* Estrategia / descuentos */
  openCreateEstrategiaModal() {
    this.estrategiaForm = { nombre: '', descripcion: '', porcentaje: 0 };
    this.showEstrategiaModal = true;
  }

  closeEstrategiaModal() {
    this.showEstrategiaModal = false;
    this.estrategiaForm = { nombre: '', descripcion: '', porcentaje: 0 };
  }

  saveEstrategia() {
    const payload = { nombre: this.estrategiaForm.nombre, descripcion: this.estrategiaForm.descripcion, porcentaje: Number(this.estrategiaForm.porcentaje) };
    this.clienteService.createEstrategiaPorcentaje(payload).subscribe({ next: () => { this.loadEstrategias(); this.closeEstrategiaModal(); }, error: () => { alert('Error creando estrategia'); } });
  }

  /* Assign existing estrategia to client */
  openAssignModal(client: Cliente) {
    this.selectedClientForAssign = client;
    this.showAssignModal = true;
    // ensure estrategias are loaded
    if (!this.estrategias || this.estrategias.length === 0) this.loadEstrategias();
  }

  closeAssignModal() { this.selectedClientForAssign = null; this.showAssignModal = false; }

  assignEstrategiaToClient(estr: any) {
    if (!this.selectedClientForAssign || !this.selectedClientForAssign.id) return;
    const estrategiaId = estr.id ?? estr.estrategiaId ?? null;
    if (!estrategiaId) { alert('Estrategia inválida'); return; }
    this.clienteService.assignDescuento(this.selectedClientForAssign.id, estrategiaId).subscribe({ next: () => { this.loadAll(); this.closeAssignModal(); }, error: () => { alert('Error asignando estrategia'); } });
  }

  calcularPrecioConDescuento(c: Cliente) {
    if (!c.id) { alert('Cliente sin id'); return; }
    const totalStr = prompt('Total a calcular (por ejemplo 100.00):');
    if (!totalStr) return;
    const total = Number(totalStr);
    if (isNaN(total)) { alert('Total inválido'); return; }
    this.clienteService.precioDescuento(c.id, total).subscribe({ next: (res: any) => {
      alert(`Total original: ${res.totalOriginal}\nDescuento: ${res.montoDescuento}\nTotal con descuento: ${res.totalConDescuento}`);
    }, error: () => { alert('Error calculando descuento'); } });
  }

  cantidadAsientosMesa(mesa: Mesa): number {
    return Number(mesa.cantidadAsientos ?? mesa.asientos?.length ?? 0);
  }
}
