public class MultiplePUT {
    public static void main(String[] args)
    {
        // To test multiple PUT requests 
        // here we run 4 new content servers 
        // to test concurrent PUT requests
        for (int i = 0; i < 4; i++) {
            new Thread(()->{
                ContentServer cs = new ContentServer();
                cs.start();
            }).start();
        }
    }
}
