import { Routes } from '@angular/router';
import { PedidoComponent } from './pedido/pedido';
import { InventarioComponent } from './inventario/inventario';
import { ReportesComponent } from './reportes/reportes.component';

export const routes: Routes = [
	{ path: '', redirectTo: 'pedido', pathMatch: 'full' },
	{ path: 'pedido', component: PedidoComponent },
	{ path: 'inventario', component: InventarioComponent },
	{ path: 'reportes', component: ReportesComponent }
];
