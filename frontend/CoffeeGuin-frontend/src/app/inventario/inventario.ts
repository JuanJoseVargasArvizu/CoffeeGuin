import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CategoriaService, Categoria } from '../services/categoria.service';
import { ProductoService, Producto } from '../services/producto.service';

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

  categorias: Categoria[] = [];
  productos: Producto[] = [];
  ingredientes: any[] = [];

  categoriaForm: any = { nombre: '' };
  productoForm: any = { nombre: '', precio: 0, tipo: null, categoriaId: null, stockActual: 0, umbralAlerta: 0, ingredienteIds: [] };

  showCategoriaModal = false;
  showProductoModal = false;

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.categoriaService.list().subscribe({ next: (v: any) => (this.categorias = v || []), error: () => (this.categorias = []) });
    this.productoService.list().subscribe({ next: (v: any) => (this.productos = v || []), error: () => (this.productos = []) });
    this.productoService.listIngredientes().subscribe({ next: (v: any) => (this.ingredientes = v || []), error: () => (this.ingredientes = []) });
  }

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
}
