package birtek_interfaze_grafikoa;
import java.util.HashMap;
import java.util.Map;

public class Hizkuntza {
    
    // Uneko hizkuntza (Defektuz: Euskera)
    public static String selectedLang = "EU"; 

    private static final Map<String, String[]> dictionary = new HashMap<>();

    static {
        // Formatua: { "CLAVE", { "EU", "ES", "EN" } }
        
        // --- OROKORRAK / GENERAL ---
        add("app_title", "Birtek Kudeaketa", "Gestión Birtek", "Birtek Management");
        add("login_title", "Saioa Hasi", "Iniciar Sesión", "Login");
        add("login_btn", "Sartu", "Entrar", "Login");
        add("email", "Emaila:", "Email:", "Email:");
        add("pass", "Pasahitza:", "Contraseña:", "Password:");
        add("search", "Bilatu:", "Buscar:", "Search:");
        add("logout", "Saioa Itxi", "Cerrar Sesión", "Logout");
        add("logout_confirm", "Ziur zaude saioa itxi nahi duzula?", "¿Seguro que quieres salir?", "Are you sure you want to log out?");
        add("refresh", "Eguneratu", "Actualizar", "Refresh");
        add("load_data", "Datuak Kargatu", "Cargar Datos", "Load Data");
        add("check_db", "Egiaztatu DB", "Verificar BD", "Check DB");
        add("exit", "Irten", "Salir", "Exit");
        
        // --- ADMINISTRAZIOA ---
        add("menu_admin", "ADMINISTRAZIOA", "ADMINISTRACIÓN", "ADMINISTRATION");
        add("tab_employees", "Langileak", "Empleados", "Employees");
        
        // --- TEKNIKOA ---
        add("menu_tech", "TEKNIKOA (SAT)", "TÉCNICO (SAT)", "TECHNICAL (SAT)");
        add("tab_repairs", "Konponketak", "Reparaciones", "Repairs");
        add("tab_products", "Produktuak", "Productos", "Products");
        
        // --- SALMENTAK ---
        add("menu_sales", "SALMENTAK", "VENTAS", "SALES");
        add("tab_clients", "Bezeroak", "Clientes", "Clients");
        add("tab_orders", "Eskaerak", "Pedidos", "Orders");
        
        // --- LOGISTIKA ---
        add("menu_logistics", "LOGISTIKA", "LOGÍSTICA", "LOGISTICS");
        add("lbl_logistics", "Hornitzaileen Sarrerak", "Entradas de Proveedores", "Supplier Entries");
        
        // --- SISTEMAK ---
        add("menu_systems", "SISTEMAK", "SISTEMAS", "SYSTEMS");
        add("sys_title", "Sistemak eta Kudeaketa", "Sistemas y Gestión", "Systems & Management");
        
        // --- DIALOGOAK ---
        add("dialog_rep_title", "Editatu Konponketa", "Editar Reparación", "Edit Repair");
        add("status", "Egoera:", "Estado:", "Status:");
        add("notes", "Oharrak:", "Notas:", "Notes:");
        add("save", "GORDE", "GUARDAR", "SAVE");
    }

    private static void add(String key, String eu, String es, String en) {
        dictionary.put(key, new String[]{eu, es, en});
    }

    public static String get(String key) {
        String[] values = dictionary.get(key);
        if (values == null) return "MISSING: " + key;
        
        switch (selectedLang) {
            case "ES": return values[1];
            case "EN": return values[2];
            default: return values[0]; // EU
        }
    }
}