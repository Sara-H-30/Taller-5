package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class ProductoMenuTest {

    @Test
    void testConstructorYGetters() {
        ProductoMenu producto = new ProductoMenu("corral", 14000);
        assertEquals("corral", producto.getNombre(), "El nombre del producto no es el esperado.");
        assertEquals(14000, producto.getPrecio(), "El precio del producto no es el esperado.");
    }

    @Test
    void testGenerarTextoFactura() {
        ProductoMenu producto = new ProductoMenu("papas medianas", 5500);
        String expectedFactura = "papas medianas\n            5500\n";
        assertEquals(expectedFactura, producto.generarTextoFactura(), "El texto de la factura no coincide con el esperado.");
    }
}



 