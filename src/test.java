import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sujiaxu on 16/7/16.
 */






class info {
    boolean talk;
    String colour;
    ArrayList<String> array;

    public info(boolean talk, String colour, ArrayList<String> array) {
        this.talk = talk;
        this.colour = colour;
        this.array = array;

    }

    public info() {

    }

}

public class test {

    public static int num = 30;


    public static void main(String[] args) throws IOException {


        HashMap<Integer, info> map = new HashMap<>();


        for (int i = 0; i < 3; i++) {
            info Info1 = new info();
            Info1.talk = true;
            Info1.colour = "blue";
            ArrayList<String> myList = new ArrayList<String>();
            myList.add("fuk");
            Info1.array = myList;
            map.put(i, Info1);

        }
        String color = map.get(1).colour;
        color = map.get(1).colour = "red";




        System.out.println(map.get(2).colour);

        String aa = Integer.toString(555555);
        System.out.println(aa);
        String j = "ox123"+aa+"heheh";
        System.out.println(j);

        num++;
        System.out.println(num);

/*
        map.get(1).colour = "red";




        String test1 = "@private josh";
        String test2 = "privat";
        String test3 = test1.substring(1,3);

        System.out.println(test3.equals("pre"));
*/
    }
}
