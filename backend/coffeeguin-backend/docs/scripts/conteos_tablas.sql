-- Fase 0.3: volúmenes por tabla antes de migraciones destructivas (Fase 1+).
-- Ejecutar contra la base real o una copia.

SELECT 'categoria' AS tabla, COUNT(*) AS filas FROM categoria
UNION ALL SELECT 'producto', COUNT(*) FROM producto
UNION ALL SELECT 'ingrediente', COUNT(*) FROM ingrediente
UNION ALL SELECT 'producto_receta', COUNT(*) FROM producto_receta
UNION ALL SELECT 'cliente', COUNT(*) FROM cliente
UNION ALL SELECT 'estrategia_descuento', COUNT(*) FROM estrategia_descuento
UNION ALL SELECT 'venta', COUNT(*) FROM venta
UNION ALL SELECT 'venta_producto', COUNT(*) FROM venta_producto
UNION ALL SELECT 'mesa', COUNT(*) FROM mesa
UNION ALL SELECT 'asiento', COUNT(*) FROM asiento
UNION ALL SELECT 'reporte', COUNT(*) FROM reporte;
