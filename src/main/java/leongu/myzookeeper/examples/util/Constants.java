package leongu.myzookeeper.examples.util;

import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Constants {
  // zookeeper config
  public static final String ZK_SERVER_URL = "zookeeper0:2181";

  // other
  public static SimpleDateFormat format =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private Constants() {
  }

  public static long currTime() {
    return System.currentTimeMillis();
  }

  public static String currTimeStr() {
    return format.format(new Date());
  }

  public static List<ACL> adminZKAcls() {
    return Arrays.asList(new ACL(ZooDefs.Perms.ALL,
        ZooDefs.Ids.ANYONE_ID_UNSAFE)); // world, anyone
  }

  public static List<ACL> masterZKAcls() {
    ACL first = ZooDefs.Ids.CREATOR_ALL_ACL.get(0);
    return Arrays.asList(first,
        new ACL(ZooDefs.Perms.READ ^ ZooDefs.Perms.CREATE,
            ZooDefs.Ids.ANYONE_ID_UNSAFE));
  }
}
