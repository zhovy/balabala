
import org.junit.Test;

import java.io.File;

/**
 * @date: 2022/05/23
 **/
public class Practice {


    @Test
    public void dirIsLive(){

        final File file = new File("E:/tmp/");

        if (!file.exists()){
            final boolean mkdirs = file.mkdirs();
            System.out.println(
                    mkdirs
            );
        }
    }

}
