import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="app-shell">
      <aside class="sidebar">
        <h2>CoffeeGuin</h2>
        <nav>
          <a routerLink="/pedido" routerLinkActive="active">Pedido</a>
          <a routerLink="/inventario" routerLinkActive="active">Inventario</a>
          <a routerLink="/reportes" routerLinkActive="active">Reportes</a>
        </nav>
      </aside>

      <main class="content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [
    `:host{display:block;font-family:Arial,Helvetica,sans-serif}
    .app-shell{display:flex;min-height:100vh}
    .sidebar{width:220px;padding:20px;background:#f7f6f3;border-right:1px solid #e7e3dc}
    .sidebar h2{margin:0 0 12px 0}
    .sidebar nav a{display:block;padding:8px 10px;margin:6px 0;color:#2f2a25;text-decoration:none;border-radius:6px}
    .sidebar nav a.active{background:#3e5f56;color:#fff}
    .content{flex:1;padding:20px}
    `
  ]
})
export class App {}
