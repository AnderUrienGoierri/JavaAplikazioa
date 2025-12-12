package birtek_interfaze_grafikoa;

/*
 Kodea garbi eta profesional mantentzeko, lehenik eta behin utilitate klase txiki bat sortu dut taulak betetzeko. 
 Horrek menu bakoitzean 50 kode lerro errepikatzea saihestuko dizu.
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class TablaModelador {

    public static DefaultTableModel construirModelo(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // 1. Obtener nombres de columnas
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // 2. Obtener datos de las filas
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }
}