import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author: zhouy
 * @date: 2022/05/30
 **/
public class ThreadTest implements Runnable {


    @Override
    public void run() {

    }


    @Test
    public void test() throws ExecutionException, InterruptedException {


        final ConcurrentHashMap<Object, Future<String>> taskCache = new ConcurrentHashMap<>();

        final ExecutorService executorService = Executors.newFixedThreadPool(10);

        System.out.println("----------------------------------");
        Callable01 callable01 = new Callable01();
        final Future<Object> submit = executorService.submit(callable01);
        final Object o = submit.get();
        System.out.println(o);



        new FutureTask<>(callable01);


    }


    public static class Callable01 implements Callable<Object> {

        @Override
        public Object call()  {
            System.out.println("start-" + Thread.currentThread()
                    .getId());
            int i = 10 / 2;
            System.out.println("result=" + i);
            return i;
        }
    }
}
