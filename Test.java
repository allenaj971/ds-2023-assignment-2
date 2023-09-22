public class Test {
    public static void main(String args[]) {

        // for (int i = 0; i < 3; i++) {

        Client c = new Client();
        c.start();
        // }

        ContentServer cs = new ContentServer();
        cs.start();
    }
}
