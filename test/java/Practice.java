
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<String> cartesianProduct(List<List<String>> wordLists) {
        List<String> cp = wordLists.get(0);
        for (int i = 1; i < wordLists.size(); i++) {
            List<String> secondList = wordLists.get(i);

            cp = cp.stream().flatMap(
                            s1 -> secondList.stream().map(s2 -> s1 + "|" + s2))
                    .collect(Collectors.toList());
        }
        return cp;
    }



}
