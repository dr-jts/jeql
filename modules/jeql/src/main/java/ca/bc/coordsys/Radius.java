package ca.bc.coordsys;


/**
 * Utility class, holds constants associated with Radius 
 * for various projections.
 *
 * @author $Author: dkim $
 * @version  $Revision: 1.2 $
 *
 * <pre>
 *
 *  $Id: Radius.java,v 1.2 2003/11/05 05:24:47 dkim Exp $
 *  $Date: 2003/11/05 05:24:47 $
 *  $Log: Radius.java,v $
 *  Revision 1.2  2003/11/05 05:24:47  dkim
 *  Added global header; cleaned up Javadoc.
 *
 *  Revision 1.1  2003/09/15 20:26:11  jaquino
 *  Reprojection
 *
 *  Revision 1.2  2003/07/25 17:01:03  gkostadinov
 *  Moved classses reponsible for performing the basic projection to a new
 *  package -- base.
 *
 *  Revision 1.1  2003/07/24 23:14:43  gkostadinov
 *  adding base projection classes
 *
 *  Revision 1.1  2003/06/20 18:34:31  gkostadinov
 *  Entering the source code into the CVS.
 *
 * </pre>
 */

public class Radius {

  public double a, b, rf;
  public final static int WGS72 = 1;
  public final static int CLARKE = 2;
  public final static int GRS80 = 0;

  public Radius(int type) {
    switch (type) {
      case Radius.GRS80:
        a = 6378137.0;
        b = -1.0;
        rf = 298.257222101;
        break;
      case Radius.WGS72:
        a = 6378135.0;
        b = 6356750.5;
        rf = -1.0;
        break;
      case Radius.CLARKE:
        a = 6378206.4;
        b = 6356583.8;
        rf = -1.0;
        break;
    }
  }
}

