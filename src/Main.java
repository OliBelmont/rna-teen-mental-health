import preprocessing.DataLoader;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String args[]){

        DataLoader loader = new DataLoader();

        List<String[]> load = loader.loadFile();

        System.out.println(Arrays.toString(load.get(0)));


    }
}
