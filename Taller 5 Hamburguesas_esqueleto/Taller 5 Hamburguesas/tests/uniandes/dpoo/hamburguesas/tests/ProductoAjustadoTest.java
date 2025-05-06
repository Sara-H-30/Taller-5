package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

class ProductoAjustadoTest {

    private ProductoMenu productoBase;
    private ProductoAjustado productoAjustado;

    @BeforeEach
    void setUp() {
        productoBase = new ProductoMenu("corral", 14000);
        productoAjustado = new ProductoAjustado(productoBase);

    }

    @Test
    void testConstructorYGetNombre() {
        assertEquals("corral", productoAjustado.getNombre(),
                "El nombre del producto ajustado debe ser el mismo que el del producto base.");
    }

    @Test
    void testGetPrecioActual() {

        assertEquals(0, productoAjustado.getPrecio(),
                "ERROR ESPERADO: getPrecio() actualmente devuelve 0, independientemente del precio base o ajustes.");
        System.out.println("INFO: testGetPrecioActual verifica que getPrecio() retorna 0, como en el código actual.");
    }

    @Test
    void testGenerarTextoFacturaActual() {
        
        String baseProductString = productoBase.toString(); 


        String expectedFactura = baseProductString +
                                 "            " + 0 + "\n"; 

        String actualFactura = productoAjustado.generarTextoFactura();


        assertTrue(actualFactura.contains(productoBase.getClass().getName()), "La factura debe contener el nombre de la clase base.");
        assertTrue(actualFactura.endsWith("            0\n"), "La factura debe terminar con el precio actual (0).");

        System.out.println("INFO: testGenerarTextoFacturaActual verifica la salida con listas de ajuste vacías y precio 0.");
       
    }
}