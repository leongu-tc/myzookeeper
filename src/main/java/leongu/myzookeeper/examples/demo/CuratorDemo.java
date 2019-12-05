package leongu.myzookeeper.examples.demo;

import leongu.myzookeeper.examples.util.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class CuratorDemo {
  private CuratorFramework client = initCurator();

  public CuratorFramework initCurator() {
    return CuratorFrameworkFactory.builder()
        .connectString(Constants.ZK_SERVER_URL).sessionTimeoutMs(30000)
        .connectionTimeoutMs(30000).canBeReadOnly(false)
        //    retryPolicy 连接策略：
        //    RetryOneTime: 只重连一次.
        //    RetryNTime: 指定重连的次数N.
        //    RetryUtilElapsed: 指定最大重连超时时间和重连时间间隔,间歇性重连直到超时或者链接成功.
        //    ExponentialBackoffRetry: 基于"backoff"方式重连,和RetryUtilElapsed的区别是重连的时间间隔是动态
        //    BoundedExponentialBackoffRetry: 同ExponentialBackoffRetry,增加了最大重试次数的控制.
        .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
        .build();
  }

  public static void main(String[] args) throws Exception {
    CuratorDemo demo = new CuratorDemo();
    // 1 START, SOME operation need start
    demo.client.start();
    // 2 DELETE 保障机制 guaranteed
    demo.client.delete().guaranteed().deletingChildrenIfNeeded()
        .forPath("/leongu/demo");
    demo.client.create().creatingParentsIfNeeded()
        .withMode(CreateMode.PERSISTENT).withACL(Constants.adminZKAcls())
        .forPath("/leongu/demo/p1", "hello, p1".getBytes());

    demo.client.create().creatingParentsIfNeeded()
        .withMode(CreateMode.EPHEMERAL).withACL(Constants.adminZKAcls())
        .forPath("/leongu/demo/e1", "hello, e1".getBytes());

    // 3.查看子节点列表
    List<String> clist = demo.client.getChildren().forPath("/leongu/demo");
    System.out.println("子节点： " + clist.toString());

    // 4.获取节点的数据内容 返回字节数组； 获取节点状态信息：storingStatIN
    Stat stat = new Stat();
    byte[] ret =
        demo.client.getData().storingStatIn(stat).forPath("/leongu/demo/e1");
    System.out.println("数据: " + new String(ret));
    System.out.println("节点状态信息: " + stat);

    // 5.修改数据
    demo.client.setData().withVersion(stat.getVersion())
        .forPath("/leongu/demo/e1", "hello,e1,again".getBytes());
    byte[] ret1 =
        demo.client.getData().storingStatIn(stat).forPath("/leongu/demo/e1");
    System.out.println("修改后的数据: " + new String(ret1));

    // TODO 6.查看是否存在 Stat s = client.checkExists().forPath("") 存在则返回stat对象，否则返回空
    // 异步调用
    //    demo.client.checkExists().inBackground(new BackgroundCallback() {
    //
    //      public void processResult(CuratorFramework arg0, CuratorEvent arg1)
    //          throws Exception {
    //        // TODO Auto-generated method stub
    //        CuratorEventType t = arg1.getType();
    //        System.out.println("事件类型:" + t);
    //        System.out.println("返回码: " + arg1.getResultCode());// 返回码 成功返回码为0
    //        System.out.println("触发事件的节点路径: " + arg1.getPath());
    //        System.out.println("子节点: " + arg1.getChildren());
    //        System.out.println("数据内容: " + arg1.getData());
    //        System.out.println("节点状态信息1: " + arg1.getStat());
    //        System.out
    //            .println("上下文: " + arg1.getContext()); // 上下文 ，执行异步调用时传入额外参数供我们使用
    //      }
    //    }, "123", es).forPath("/node_1");
    //7. 节点监听器 只能监听节点的新增，及修改，不能对节点的删除进行监听处理。
//    final NodeCache cache = new NodeCache(client, "/node_1");
    //    cache.start();
    //    cache.getListenable().addListener(new NodeCacheListener() {
    //
    //      public void nodeChanged() throws Exception {
    //        // TODO Auto-generated method stub
    //        byte[] ret = cache.getCurrentData().getData(); //拿到当前结点的最新数据
    //        System.out.println("新数据： " + new String(ret));
    //      }
    //    });
    //
    //    // 8。子节点监听器 子节点的添加，修改，删除
    //    final PathChildrenCache cache2 =
    //        new PathChildrenCache(client, "/node_1", true);
    //    cache2.start();
    //    cache2.getListenable().addListener(new PathChildrenCacheListener() {
    //
    //      public void childEvent(CuratorFramework client,
    //          PathChildrenCacheEvent event) throws Exception {
    //        // TODO Auto-generated method stub
    //        switch (event.getType()) {
    //        case CHILD_ADDED:
    //          System.out.println("CHILD_ADDED:" + event.getData());
    //          break;
    //        case CHILD_UPDATED:
    //          System.out.println("CHILD_UPDATED:" + event.getData());
    //          break;
    //        case CHILD_REMOVED:
    //          System.out.println("CHILD_REMOVED:" + event.getData());
    //          break;
    //        default:
    //          break;
    //        }
    //
    //      }
    //    });
    //
    //    Thread.sleep(Integer.MAX_VALUE);

  }
}
