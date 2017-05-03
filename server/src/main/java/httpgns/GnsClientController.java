package httpgns;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.*;



import edu.umass.cs.gnsclient.client.GNSClient;
import edu.umass.cs.gnsclient.client.GNSCommand;
import edu.umass.cs.gnsclient.client.util.GuidEntry;
import edu.umass.cs.gnsclient.client.util.GuidUtils;

@RestController
public class GnsClientController {

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

  private static GNSClient gnsClient = null;
  private static final String ADMIN_ALIAS = "http_server";
  private static final String ADMIN_PASSWORD = "password";
  private static GuidEntry adminGuid = null;
  private static final String IP_STRING = "ip";

  @RequestMapping("/getipstring")
  public IpString ipstring(@RequestParam(value="name", defaultValue="World") String name) {
    System.out.println(name);

    String final_ip_str = "";
    String targetGuidString = "";
    if(gnsClient == null || adminGuid == null){
      try {
        gnsClient = new GNSClient();
        adminGuid = GuidUtils.lookupOrCreateAccountGuid(gnsClient, ADMIN_ALIAS, ADMIN_PASSWORD);
      }catch(Exception e) {
        System.out.println("Exception occured while doing lookup for admin account");
      }
    }

    System.out.println("Admin Guid " + adminGuid.getGuid());
    try{
      targetGuidString = gnsClient.execute(GNSCommand.lookupGUID(name)).getResultString();
    } catch(Exception e){
      System.out.println("Exception occured while doing lookup for target guid");
    }

    System.out.println("Target Guid String " + targetGuidString);
    try{
      String final_ip_json = gnsClient.execute(GNSCommand.fieldRead(targetGuidString, IP_STRING, adminGuid)).getResultString();
      JSONObject obj = new JSONObject(final_ip_json);
      final_ip_str = obj.getString(IP_STRING);
      System.out.println("Final_ip_string "+ final_ip_str);
    } catch(Exception e) {
      System.out.println("Exception occured while reading value " + e.getMessage());
    }

    return new IpString(final_ip_str);
  }
}
