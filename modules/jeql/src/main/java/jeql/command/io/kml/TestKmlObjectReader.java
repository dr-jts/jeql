package jeql.command.io.kml;

import java.io.FileInputStream;
import java.io.FileReader;

public class TestKmlObjectReader {

  public static void main(String args[])
  {
    TestKmlObjectReader test = new TestKmlObjectReader();
    try {
      test.run();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public TestKmlObjectReader() {
    super();
  }

  void run()
    throws Exception
  {
    String filename = "C:\\data\\martin\\proj\\geodata\\world\\namePieSmall.kml";
    KMLObjectReader rdr = new KMLObjectReader();
    rdr.open(new FileReader(filename));
    
    while (true) {
      Placemark pm = rdr.next();
      if (pm == null)
        break;
      System.out.println(pm);
    }
  }
  
}
