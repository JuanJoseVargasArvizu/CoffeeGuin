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
  styleUrl: './inventario.css',
})
export class InventarioComponent implements OnInit {
  private categoriaService = inject(CategoriaService);
  private productoService = inject(ProductoService);

  categorias: Categoria[] = [];
  productos: Producto[] = [];

  categoriaForm: any = { nombre: '' };
  productoForm: any = { nombre: '', precio: 0, tipo: 'bebida', categoriaId: null, stockActual: null, umbralAlerta: null };

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.categoriaService.list().subscribe({ next: (v: any) => (this.categorias = v || []), error: () => (this.categorias = []) });
    this.productoService.list().subscribe({ next: (v: any) => (this.productos = v || []), error: () => (this.productos = []) });
  }

  startEditCategoria(c: Categoria) { this.categoriaForm = { ...c }; }
  cancelCategoria() { this.categoriaForm = { nombre: '' }; }

  saveCategoria() {
    if (this.categoriaForm.id) {
      this.categoriaService.update({ id: this.categoriaForm.id, nombre: this.categoriaForm.nombre }).subscribe(() => this.loadAll());
    } else {
      this.categoriaService.create({ nombre: this.categoriaForm.nombre }).subscribe(() => this.loadAll());
    }
    this.categoriaForm = { nombre: '' };
  }

  delCategoria(c: Categoria) {
    if (!confirm('Eliminar categoría?')) return;
    this.categoriaService.delete({ id: c.id! }).subscribe(() => this.loadAll());
  }

  startEditProducto(p: Producto) {
    this.productoForm = { ...p, categoriaId: p.categoria?.id ?? p.categoria };
  }
  cancelProducto() { this.productoForm = { nombre: '', precio: 0, tipo: 'bebida', categoriaId: null, stockActual: null, umbralAlerta: null }; }

  saveProducto() {
    const payload: any = {
      nombre: this.productoForm.nombre,
      precio: this.productoForm.precio,
      tipo: this.productoForm.tipo,
      categoria: this.categorias.find(c => c.id === this.productoForm.categoriaId) ?? null,
      stockActual: this.productoForm.stockActual,
      umbralAlerta: this.productoForm.umbralAlerta
    };

    if (this.productoForm.id) {
      payload.id = this.productoForm.id;
      this.productoService.update(payload).subscribe(() => this.loadAll());
    } else {
      this.productoService.create(payload).subscribe(() => this.loadAll());
    }

    this.cancelProducto();
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
