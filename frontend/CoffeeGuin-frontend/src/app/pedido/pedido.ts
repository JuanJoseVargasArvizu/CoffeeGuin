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
  error = '';

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
      },
      error: () => (this.error = 'No se pudo cargar el menu')
    });
  }
}

interface Categoria { id?: number; nombre: string; productos?: any[] }
