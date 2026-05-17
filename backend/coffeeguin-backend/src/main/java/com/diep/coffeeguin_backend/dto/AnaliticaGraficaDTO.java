package com.diep.coffeeguin_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class AnaliticaGraficaDTO {

	private List<String> labels = new ArrayList<>();
	private List<Integer> seriesCantidad = new ArrayList<>();
	private List<Double> seriesIngresos = new ArrayList<>();

	public AnaliticaGraficaDTO() {
	}

	public AnaliticaGraficaDTO(List<String> labels, List<Integer> seriesCantidad, List<Double> seriesIngresos) {
		this.labels = labels;
		this.seriesCantidad = seriesCantidad;
		this.seriesIngresos = seriesIngresos;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<Integer> getSeriesCantidad() {
		return seriesCantidad;
	}

	public void setSeriesCantidad(List<Integer> seriesCantidad) {
		this.seriesCantidad = seriesCantidad;
	}

	public List<Double> getSeriesIngresos() {
		return seriesIngresos;
	}

	public void setSeriesIngresos(List<Double> seriesIngresos) {
		this.seriesIngresos = seriesIngresos;
	}
}