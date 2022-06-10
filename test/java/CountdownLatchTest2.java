import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountdownLatchTest2 {
    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        final CountDownLatch referee = new CountDownLatch(1);
        final CountDownLatch player = new CountDownLatch(4);
        
        for (int i = 0; i < 4; i++) {
            Runnable runnable = () -> {
                try {
                    // 1、4位选手准备
                    System.out.println("选手" + Thread.currentThread().getName() + "正在等待裁判发布口令");
                    referee.await();
                    // 4、选手听到了裁判的黑哨，开始比赛
                    System.out.println("选手" + Thread.currentThread().getName() + "已接受裁判口令");
                    Thread.sleep((long) (Math.random() * 10000));
                    System.out.println("选手" + Thread.currentThread().getName() + "到达终点");
                    // 冲线  冲线  lbw 牛逼
                    player.countDown();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            // game start
            service.execute(runnable);
        }
        
        try {
            Thread.sleep((long) (Math.random() * 10000));
            // 2、裁判准备吹黑哨啦 注意了 注意了
            System.out.println("裁判"+Thread.currentThread().getName()+"即将发布口令");
            referee.countDown();
            // 3、裁判宣布开始
            System.out.println("裁判"+Thread.currentThread().getName()+"已发送口令，正在等待所有选手到达终点");
            // 5、所有选手到达重点，裁判宣布比赛结束
            player.await();
            System.out.println("所有选手都到达终点");
            System.out.println("裁判"+Thread.currentThread().getName()+"汇总成绩排名");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // game over
        service.shutdown();
    }
}