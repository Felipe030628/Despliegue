package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Modelo.Productos;

public class ProductosDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Obtiene el listado completo de productos.
     */
    public List<Productos> listarProductos() {
        List<Productos> lista = new ArrayList<>();
        String sql = "{call sp_listar_productos()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Productos p = new Productos();
                p.setId(rs.getInt("idProductos"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setFecha_vencimiento(rs.getString("fecha_vencimiento"));
                p.setIdCategoria(rs.getInt("categorias_idCategorias"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar los productos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Registra un nuevo producto en la base de datos.
     */
    public void registrarProducto(Productos p) {
        String sql = "{call sp_registrar_producto(?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, p.getNombre());
            cs.setDouble(2, p.getPrecio());
            cs.setString(3, p.getFecha_vencimiento());
            cs.setInt(4, p.getIdCategoria());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al registrar el producto: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de un producto existente.
     */
    public void actualizarProducto(Productos p) {
        String sql = "{call sp_actualizar_producto(?, ?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, p.getId());
            cs.setString(2, p.getNombre());
            cs.setDouble(3, p.getPrecio());
            cs.setString(4, p.getFecha_vencimiento());
            cs.setInt(5, p.getIdCategoria());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar el producto (" + p.getId() + "): " + e.getMessage());
        }
    }

    /**
     * Elimina un producto por su ID.
     */
    public void eliminarProducto(int id) {
        String sql = "{call sp_eliminar_producto(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto (" + id + "): " + e.getMessage());
        }
    }

    /**
     * Obtiene el total de productos registrados en el sistema.
     */
    public int contarProductosCriticos() {
        int total = 0;
        String sql = "{call sp_contar_productos_criticos_total()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar productos críticos: " + e.getMessage());
        }

        return total;
    }

    /**
     * Obtiene la cantidad total de marcas/productos registrados.
     */
    public int contarTotalMarcas() {
        int total = 0;
        String sql = "{call sp_contar_total_marcas()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar total de marcas: " + e.getMessage());
        }

        return total;
    }

    /**
     * Obtiene la distribución de productos agrupados por categoría.
     */
    public List<Map<String, Object>> obtenerDistribucionCategorias() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "{call sp_obtener_distribucion_categorias()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("categoria", rs.getString("categoria"));
                fila.put("total", rs.getInt("total"));
                lista.add(fila);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener distribución por categorías: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene la lista de productos calculando su stock actual según movimientos.
     */
    public List<Productos> listarProductosConStock() {
        List<Productos> lista = new ArrayList<>();
        String sql = "{call sp_listar_productos_con_stock()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Productos p = new Productos();
                p.setId(rs.getInt("idProductos"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setIdCategoria(rs.getInt("categorias_idCategorias"));
                p.setStock(rs.getInt("stock"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar productos con stock: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene el stock disponible de un producto específico.
     */
    public int obtenerStock(int idProducto) {
        int stock = 0;
        String sql = "{call sp_obtener_stock_producto(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idProducto);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    stock = rs.getInt("stock");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener stock del producto (" + idProducto + "): " + e.getMessage());
        }

        return stock;
    }

    /**
     * Consulta un producto por su ID.
     */
    public Productos listarPorId(int id) {
        Productos p = new Productos();
        String sql = "{call sp_buscar_producto_por_id(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    p.setId(rs.getInt("idProductos"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setFecha_vencimiento(rs.getString("fecha_vencimiento"));
                    p.setIdCategoria(rs.getInt("categorias_idCategorias"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID (" + id + "): " + e.getMessage());
        }

        return p;
    }

    /**
     * Busca productos por coincidencia en el nombre con un límite de resultados.
     */
    public List<Productos> buscarPorNombre(String termino, int limite) {
        List<Productos> lista = new ArrayList<>();
        String sql = "{call sp_buscar_productos_por_nombre(?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, termino);
            cs.setInt(2, limite);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Productos p = new Productos();
                    p.setId(rs.getInt("idProductos"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setFecha_vencimiento(rs.getString("fecha_vencimiento"));
                    p.setIdCategoria(rs.getInt("categorias_idCategorias"));
                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar productos por nombre: " + e.getMessage());
        }

        return lista;
    }
}