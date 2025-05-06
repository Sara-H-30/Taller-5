package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import uniandes.dpoo.hamburguesas.excepciones.*; 
import uniandes.dpoo.hamburguesas.mundo.*; 

class RestauranteTest {

    private Restaurante restaurante;
    private static final String TEST_DATA_PATH = "test/data/"; 
    private static final String FACTURAS_PATH = "./facturas/"; 


    private File createTestFile(Path dir, String name, String content) throws IOException {
        File file = dir.resolve(name).toFile();
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println(content);
        }
        return file;
    }

  
    private void resetNumeroPedidos() {
        try {
            Field field = Pedido.class.getDeclaredField("numeroPedidos");
            field.setAccessible(true);
            field.setInt(null, 0); // Reset static field to 0
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Could not reset numeroPedidos static counter", e);
        }
    }

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        resetNumeroPedidos(); 

       
        File facturasDir = new File(FACTURAS_PATH);
        if (facturasDir.exists()) {
             for (File f : facturasDir.listFiles()) {
                if (f.getName().startsWith("factura_")) {
                    f.delete();
                }
             }
        } else {
             facturasDir.mkdirs(); 
        }

         
        File dataDir = new File(TEST_DATA_PATH);
        if (!dataDir.exists()) dataDir.mkdirs();

        try {
            File ingredientesFile = new File(TEST_DATA_PATH + "ingredientes.txt");
            if (!ingredientesFile.exists()) {
                 try(PrintWriter out = new PrintWriter(ingredientesFile)) {
                     out.println("lechuga;1000");
                     out.println("tomate;1000");
                     out.println("queso americano;2500");
                 }
            }
             File menuFile = new File(TEST_DATA_PATH + "menu.txt");
             if (!menuFile.exists()) {
                  try(PrintWriter out = new PrintWriter(menuFile)) {
                      out.println("corral;14000");
                      out.println("papas medianas;5500");
                      out.println("gaseosa;5000");
                  }
             }
             File combosFile = new File(TEST_DATA_PATH + "combos.txt");
             if (!combosFile.exists()) {
                  try(PrintWriter out = new PrintWriter(combosFile)) {
                      out.println("combo corral;10%;corral;papas medianas;gaseosa");
                  }
             }
        } catch (IOException e) {
            fail("Setup failed: Could not create base test files.", e);
        }
    }

     @AfterEach
     void tearDown() {
         resetNumeroPedidos();
        
         File facturasDir = new File(FACTURAS_PATH);
         if (facturasDir.exists()) {
              for (File f : facturasDir.listFiles()) {
                 if (f.getName().startsWith("factura_")) {
                     f.delete();
                 }
              }
             
         }
     }

    @Test
    void testConstructorInicial() {
        assertNull(restaurante.getPedidoEnCurso(), "No debe haber pedido en curso al inicio.");
        assertNotNull(restaurante.getPedidos(), "La lista de pedidos cerrados no debe ser null.");
        assertTrue(restaurante.getPedidos().isEmpty(), "La lista de pedidos cerrados debe estar vacía.");
        assertNotNull(restaurante.getIngredientes(), "La lista de ingredientes no debe ser null.");
        assertTrue(restaurante.getIngredientes().isEmpty(), "La lista de ingredientes debe estar vacía.");
        assertNotNull(restaurante.getMenuBase(), "La lista de menú base no debe ser null.");
        assertTrue(restaurante.getMenuBase().isEmpty(), "La lista de menú base debe estar vacía.");
        assertNotNull(restaurante.getMenuCombos(), "La lista de combos no debe ser null.");
        assertTrue(restaurante.getMenuCombos().isEmpty(), "La lista de combos debe estar vacía.");
    }

    @Test
    void testCargarInformacionRestauranteOK() throws HamburguesaException, NumberFormatException, IOException {
     
        File ingredientes = new File(TEST_DATA_PATH + "ingredientes.txt");
        File menu = new File(TEST_DATA_PATH + "menu.txt");
        File combos = new File(TEST_DATA_PATH + "combos.txt");

    
        restaurante.cargarInformacionRestaurante(ingredientes, menu, combos);


        assertFalse(restaurante.getIngredientes().isEmpty(), "Ingredientes no se cargaron.");
        assertEquals(3, restaurante.getIngredientes().size(), "Numero incorrecto de ingredientes.");
        assertFalse(restaurante.getMenuBase().isEmpty(), "Menu base no se cargó.");
        assertEquals(3, restaurante.getMenuBase().size(), "Numero incorrecto de productos menu.");
        assertFalse(restaurante.getMenuCombos().isEmpty(), "Combos no se cargaron.");
        assertEquals(1, restaurante.getMenuCombos().size(), "Numero incorrecto de combos.");
        assertEquals("combo corral", restaurante.getMenuCombos().get(0).getNombre());
    }

     @Test
     void testCargarInformacionIngredienteRepetido(@TempDir Path tempDir) throws IOException {
       
         File menu = new File(TEST_DATA_PATH + "menu.txt");
         File combos = new File(TEST_DATA_PATH + "combos.txt");
         File ingredientesRep = createTestFile(tempDir, "ing_rep.txt", "lechuga;1000\ntomate;1000\nlechuga;1500"); 

       
         assertThrows(IngredienteRepetidoException.class, () -> {
             restaurante.cargarInformacionRestaurante(ingredientesRep, menu, combos);
         }, "Debería lanzar IngredienteRepetidoException.");
     }

    @Test
    void testCargarInformacionProductoMenuRepetido(@TempDir Path tempDir) throws IOException {
       
        File ingredientes = new File(TEST_DATA_PATH + "ingredientes.txt");
        File combos = new File(TEST_DATA_PATH + "combos.txt");
        File menuRep = createTestFile(tempDir, "menu_rep.txt", "corral;14000\npapas medianas;5500\ncorral;15000"); // Duplicate

     
        assertThrows(ProductoRepetidoException.class, () -> {
            restaurante.cargarInformacionRestaurante(ingredientes, menuRep, combos);
        }, "Debería lanzar ProductoRepetidoException para menu.");
    }

     @Test
    void testCargarInformacionComboRepetido(@TempDir Path tempDir) throws IOException {
 
        File ingredientes = new File(TEST_DATA_PATH + "ingredientes.txt");
        File menu = new File(TEST_DATA_PATH + "menu.txt");
        File combosRep = createTestFile(tempDir, "combo_rep.txt", "combo corral;10%;corral;papas medianas\ncombo especial;8%;gaseosa\ncombo corral;12%;corral;gaseosa"); // Duplicate

  
        assertThrows(ProductoRepetidoException.class, () -> {
            restaurante.cargarInformacionRestaurante(ingredientes, menu, combosRep);
        }, "Debería lanzar ProductoRepetidoException para combos.");
    }

     @Test
    void testCargarInformacionProductoFaltanteEnCombo(@TempDir Path tempDir) throws IOException {
 
        File ingredientes = new File(TEST_DATA_PATH + "ingredientes.txt");
        File menu = new File(TEST_DATA_PATH + "menu.txt"); // Does not contain 'hamburguesa especial'
        File comboFaltante = createTestFile(tempDir, "combo_falt.txt", "combo especial;10%;hamburguesa especial;papas medianas"); // Missing item

   
        assertThrows(ProductoFaltanteException.class, () -> {
            restaurante.cargarInformacionRestaurante(ingredientes, menu, comboFaltante);
        }, "Debería lanzar ProductoFaltanteException.");
    }

    @Test
    void testCargarInformacionFileNotFound() {
  
        File ingredientes = new File("path/inexistente/ingredientes.txt");
        File menu = new File(TEST_DATA_PATH + "menu.txt");
        File combos = new File(TEST_DATA_PATH + "combos.txt");

 
        assertThrows(FileNotFoundException.class, () -> { // Expecting specific exception from FileReader
            restaurante.cargarInformacionRestaurante(ingredientes, menu, combos);
        }, "Debería lanzar FileNotFoundException.");
    }


    @Test
    void testIniciarPedidoOK() throws YaHayUnPedidoEnCursoException {
      
        String nombre = "Cliente Nuevo";
        String dir = "Dir Nueva";

     
        restaurante.iniciarPedido(nombre, dir);
        Pedido enCurso = restaurante.getPedidoEnCurso();

     
        assertNotNull(enCurso, "Debe haber un pedido en curso.");
        assertEquals(nombre, enCurso.getNombreCliente());
        assertEquals(0, enCurso.getIdPedido()); // First order ID
    }

    @Test
    void testIniciarPedidoYaExistente() throws YaHayUnPedidoEnCursoException {
      
        restaurante.iniciarPedido("Cliente 1", "Dir 1");

      
        assertThrows(YaHayUnPedidoEnCursoException.class, () -> {
            restaurante.iniciarPedido("Cliente 2", "Dir 2");
        }, "Debería lanzar YaHayUnPedidoEnCursoException.");
    }

    @Test
    void testCerrarYGuardarPedidoOK() throws Exception { 
   
         restaurante.cargarInformacionRestaurante(
             new File(TEST_DATA_PATH + "ingredientes.txt"),
             new File(TEST_DATA_PATH + "menu.txt"),
             new File(TEST_DATA_PATH + "combos.txt")
         );
         restaurante.iniciarPedido("Cliente Final", "Dir Final");
         Pedido pedidoActivo = restaurante.getPedidoEnCurso();
         assertNotNull(pedidoActivo);
         pedidoActivo.agregarProducto(restaurante.getMenuBase().get(0)); 
         int idPedido = pedidoActivo.getIdPedido(); 
         String nombreArchivoEsperado = "factura_" + idPedido + ".txt";
         File archivoFacturaEsperado = new File(FACTURAS_PATH + nombreArchivoEsperado);
         String facturaContenidoEsperado = pedidoActivo.generarTextoFactura();

       
         assertFalse(archivoFacturaEsperado.exists(), "Archivo factura no deberia existir antes de cerrar.");

         restaurante.cerrarYGuardarPedido();

         assertNull(restaurante.getPedidoEnCurso(), "El pedido en curso deberia ser null despues de cerrar.");
         assertTrue(archivoFacturaEsperado.exists(), "Archivo factura deberia existir despues de cerrar.");

         String contenidoLeido = Files.readString(archivoFacturaEsperado.toPath());
         assertEquals(facturaContenidoEsperado, contenidoLeido, "Contenido de factura guardada no coincide.");

         assertTrue(restaurante.getPedidos().isEmpty(), "La lista 'pedidos' deberia estar vacia (implementacion actual).");

         archivoFacturaEsperado.delete();
    }

    @Test
    void testCerrarPedidoSinPedidoEnCurso() {

        assertThrows(NoHayPedidoEnCursoException.class, () -> {
            restaurante.cerrarYGuardarPedido();
        }, "Debería lanzar NoHayPedidoEnCursoException.");
    }

    @Test
    void testGetters() throws Exception {

         File ingredientes = new File(TEST_DATA_PATH + "ingredientes.txt");
         File menu = new File(TEST_DATA_PATH + "menu.txt");
         File combos = new File(TEST_DATA_PATH + "combos.txt");
         restaurante.cargarInformacionRestaurante(ingredientes, menu, combos);


         assertNull(restaurante.getPedidoEnCurso());
         assertTrue(restaurante.getPedidos().isEmpty());
         assertEquals(3, restaurante.getMenuBase().size());
         assertEquals(1, restaurante.getMenuCombos().size());
         assertEquals(3, restaurante.getIngredientes().size());


         restaurante.iniciarPedido("Test Client", "Test Addr");
         assertNotNull(restaurante.getPedidoEnCurso());
         assertTrue(restaurante.getPedidos().isEmpty()); 


         restaurante.cerrarYGuardarPedido();
         assertNull(restaurante.getPedidoEnCurso());
     
         assertTrue(restaurante.getPedidos().isEmpty()); 

         File facturaGen = new File(FACTURAS_PATH + "factura_0.txt");
         if (facturaGen.exists()) {
             facturaGen.delete();
         }
    }
}