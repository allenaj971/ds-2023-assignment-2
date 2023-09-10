public class Test {
    public static void main(String args[])
    {
        for (int i = 0; i < 2; i++) {
            new Thread(()->{
                // initialise the client 
            Client c = new Client("127.0.0.1", 3000, Thread.currentThread().getName());
            }).start();
            
            if(i % 2 == 0)
            {
                new Thread(()->{
                ContentServer cs = new ContentServer("127.0.0.1", 3000, 1);
                }).start();
            }
            else
            {
                new Thread(()->{
                ContentServer cs = new ContentServer("127.0.0.1", 3000, 2);
                }).start();
            }
        }
    }
}
