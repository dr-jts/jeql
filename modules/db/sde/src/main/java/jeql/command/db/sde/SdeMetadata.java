package jeql.command.db.sde;

import java.util.*;
import com.esri.sde.sdk.client.*;
import com.vividsolutions.jts.geom.*;
import jeql.command.db.DbCommandBase;
import jeql.engine.Scope;
import jeql.api.table.Table;
import jeql.api.row.*;

public class SdeMetadata extends DbCommandBase {
  private int limit = -1;
  private boolean indexExtent = false;
  // private boolean dataExtent = false;
  private String layerNamePattern = null;
  private SimpleSqlPatternMatcher matcher = null;

  private Table result;

  public SdeMetadata() {

  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  /**
   * Sets whether index extent should be calculated
   * 
   * @param calcExtent
   */
  public void setIndexExtent(boolean calcExtent) {
    this.indexExtent = calcExtent;
  }

  /*
   * public void setDataExtent(boolean calcExtent) { this.dataExtent =
   * calcExtent; }
   */
  public void setLayerName(String layerNamePattern) {
    this.layerNamePattern = layerNamePattern;
    matcher = new SimpleSqlPatternMatcher(layerNamePattern);
  }

  public Table getDefault() {
    return result;
  }

  public void execute(Scope scope) throws Exception {
    SeConnection conn = SdeUtil.getConnection(url, user, password);
    List layers = conn.getLayers();

    RowSchema schema = createSchema();
    ArrayRowList rl = new ArrayRowList(schema);

    int num = layers.size();
    if (limit >= 0 && limit < num)
      num = limit;

    int COL_ID_INDEX = schema.getColIndex(COL_ID);
    int COL_NAME_INDEX = schema.getColIndex(COL_NAME);
    int COL_DESC_INDEX = schema.getColIndex(COL_DESC);
    int COL_SPATIALCOL_INDEX = schema.getColIndex(COL_SPATIALCOL);
    int COL_KEYWORD_INDEX = schema.getColIndex(COL_KEYWORD);
    int COL_COORDREF_INDEX = schema.getColIndex(COL_COORDREF);
    int COL_IS3D_INDEX = schema.getColIndex(COL_IS3D);
    int COL_ALLOW_POINT_INDEX = schema.getColIndex(COL_ALLOW_POINT);
    int COL_ALLOW_LINE_INDEX = schema.getColIndex(COL_ALLOW_LINE);
    int COL_ALLOW_SIMPLE_LINE_INDEX = schema.getColIndex(COL_ALLOW_SIMPLE_LINE);
    int COL_ALLOW_AREA_INDEX = schema.getColIndex(COL_ALLOW_AREA);
    int COL_ALLOW_MULTIPART_INDEX = schema.getColIndex(COL_ALLOW_MULTIPART);

    int COL_EXTENT_INDEX = schema.getColIndex(COL_EXTENT);
    int COL_INDEXEXTENT_INDEX = schema.getColIndex(COL_INDEXEXTENT);
    int COL_DATAEXTENT_INDEX = schema.getColIndex(COL_DATAEXTENT);

    for (int i = 0; i < num; i++) {
      SeLayer lyr = (SeLayer) layers.get(i);
      String layerName = lyr.getQualifiedName();

      // skip layer if not matched
      if (matcher != null && !matcher.matchIgnoreCase(layerName))
        continue;

      BasicRow row = new BasicRow(schema.size());

      // System.out.println(lyr.getName());
      setValue(row, COL_ID_INDEX, new Integer((int) lyr.getID().longValue()));
      setValue(row, COL_NAME_INDEX, lyr.getQualifiedName());
      setValue(row, COL_DESC_INDEX, lyr.getDescription());
      setValue(row, COL_SPATIALCOL_INDEX, lyr.getSpatialColumn());
      setValue(row, COL_KEYWORD_INDEX, lyr.getCreationKeyword());
      setValue(row, COL_COORDREF_INDEX, lyr.getCoordRef()
          .getCoordSysDescription());
      setValue(row, COL_IS3D_INDEX, new Boolean(lyr.is3D()));

      setValue(row, COL_EXTENT_INDEX, SdeUtil.toGeometry(lyr.getExtent()));
      if (indexExtent) {
        // compute actual extent of data but using index
        row.setValue(COL_INDEXEXTENT_INDEX,
            SdeUtil.toGeometry(lyr.calculateExtent(true, new SeSqlConstruct())));
      }
      int shapeTypeMask = lyr.getShapeTypes();
      setValue(row, COL_ALLOW_POINT_INDEX, new Boolean(
          (shapeTypeMask & SeLayer.SE_POINT_TYPE_MASK) != 0));
      setValue(row, COL_ALLOW_LINE_INDEX, new Boolean(
          (shapeTypeMask & SeLayer.SE_LINE_TYPE_MASK) != 0));
      setValue(row, COL_ALLOW_SIMPLE_LINE_INDEX, new Boolean(
          (shapeTypeMask & SeLayer.SE_SIMPLE_LINE_TYPE_MASK) != 0));
      setValue(row, COL_ALLOW_AREA_INDEX, new Boolean(
          (shapeTypeMask & SeLayer.SE_AREA_TYPE_MASK) != 0));
      setValue(row, COL_ALLOW_MULTIPART_INDEX, new Boolean(
          (shapeTypeMask & SeLayer.SE_MULTIPART_TYPE_MASK) != 0));
      rl.add(row);
    }
    result = new Table(rl);
  }

  private void setValue(BasicRow row, int index, Object value) {
    if (index < 0)
      return;
    row.setValue(index, value);
  }

  private static final String COL_ID = "id";
  private static final String COL_NAME = "name";
  private static final String COL_DESC = "description";
  private static final String COL_SPATIALCOL = "spatialCol";
  private static final String COL_KEYWORD = "keyword";
  private static final String COL_COORDREF = "coordRef";
  private static final String COL_IS3D = "is3D";

  private static final String COL_ALLOW_POINT = "allowsPoint";
  private static final String COL_ALLOW_LINE = "allowsLine";
  private static final String COL_ALLOW_SIMPLE_LINE = "allowsSimpleLine";
  private static final String COL_ALLOW_AREA = "allowsArea";
  private static final String COL_ALLOW_MULTIPART = "allowsMultiPart";

  private static final String COL_EXTENT = "extent";
  private static final String COL_INDEXEXTENT = "indexExtent";
  private static final String COL_DATAEXTENT = "dataExtent";

  private RowSchema createSchema() {
    int schemaSize = 13;
    RowSchema schema = new RowSchema(schemaSize);

    schema.setColumnDef(0, COL_ID, Integer.class);
    schema.setColumnDef(1, COL_NAME, String.class);
    schema.setColumnDef(2, COL_DESC, String.class);
    schema.setColumnDef(3, COL_SPATIALCOL, String.class);
    schema.setColumnDef(4, COL_KEYWORD, String.class);
    schema.setColumnDef(5, COL_IS3D, Boolean.class);

    schema.setColumnDef(6, COL_ALLOW_POINT, Boolean.class);
    schema.setColumnDef(7, COL_ALLOW_LINE, Boolean.class);
    schema.setColumnDef(8, COL_ALLOW_SIMPLE_LINE, Boolean.class);
    schema.setColumnDef(9, COL_ALLOW_AREA, Boolean.class);
    schema.setColumnDef(10, COL_ALLOW_MULTIPART, Boolean.class);

    schema.setColumnDef(11, COL_COORDREF, String.class);
    schema.setColumnDef(12, COL_EXTENT, Geometry.class);
    schema.setColumnDef(12, COL_INDEXEXTENT, Geometry.class);

    return schema;
  }

}
