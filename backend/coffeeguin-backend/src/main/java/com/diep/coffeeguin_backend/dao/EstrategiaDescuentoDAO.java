package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.DescuentoFijo;
import com.diep.coffeeguin_backend.model.DescuentoPorcentaje;
import com.diep.coffeeguin_backend.model.EstrategiaDescuento;

import java.util.List;

public interface EstrategiaDescuentoDAO {

	void agregar(EstrategiaDescuento estrategia);

	void eliminar(EstrategiaDescuento estrategia);

	EstrategiaDescuento buscarPorId(int id);

	List<EstrategiaDescuento> listarTodas();

	List<EstrategiaDescuento> listarActivas();

	void actualizar(EstrategiaDescuento estrategia);

	List<DescuentoPorcentaje> listarPorcentajes();

	List<DescuentoFijo> listarFijos();
}
