import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CategoriaService, Categoria } from '../services/categoria.service';
import { ProductoService, Producto } from '../services/producto.service';
import { ClienteService, Cliente } from '../services/cliente.service';

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

  categorias: Categoria[] = [];
  productos: Producto[] = [];
  ingredientes: any[] = [];
  clientes: Cliente[] = [];

  categoriaForm: any = { nombre: '' };
  productoForm: any = { nombre: '', precio: 0, tipo: null, categoriaId: null, stockActual: 0, umbralAlerta: 0, ingredienteIds: [] };
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
  }

  loadAll(): void {
    this.categoriaService.list().subscribe({ next: (v: any) => (this.categorias = v || []), error: () => (this.categorias = []) });
    this.productoService.list().subscribe({ next: (v: any) => (this.productos = v || []), error: () => (this.productos = []) });
    this.productoService.listIngredientes().subscribe({ next: (v: any) => (this.ingredientes = v || []), error: () => (this.ingredientes = []) });
    this.clienteService.list().subscribe({ next: (v: any) => (this.clientes = v || []), error: () => (this.clientes = []) });
    this.loadEstrategias();
  }

  loadEstrategias() { this.clienteService.listEstrategias().subscribe({ next: (v: any) => (this.estrategias = v || []), error: () => (this.estrategias = []) }); }

  startEditCategoria(c: Categoria) { this.categoriaForm = { ...c }; this.openCategoriaModal(); }
  cancelCategoria() { this.categoriaForm = { nombre: '' }; this.closeCategoriaModal(); }

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
    this.productoForm = { ...p, tipo: (p as any).tipo ?? null, precio: (p as any).precio ?? 0, categoriaId: p.categoria?.id ?? p.categoria, ingredienteIds: (p as any).ingredientes ? (p as any).ingredientes.map((i: any) => i.id ?? i) : [] };
    this.openProductoModal();
  }

  cancelProducto() {
    this.productoForm = { nombre: '', precio: 0, tipo: null, categoriaId: null, stockActual: 0, umbralAlerta: 0, ingredienteIds: [] };
  }

  openProductoModal(newOne = false) {
    if (newOne) this.productoForm = { nombre: '', precio: 0, tipo: null, categoriaId: null, stockActual: 0, umbralAlerta: 0, ingredienteIds: [] };
    this.showProductoModal = true;
  }

  closeProductoModal() {
    this.showProductoModal = false;
    this.productoForm = { nombre: '', precio: 0, tipo: null, categoriaId: null, stockActual: 0, umbralAlerta: 0, ingredienteIds: [] };
  }

  setProductoTipo(t: string) {
    this.productoForm.tipo = t;
    if (t === 'ingrediente') {
      this.productoForm.precio = 0;
      this.productoForm.ingredienteIds = [];
      this.productoForm.stockActual = this.productoForm.stockActual ?? 0;
    } else {
      this.productoForm.precio = this.productoForm.precio ?? 0;
      this.productoForm.ingredienteIds = this.productoForm.ingredienteIds ?? [];
      this.productoForm.stockActual = null;
    }
  }

  toggleIngrediente(id: number, checked: boolean) {
    if (!this.productoForm.ingredienteIds) this.productoForm.ingredienteIds = [];
    if (checked) {
      if (!this.productoForm.ingredienteIds.includes(id)) this.productoForm.ingredienteIds.push(id);
    } else {
      this.productoForm.ingredienteIds = this.productoForm.ingredienteIds.filter((x: any) => x !== id);
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
        tipo: 'ingrediente',
        precio: 0,
        categoria: this.productoForm.categoriaId ? { id: this.productoForm.categoriaId } : null,
        ingredientes: [],
        stockActual: this.productoForm.stockActual ?? 0,
        umbralAlerta: this.productoForm.umbralAlerta ?? 0
      };
    } else {
      payload = {
        nombre: this.productoForm.nombre,
        precio: this.productoForm.precio ?? 0,
        tipo: this.productoForm.tipo,
        categoria: this.productoForm.categoriaId ? { id: this.productoForm.categoriaId } : null,
        ingredientes: (this.productoForm.ingredienteIds && this.productoForm.ingredienteIds.length > 0) ? (this.productoForm.ingredienteIds || []).map((id: number) => ({ id, tipo: 'ingrediente' })) : null,
        stockActual: this.productoForm.stockActual ?? 0,
        umbralAlerta: this.productoForm.umbralAlerta ?? 0
      };
    }

    if (this.productoForm.id) {
      payload.id = this.productoForm.id;
      this.productoService.update(payload).subscribe(() => this.loadAll());
    } else {
      this.productoService.create(payload).subscribe(() => this.loadAll());
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
}
