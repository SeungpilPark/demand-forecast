package com.woowahan.market.forecast.redshift;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.woowahan.market.forecast.context.ContextHolder;
import com.woowahan.market.forecast.excel.ItemRow;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class RedshiftClient {

  public static ArrayList<ItemRow> connectCluster(String dbURL, String masterUsername,
      String masterUserPassword, int limit) throws SQLException {
    LambdaLogger logger = ContextHolder.getContext().getLogger();

    Connection conn = null;
    Statement stmt = null;
    try {
      //Open a connection and define properties.
      logger.log("Connecting to database...");
      Properties props = new Properties();

      //Uncomment the following line if using a keystore.
      //props.setProperty("ssl", "true");
      props.setProperty("user", masterUsername);
      props.setProperty("password", masterUserPassword);
      conn = DriverManager.getConnection(dbURL, props);

      //Try a simple query.
      logger.log("Listing system tables...");
      stmt = conn.createStatement();
      String sql;
      sql = "select\n"
          + "  sku.code as sku_code,\n"
          + "  sku.name as sku_name,\n"
          + "  sku.storage_method,\n"
          + "  sku.supplier_id,\n"
          + "  sku.supplier_name,\n"
          + "  sku.supplier_md_id,\n"
          + "  sku.supplier_md_name,\n"
          + "  center.code as center_code,\n"
          + "  center.name as center_name,\n"
          + "  forecast.date_qty as date_qty \n"
          + "from\n"
          + "(\n"
          + "\tselect \n"
          + "\t  sku_code,\n"
          + "\t  center_code,\n"
          + "\t  listagg(date || ',' || qty, ';') within group (order by date) as date_qty\n"
          + "\tfrom \n"
          + "\t  spectrum.step80_forecast_qty\n"
          + "\twhere \n"
          + "\t  jobid='28921229-f194-4d03-97b8-266f152def8f'\n"
          + "\tgroup by sku_code,center_code\n"
          + ")  forecast\n"
          + "left join\n"
          + "\t(select * from spectrum.sku where date = '2021-02-04') sku  \n"
          + "on\n"
          + "\tsku.code = forecast.sku_code\n"
          + "left join\n"
          + "\t(select * from spectrum.center where date = '2021-02-04') center  \n"
          + "on\n"
          + "\tcenter.code = forecast.center_code\t\n"
          + "order by sku.code,center.code\n"
          + "limit " + limit;
      ResultSet rs = stmt.executeQuery(sql);

      //Get the data from the result set.
      ArrayList<ItemRow> itemRows = new ArrayList<>();
      while (rs.next()) {
        itemRows.add(
            ItemRow.builder()
                .skuCode(rs.getString("sku_code"))
                .skuName(rs.getString("sku_name"))
                .storageMethod(rs.getString("storage_method"))
                .supplierName(rs.getString("supplier_name"))
                .supplierMdName(rs.getString("supplier_md_name"))
                .centerCode(rs.getString("center_code"))
                .centerName(rs.getString("center_name"))
                .data(rs.getString("date_qty"))
                .build()
        );
      }
      rs.close();
      stmt.close();
      conn.close();
      return itemRows;
    } finally {
      if (stmt != null) {
        stmt.close();
      }
      if (conn != null) {
        conn.close();
      }
    }
  }
}
