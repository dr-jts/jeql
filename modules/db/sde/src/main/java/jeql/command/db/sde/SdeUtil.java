package jeql.command.db.sde;

import jeql.api.error.ExecutionException;
import jeql.api.error.InvalidInputException;
import jeql.engine.ConfigurationException;
import jeql.std.geom.GeomFunction;

import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeError;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeExtent;
import com.esri.sde.sdk.client.SeFilter;
import com.esri.sde.sdk.client.SeTable;
import com.vividsolutions.jts.geom.Envelope;
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

  public static SeExtent toExtent(Geometry geom) {
    Envelope env = geom.getEnvelopeInternal();
    return new SeExtent(env.getMinX(), env.getMinY(), env.getMaxX(), env.getMaxY());
  }

  /**
   * Transforms SeExceptions to JEQL exceptions with the error message.
   * All others are left unchanged.
   * 
   * @param ex
   * @return
   */
  public static ExecutionException seError(Exception ex) {
    if (ex instanceof SeException) {
      SeException sex = (SeException) ex;
      return new ExecutionException(sex.getSeError().getErrDesc());
    }
    return new ExecutionException(ex);
  }
  
  public static int filterMethod(String methodName) {
    
    if (methodName.equalsIgnoreCase("ENVP")) {
      return SeFilter.METHOD_ENVP;
    }
    ;
    if (methodName.equalsIgnoreCase("ENVP_BY_GRID")) {
      return SeFilter.METHOD_ENVP_BY_GRID;
    }
    ;
    if (methodName.equalsIgnoreCase("CP")) {
      return SeFilter.METHOD_CP;
    }
    ;
    if (methodName.equalsIgnoreCase("LCROSS")) {
      return SeFilter.METHOD_LCROSS;
    }
    ;
    if (methodName.equalsIgnoreCase("COMMON")) {
      return SeFilter.METHOD_COMMON;
    }
    ;
    if (methodName.equalsIgnoreCase("CP_OR_LCROSS")) {
      return SeFilter.METHOD_CP_OR_LCROSS;
    }
    ;
    if (methodName.equalsIgnoreCase("LCROSS_OR_CP")) {
      return SeFilter.METHOD_LCROSS_OR_CP;
    }
    ;
    if (methodName.equalsIgnoreCase("ET_OR_AI")) {
      return SeFilter.METHOD_ET_OR_AI;
    }
    ;
    if (methodName.equalsIgnoreCase("AI_OR_ET")) {
      return SeFilter.METHOD_AI_OR_ET;
    }
    ;
    if (methodName.equalsIgnoreCase("ET_OR_II")) {
      return SeFilter.METHOD_ET_OR_II;
    }
    ;
    if (methodName.equalsIgnoreCase("II_OR_ET")) {
      return SeFilter.METHOD_II_OR_ET;
    }
    ;
    if (methodName.equalsIgnoreCase("AI")) {
      return SeFilter.METHOD_AI;
    }
    ;
    if (methodName.equalsIgnoreCase("II")) {
      return SeFilter.METHOD_II;
    }
    ;
    if (methodName.equalsIgnoreCase("AI_NO_ET")) {
      return SeFilter.METHOD_AI_NO_ET;
    }
    ;
    if (methodName.equalsIgnoreCase("II_NO_ET")) {
      return SeFilter.METHOD_II_NO_ET;
    }
    ;
    if (methodName.equalsIgnoreCase("PC")) {
      return SeFilter.METHOD_PC;
    }
    ;
    if (methodName.equalsIgnoreCase("SC")) {
      return SeFilter.METHOD_SC;
    }
    ;
    if (methodName.equalsIgnoreCase("PC_NO_ET")) {
      return SeFilter.METHOD_PC_NO_ET;
    }
    ;
    if (methodName.equalsIgnoreCase("SC_NO_ET")) {
      return SeFilter.METHOD_SC_NO_ET;
    }
    ;
    if (methodName.equalsIgnoreCase("PIP")) {
      return SeFilter.METHOD_PIP;
    }
    ;
    if (methodName.equalsIgnoreCase("IDENTICAL")) {
      return SeFilter.METHOD_IDENTICAL;
    }
    ;
    if (methodName.equalsIgnoreCase("CBM")) {
      return SeFilter.METHOD_CBM;
    }
    throw new InvalidInputException("Invalid SDE filter method: " + methodName);
  }
}
