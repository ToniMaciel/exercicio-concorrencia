public class MusicPlayer {

    public static void main(String[] args) throws Exception {
        Thread threadInterface = new UserInterfaceThread();
        threadInterface.start();
        threadInterface.join();
    }
}
