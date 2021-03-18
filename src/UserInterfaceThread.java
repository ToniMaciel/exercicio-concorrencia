import java.util.ArrayList;
import java.util.Scanner;

public class UserInterfaceThread extends Thread{

    Scanner in = new Scanner(System.in);
    ArrayList<Song> songs = new ArrayList<>();
    ArrayList<Thread> songsExe = new ArrayList<>();
    
    @Override
    public void run() {
        System.out.println("Bem-vindo");
        printCommands();
        String input;
        
        while(true){
            input = in.nextLine();

            if (input.equals("add")){
                addSong();
            } else if (input.equals("lst")){
                listSongs();
            } else if (input.equals("rmv")){
                removeSong();
            } else if (input.equals("help")){
                printCommands();
            } else if (input.equals("ext")){
                in.close();
                System.exit(0);
            } else {
                System.out.println("Comando inválido");
            }
        }
    }

    private void listSongs() {
        System.out.println("Idx. \t Música \t Artista \t Duração");

        for (Song song : songs) {
            Thread newThread = new Song(song.name, song.artist, song.duration, song.id);
            songsExe.add(newThread);
            newThread.start();
        }
    }

    private void removeSong() {
        if(songs.isEmpty()) 
            System.out.println("Parece que você não tem músicas para remover :/");
        else {
            System.out.println("Digite o índice da música que deseja remover:");

                for (Song song : songs) {
                    System.out.println(song.id + ". " + song.name + " do artista " + song.artist);
                }
    
                int idx = in.nextInt() - 1; in.nextLine();
    
                if(idx < 0 || idx > songs.size() - 1)
                    System.out.println("Esse índice não existe :( Por favor use o comando 'rmv' novamente caso deseje excluir uma música");
                else{
                    Song aux = songs.remove(idx);
                    System.out.println("A música " + aux.name + " foi removida com sucesso!");
                }
        }
    }

    private void printCommands() {
        System.out.println("Digite o comando que você deseja:\n" + "add - Adicionar uma música\n" 
        + "rmv - Remover uma música\n" + "lst - Listar as músicas que estão na lista\n" + 
        "help - Mostrar comandos novamente\n" + "ext - Parar a execução");
    }

    private void addSong() {
        System.out.println("Digite o nome da música:");
        String name = in.nextLine();
        System.out.println("Digite o nome do artista:");
        String artist = in.nextLine();
        System.out.println("Digite a duração em segundos:");
        String duration = in.nextLine();

        Song newSong = new Song(name, artist, Integer.parseInt(duration), songs.size() + 1);
        songs.add(newSong);
        System.out.println("Você adicionou a música: " + name);
    }

}
