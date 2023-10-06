public class LamportTesting {
    public static void main(String[] args) {
        // Lamport clocks are implemented, you can see the requests and their lamport
        // timestamps in the aggregation server
        // terminal and the output when you run 'make lamporttesting' in another
        // terminal, and you will see that the responses
        // are sent back in lamport timestamp order

        // I am running a content server and client and interleaving
        // PUT and GET requests so that the responses are processed
        // in a particular order to obey the lamport algorithm
        ContentServer cs = new ContentServer();
        cs.start();

        try {
            cs.join();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        Client c = new Client();
        c.start();

        try {
            c.join();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

    }
}
