package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class ProductoMenuTest {

    @Test
    void testConstructorYGetters() {
        ProductoMenu producto = new ProductoMenu("Hamburguesa Sencilla", 15000);
        assertEquals("Hamburguesa Sencilla", producto.getNombre(), "El nombre del producto no es el esperado.");
        assertEquals(15000, producto.getPrecio(), "El precio del producto no es el esperado.");
    }

    @Test
    void testGenerarTextoFactura() {
        ProductoMenu producto = new ProductoMenu("Papas Fritas", 5000);
        String expectedFactura = "Papas Fritas\n            5000\n";
        assertEquals(expectedFactura, producto.generarTextoFactura(), "El texto de la factura no coincide con el esperado.");
    }
}