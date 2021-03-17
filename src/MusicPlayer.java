import java.util.ArrayList;
import java.util.Scanner;

public class MusicPlayer {

    static class Song{
        String title, singer;
        int duration;
    }

    static Scanner in = new Scanner(System.in);
    static ArrayList<Song> songsList = new ArrayList<>();

    static Thread AddSongThread = new Thread() {
        public void run(){
            Song newSong = new Song();
            System.out.println("Digite o nome da musica");
            newSong.title = in.nextLine();
            System.out.println("Digite o artista");
            newSong.singer = in.nextLine();
            System.out.println("Digite a duracao em segundos");
            newSong.duration = in.nextInt();
            in.nextLine();

            songsList.add(newSong);

            System.out.println("Voce adicionou a musico" + newSong.title);
        }
    };

    static Thread ListSongThread = new Thread() {
        public void run(){
            System.out.println("Idx.\tTitulo\tCantor\tDuracao");
            for (int i = 0; i < songsList.size(); ++i){
                Song s = songsList.get(i);
                System.out.printf("%d.\t%s\t%s\t%d\n", i + 1, s.title, s.singer, s.duration);
            }
        }
    };

    static Thread RemoveSongThread = new Thread() {
        public void run(){
            System.out.println("indice");
            int idx = in.nextInt();
            in.nextLine();
            songsList.remove(idx);
        }
    };

    static Thread UserInterfaceThread = new Thread() {
        public void run(){
            System.out.println("test");
            String input = new String();

            while (true){
                input = in.nextLine();
                
                if (input.equals("add")){
                    AddSongThread.start();
                    try {
                        AddSongThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if (input.equals("lst")){
                    ListSongThread.start();
                    try {
                        ListSongThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if (input.equals("rmv")){
                    RemoveSongThread.start();
                    try {
                        RemoveSongThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
    
                } else if (input.equals("ext")){
                    System.exit(0);
                } else {
                    System.out.println("Comando invalido");
                }
            }
        }
    };

    public static void main(final String[] args) throws InterruptedException {
        UserInterfaceThread.start();
        UserInterfaceThread.join();
    }
}
