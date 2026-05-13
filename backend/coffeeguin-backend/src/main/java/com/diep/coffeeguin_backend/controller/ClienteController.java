package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Cliente;
import com.diep.coffeeguin_backend.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	@GetMapping
	public ResponseEntity<List<Cliente>> listarClientes() {
		return ResponseEntity.ok(clienteService.consultarTodos());
	}

	@GetMapping("/activos")
	public ResponseEntity<List<Cliente>> listarClientesActivos() {
		return ResponseEntity.ok(clienteService.consultarActivos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Cliente> obtenerCliente(@PathVariable int id) {
		try {
			return ResponseEntity.ok(clienteService.consultarPorId(id));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<Cliente> obtenerClientePorEmail(@PathVariable String email) {
		try {
			return ResponseEntity.ok(clienteService.consultarPorEmail(email));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/telefono/{telefono}")
	public ResponseEntity<Cliente> obtenerClientePorTelefono(@PathVariable String telefono) {
		try {
			return ResponseEntity.ok(clienteService.consultarPorTelefono(telefono));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/estrategia/{estrategiaId}")
	public ResponseEntity<List<Cliente>> obtenerClientesPorEstrategia(@PathVariable int estrategiaId) {
		return ResponseEntity.ok(clienteService.consultarClientesPorEstrategia(estrategiaId));
	}

	@PostMapping
	public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
		try {
			clienteService.registrarNuevoCliente(cliente);
			return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Cliente> actualizarCliente(@PathVariable int id, @RequestBody Cliente cliente) {
		try {
			cliente.setId(id);
			clienteService.actualizarCliente(cliente);
			return ResponseEntity.ok(cliente);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@PatchMapping("/{id}/contacto")
	public ResponseEntity<Cliente> actualizarContacto(
			@PathVariable int id,
			@RequestBody ClienteContactoUpdateRequest updateData) {
		try {
			clienteService.actualizarInformacionContacto(id, updateData.getEmail(), updateData.getTelefono(), updateData.getDireccion());
			return ResponseEntity.ok(clienteService.consultarPorId(id));
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@PatchMapping("/{id}/preferencias")
	public ResponseEntity<Cliente> actualizarPreferencias(
			@PathVariable int id,
			@RequestBody ClientePreferenciasUpdateRequest updateData) {
		try {
			clienteService.actualizarPreferencias(id, updateData.getPreferencias(), updateData.getAlergias(), updateData.getBebidaFavorita(), updateData.getPlatoFavorito());
			return ResponseEntity.ok(clienteService.consultarPorId(id));
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/{id}/descuentos")
	public ResponseEntity<Cliente> asignarDescuento(
			@PathVariable int id,
			@RequestBody AsignarDescuentoRequest request) {
		try {
			clienteService.asignarEstrategiaDescuento(id, request.getEstrategiaId());
			return ResponseEntity.ok(clienteService.consultarPorId(id));
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}/descuentos")
	public ResponseEntity<Cliente> eliminarDescuento(@PathVariable int id) {
		try {
			clienteService.eliminarEstrategiaDescuento(id);
			return ResponseEntity.ok(clienteService.consultarPorId(id));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/{id}/precio-descuento")
	public ResponseEntity<DescuentoResponse> calcularPrecioConDescuento(
			@PathVariable int id,
			@RequestParam double total) {
		try {
			double precioConDescuento = clienteService.calcularPrecioConDescuento(id, total);
			double descuentoAplicado = total - precioConDescuento;
			return ResponseEntity.ok(new DescuentoResponse(total, precioConDescuento, descuentoAplicado));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@PatchMapping("/{id}/desactivar")
	public ResponseEntity<Cliente> desactivarCliente(@PathVariable int id) {
		try {
			clienteService.desactivarCliente(id);
			return ResponseEntity.ok(clienteService.consultarPorId(id));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@PatchMapping("/{id}/reactivar")
	public ResponseEntity<Cliente> reactivarCliente(@PathVariable int id) {
		try {
			clienteService.reactivarCliente(id);
			return ResponseEntity.ok(clienteService.consultarPorId(id));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarCliente(@PathVariable int id) {
		try {
			clienteService.eliminarCliente(id);
			return ResponseEntity.noContent().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	public static class ClienteContactoUpdateRequest {
		private String email;
		private String telefono;
		private String direccion;

		public String getEmail() { return email; }
		public void setEmail(String email) { this.email = email; }
		public String getTelefono() { return telefono; }
		public void setTelefono(String telefono) { this.telefono = telefono; }
		public String getDireccion() { return direccion; }
		public void setDireccion(String direccion) { this.direccion = direccion; }
	}

	public static class ClientePreferenciasUpdateRequest {
		private String preferencias;
		private String alergias;
		private String bebidaFavorita;
		private String platoFavorito;

		public String getPreferencias() { return preferencias; }
		public void setPreferencias(String preferencias) { this.preferencias = preferencias; }
		public String getAlergias() { return alergias; }
		public void setAlergias(String alergias) { this.alergias = alergias; }
		public String getBebidaFavorita() { return bebidaFavorita; }
		public void setBebidaFavorita(String bebidaFavorita) { this.bebidaFavorita = bebidaFavorita; }
		public String getPlatoFavorito() { return platoFavorito; }
		public void setPlatoFavorito(String platoFavorito) { this.platoFavorito = platoFavorito; }
	}

	public static class DescuentoResponse {
		private double totalOriginal;
		private double totalConDescuento;
		private double montoDescuento;

		public DescuentoResponse(double totalOriginal, double totalConDescuento, double montoDescuento) {
			this.totalOriginal = totalOriginal;
			this.totalConDescuento = totalConDescuento;
			this.montoDescuento = montoDescuento;
		}

		public double getTotalOriginal() { return totalOriginal; }
		public void setTotalOriginal(double totalOriginal) { this.totalOriginal = totalOriginal; }
		public double getTotalConDescuento() { return totalConDescuento; }
		public void setTotalConDescuento(double totalConDescuento) { this.totalConDescuento = totalConDescuento; }
		public double getMontoDescuento() { return montoDescuento; }
		public void setMontoDescuento(double montoDescuento) { this.montoDescuento = montoDescuento; }
	}

	public static class AsignarDescuentoRequest {
		private int estrategiaId;

		public int getEstrategiaId() { return estrategiaId; }
		public void setEstrategiaId(int estrategiaId) { this.estrategiaId = estrategiaId; }
	}
}
