import java.util.ArrayList;
import java.util.Scanner;

public class MusicPlayer {

    static Scanner in = new Scanner(System.in);
    static ArrayList<Song> songsList = new ArrayList<>();

    /*
    TODO
    -Comentar o código
    -Deixar todos os prints iguais tanto em um sistema como no outro
    -Incrementar a impressão de comandos
    -No outro sistema, usar mesmo esquema do remove daqui 
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
            if(songsList.isEmpty()){
                System.out.println("Parece que você não tem músicas para remover :/");
            } else {
                System.out.println("Digite o índice da música que deseja remover:");

                int i = 1;
                for (Song song : songsList) {
                    System.out.println(i + ". " + song.title + " do artista " + song.singer);
                    i++;
                }
    
                int idx = in.nextInt() - 1; in.nextLine();
    
                if(idx < 0 || idx > songsList.size() - 1)
                    System.out.println("Esse índice não existe :( Por favor use o comando 'rmv' novamente caso deseje excluir uma música");
                else{
                    Song aux = songsList.remove(idx);
                    System.out.println("A música " + aux.title + " foi removida com sucesso!");
                }
            }
        }
    }

    static Thread UserInterfaceThread = new Thread() {
        @Override public void run(){
            System.out.println("Bem-Vindo");
            printCommands();
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

                } else if (input.equals("help")){
                    printCommands();
                } else if (input.equals("ext")){
                    System.exit(0);
                } else {
                    System.out.println("Comando invalido");
                }
            }
        }

        private void printCommands() {
            System.out.println("Digite o comando que você deseja:\n" + "add - Adicionar uma música\n" 
            + "rmv - Remover uma música\n" + "lst - Listar as músicas que estão na lista\n" + 
            "help - Mostrar comandos novamente\n" + "ext - Parar a execução");
        }
    };

    public static void main(final String[] args) throws InterruptedException {
        UserInterfaceThread.start();
        UserInterfaceThread.join();
    }
}
