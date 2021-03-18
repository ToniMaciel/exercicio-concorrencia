import java.util.ArrayList;
import java.util.Scanner;

public class MusicPlayer {

    static Scanner in = new Scanner(System.in);
    static ArrayList<Song> songsList = new ArrayList<>();

    /*
    TODO
    -Lidar com as exceções da remoção
    -Comentar o código
    -Deixar todos os prints iguais tanto em um sistema como no outro
    -Incrementar a impressão de comandos
    */

    static class Song{
        String title, singer;
        int duration;
    }

    static class AddSongThread extends Thread {
        @Override public void run(){
            Song newSong = new Song();
            System.out.println("Digite o nome da música:");
            newSong.title = in.nextLine();
            System.out.println("Digite o nome do artista:");
            newSong.singer = in.nextLine();
            System.out.println("Digite a duração em segundos:");
            newSong.duration = in.nextInt(); in.nextLine();

            songsList.add(newSong);

            System.out.println("Você adicionou a música: " + newSong.title);
        }
    }

    static class ListSongThread extends Thread {
        @Override public void run(){
            System.out.println("Idx. \tMúsica \tArtista \tDuração");
            for (int i = 0; i < songsList.size(); ++i){
                Song s = songsList.get(i);
                System.out.printf("%d.\t %s\t %s\t %d\n", i + 1, s.title, s.singer, s.duration);
            }
        }
    }

    static class RemoveSongThread extends Thread {
        @Override public void run(){
            System.out.println("Índice da música a ser deletada:");
            int idx = in.nextInt(); in.nextLine();
            songsList.remove(idx - 1);
        }
    }

    static Thread UserInterfaceThread = new Thread() {
        @Override public void run(){
            System.out.println("Bem-Vindo");
            String input;

            while (true){
                input = in.nextLine();
                
                if (input.equals("add")){
                    AddSongThread addThread = new AddSongThread();
                    addThread.start();
                    try {
                        addThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if (input.equals("lst")){
                    ListSongThread listThread = new ListSongThread();
                    listThread.start();
                    try {
                        listThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if (input.equals("rmv")){
                    RemoveSongThread removeThread = new RemoveSongThread();
                    removeThread.start();
                    try {
                        removeThread.join();
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
