/**
 * Created by sujiaxu on 16/7/11.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 创建服务器
 */


/*
*
* hash map (name , info )

info( color ,inplrivate chat or not, who talking to privately  , get hooked or not )



inplrivate chat or not   是否在private里

who talking to privately： 他是主动方，他在跟谁聊天， array list
get hoooked or not : 此人被勾住了吗，
* */




public class Server {

    //the color stuff:
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static int colorvalue = 30;
    static FileWriter writer1;
    static BufferedWriter bufw;


    public HashMap<String, Info> map = new HashMap<String, Info>();
    private List<MyChannel> all = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        System.out.println("1------");

        writer1 = new FileWriter("out.txt");

        bufw = new BufferedWriter(writer1);


        new Server().start();

    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(9999);
        while (true) {

            Socket client = server.accept();

            MyChannel channel = new MyChannel(client);//channel is a thread with  a certain socket

            all.add(channel);//统一管理add that certain socket in the list

            new Thread(channel).start();//一条道路start that thread with the certain socket
        }
    }


    public class Info {
        String color;
        boolean in_pr_ornot;
        List<MyChannel> whoIHook = new ArrayList<>();
        boolean GotHooked;


    }

    /**
     * 内部类
     * 一个客户端 一条道路
     * 1、输入流
     * 2、输出流
     * 3、接收数据
     * 4、发送数据
     */
    class MyChannel implements Runnable {
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean isRunning = true;
        private String name;

        public MyChannel() {
        }

        //初始化
        public MyChannel(Socket client) {
            System.out.println("3------");
            try {
                dis = new DataInputStream(client.getInputStream());

                dos = new DataOutputStream(client.getOutputStream());

                String rawname = dis.readUTF();

                String rawname1 = rawname.substring(rawname.indexOf(" ") + 1);
                boolean goodname = false;
                System.out.println("他在执行");

                while (goodname == false) {
                    if (map.containsKey(rawname1)) {
                        this.send("you gotta change your name ,you can not use other people 's name");
                        rawname = dis.readUTF();
                        rawname1 = rawname.substring(rawname.indexOf(" ") + 1);


                    } else {
                        goodname = true;
                    }
                }
                this.name = rawname1;
                sendOthers(rawname, false);

                this.send("welcome to the Mutithread Chatroom!!!!!!");


                File f = new File("/Users/sujiaxu/Desktop/mutithread/out.txt");
                if (f.exists() && !f.isDirectory()) {
                    System.out.println("第一个存在");

                    try (BufferedReader br = new BufferedReader(new FileReader("/Users/sujiaxu/Desktop/mutithread/out.txt"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            this.send("\u001B" + line + ANSI_RESET);

                        }
                    } catch (EOFException e) {

                    }
                } else {

                    System.out.println("第二个存在");


                }


                // sendOthers("@"+"name "+this.name , true);
            } catch (IOException e) {
                CloseUtil.closeAll(dos, dis);
                isRunning = false;

            }
        }

        //接收数据
        private String receive() {
            System.out.println("4------");
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
                CloseUtil.closeAll(dis);
                isRunning = false;
                all.remove(this);//移除自身
            }
            return msg;

        }

        //发送数据
        private void send(String msg) {
            System.out.println("5------");
            if (null == msg || msg.equals("")) {
                return;
            }
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                CloseUtil.closeAll(dos);
                isRunning = false;
                all.remove(this);//移除自身
            }
        }

        //发送给其他客户端

        public void traverse() {

            for (String everyone : map.keySet()) {

               /* if (map.get(everyone).GotHooked == true) {
                    this.send(everyone + " (is active but he got hooked)");
                } else {
                    this.send(everyone + " (is active and you can private him)");
                }*/

                System.out.println("they are :!!!!" + everyone);
                System.out.println(map.get(everyone).GotHooked + "hahahahaha");
            }

        }

        private void sendOthers(String msg, boolean sys) {

            /*
        String color;
        boolean in_pr_ornot;
        List<MyChannel> whoIHook = new ArrayList<>();
        boolean GotHooked;*/
            System.out.println("msg" + "(" + msg + ")");

            if (msg.startsWith("@")) {
                String prefix = msg.substring(1, 3);
                switch (prefix) {
                    case "pr"://private
                        String target = msg.substring(msg.indexOf(" ") + 1);


                        // traverse();

                        if (target.equalsIgnoreCase(this.name)) {
                            this.send("you can not talk to yourself!!!");
                            break;
                        }
                        System.out.println("target is!!!! :(" + target + ")");

                        map.get(this.name).in_pr_ornot = true;
                        if (!map.get(target).GotHooked) {
                            map.get(target).GotHooked = true;

                            //who I hook added
                            for (MyChannel other : all) {
                                if (other.name.equalsIgnoreCase(target)) {
                                    map.get(this.name).whoIHook.add(other);
                                    String thiscolor = map.get(this.name).color;
                                    other.send(thiscolor + this.name + " wanna talk to you, you @private hisname to chat with him" + ANSI_RESET);


                                }
                            }
                        } else if (map.get(target).GotHooked == true) {
                            this.send("you can not talk with him, he got hooked by others ,retry in 3 seconds");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < 5; i++) {
                                if (map.get(target).GotHooked == false) {
                                    this.send("he is available for you now");
                                    break;
                                } else {
                                    this.send("(extra credits!!!!!!)you can not talk with him, he got hooked by others ,retry in 3 seconds(extra credits!!!!!!)");
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }


                        break;
                    case "en"://end
                        String target1 = msg.substring(msg.indexOf(" ") + 1);

                        for (MyChannel other : all) {
                            if (other.name.equalsIgnoreCase(target1)) {

                                if (map.get(this.name).whoIHook.contains(other) == false) {
                                    this.send("you are not even having a private chat with him");
                                } else {
                                    map.get(this.name).whoIHook.remove(other);
                                    map.get(target1).GotHooked = false;
                                    if (map.get(this.name).whoIHook.size() == 0) {
                                        map.get(this.name).in_pr_ornot = false;
                                    }

                                }
                                break;

                            }
                        }


                        break;
                    case "wh"://who
                        for (String everyone : map.keySet()) {
                            if (everyone.equalsIgnoreCase(this.name) == false) {
                                if (map.get(everyone).GotHooked == true) {
                                    this.send(everyone + " (is active but he got hooked)");
                                } else {
                                    this.send(everyone + " (is active and you can private him)");
                                }
                            }
                        }
                        break;
                    case "ex"://exit
                        // String target2 = msg.substring(msg.indexOf(" ") + 1);

                        for (MyChannel myniggas : map.get(this.name).whoIHook){
                            for (String everybody :map.keySet()){
                                if (everybody.equalsIgnoreCase(myniggas.name)){
                                    map.get(everybody).GotHooked=false;
                                }
                            }
                        }


                        this.send("you got disconnected !!!!!!");
                        all.remove(this);
                        map.remove(this.name);
                        isRunning = false;


                        break;
                    case "na"://name
                        System.out.println("他在运行");
                        //String rawname= msg.substring(msg.indexOf(" ")+1);
                        //System.out.println("rawname is:"+"("+rawname+")!!!");
                        //int i = 0;
                        /*
                        for (MyChannel people :all){
                            System.out.println("people is:"+"("+people.name+")!!!");
                            if (people.name.equalsIgnoreCase(rawname)){
                                this.send("you want another name cz that one was taken!!");
                                i =1;
                            }
                        }

                        if (map.containsKey(rawname)){
                            this.send("you want another name cz that one was taken!!");
                            i ++;
                            try {
                                new Server().start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        */


                        Info info = new Info();
                        //set the colors
                        String num_to_str = Integer.toString(colorvalue);

                        info.color = "\u001B[" + num_to_str + "m";

                        colorvalue++;

                        if (colorvalue >= 37) {
                            colorvalue = 30;
                        }

                        info.GotHooked = false;

                        info.in_pr_ornot = false;


                        map.put(this.name, info);

                        //traverse();
                        System.out.println(this.name + " already put");
                        System.out.println(map.get(this.name).GotHooked + "!!!!!!!!!!!!!");
                        break;
                    default:

                }
            } else if (msg.startsWith("@") == false && map.get(this.name).whoIHook.size() != 0) {
                //在已经建立了联系之后,不需要@也可以直接与对方说话

                for (MyChannel myniggas : map.get(this.name).whoIHook) {
                    myniggas.send(map.get(this.name).color + this.name + " says: " + msg + ANSI_RESET);
                }


            } else {//just public  chat , remember to put that on a file
                for (MyChannel other : all) {
                    if (other == this) {
                        continue;
                    }

                    try {
                        bufw.write(map.get(this.name).color + this.name + " says to public :" + msg);

                        bufw.newLine();
                        bufw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    other.send(map.get(this.name).color + this.name + " says to public :" + msg + ANSI_RESET);
                }
            }


        }

        @Override
        public void run() {
            System.out.println("7------");
            while (isRunning) {
                System.out.println("9------");
                sendOthers(receive(), false);
            }
        }
    }

}