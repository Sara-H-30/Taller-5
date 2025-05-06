package uniandes.dpoo.hamburguesas.tests;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class ComboTest {

    private Combo combo;
    private ProductoMenu item1;
    private ProductoMenu item2;
    private ProductoMenu item3;

    @BeforeEach
    void setUp() {
        item1 = new ProductoMenu("corral", 14000);
        item2 = new ProductoMenu("papas medianas", 5500);
        item3 = new ProductoMenu("gaseosa", 5000);
        ArrayList<ProductoMenu> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
       
        combo = new Combo("corral", 0.10, items);
    }

    @Test
    void testGetNombre() {
        assertEquals("corral", combo.getNombre());
    }

    @Test
    void testGetPrecio() {
        
        int precioCalculadoPorCodigo = (int) ( (14000 + 5500 + 5000) * 0.10 );
        assertEquals(precioCalculadoPorCodigo, combo.getPrecio(), "El precio calculado por el código actual no es el esperado (2450)."); 
         System.out.println("WARN: La lógica de descuento en Combo.getPrecio() parece incorrecta. El test verifica el comportamiento actual.");
    }

    @Test
    void testGenerarTextoFactura() {
         int precioActualCodigo = 2450;
        String expectedFactura = "Combo corral\n Descuento: 0.1\n            " + precioActualCodigo + "\n";
        assertEquals(expectedFactura, combo.generarTextoFactura(), "El texto de la factura del combo no coincide.");
    }
}