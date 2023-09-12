public class Test {
    public static void main(String args[])
    {
        new Thread(()->{
            Client c = new Client();
        }).start();
        
        new Thread(()->{
            ContentServer cs = new ContentServer("127.0.0.1", 3000, 1);
        }).start();
    }
}

