package jeql.command.db.sde;

import jeql.engine.ConfigurationException;
import jeql.std.geom.GeomFunction;

import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeError;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeExtent;
import com.esri.sde.sdk.client.SeTable;
import com.vividsolutions.jts.geom.Geometry;

public class SdeUtil {
  public static boolean checkSdePresent() throws ConfigurationException {
    try {
      Class.forName("com.esri.sde.sdk.client.SeConnection");
    } catch (ClassNotFoundException e) {
      throw new ConfigurationException("ESRI ArcSDE libraries not found");
    }
    return true;
  }

  public static String[] fetchColumnNames(SeTable tbl) throws SeException {
    SeColumnDefinition[] col = tbl.describe();
    String[] name = new String[col.length];
    for (int i = 0; i < name.length; i++) {
      name[i] = col[i].getName();
    }
    return name;
  }

  public static SeConnection getConnection(String url, String user,
      String password) throws SeException {
    SdeConnectionUrlParser urlParser = new SdeConnectionUrlParser(url);
    SeConnection conn = new SeConnection(urlParser.getServer(),
        urlParser.getInstance(), urlParser.getDatabase(), user, password);
    return conn;
  }

  public static Geometry toGeometry(SeExtent extent) {
    return GeomFunction.createBox(extent.getMinX(), extent.getMinY(),
        extent.getMaxX(), extent.getMaxY());
  }

  public static String seErrDesc(SeException ex) {
    return ex.getSeError().getErrDesc();
  }
}
