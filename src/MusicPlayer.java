import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MusicPlayer {

    static Lock lock = new ReentrantLock();
    static Scanner in = new Scanner(System.in);
    static ArrayList<Song> songsList = new ArrayList<>(); //Um array que armazenará todas as músicas até então adicionadas.
    
    //A classe das músicas. Considera que cada música terá um nome, um cantor e uma duração em segundos.
    static class Song{
        String title, singer;
        int duration;
    }

    /*
    * Essa é a classe usada para adicionar uma música à lista de reprodução. 
    * Ao executar essa thread, ela pedirá o nome da música, o nome do artista e a sua duração
    * com essas informações será criado um novo objeto song que será adicionado ao array de músicas que representa a lista de reprodução.
    */
    static class AddSongThread extends Thread {
        Song newSong;

        public AddSongThread(String title, String singer, int duration) {
            this.newSong = new Song();

            this.newSong.title = title;
            this.newSong.singer = singer;
            this.newSong.duration = duration;
        }

        @Override public void run(){          
            songsList.add(this.newSong);

            System.out.println("Você adicionou a música: " + newSong.title);
        }
    }

    /*
    * Essa é a classe usada para imprimir a lista de reprodução. 
    * Ao executar essa thread, ela percorrerá a lista de reprodução e imprimirá as características de cada uma das músicas contida nela.
    */
    static class ListSongThread extends Thread {
        @Override public void run(){
            System.out.println("Idx.\tMúsica\tArtista\tDuração");
            for (int i = 0; i < songsList.size(); ++i){
                Song s = songsList.get(i);
                System.out.printf("%d.\t%s\t%s\t%d\n", i + 1, s.title.replaceAll(" ", ""), s.singer.replaceAll(" ", ""), s.duration);
            }
        }
    }

    /*
    * Essa é a classe usada para remover músicas da lista de reprodução. 
    * Ao executar essa thread, ela checará se existem músicas na lista de reprodução
    * Caso existam, será pedido a posição da música que deseja-se remover e em seguida
    * as músicas contidas nessa lista são impressas sendo mostrado seu nome, seu artista e sua posição na lista
    * Ao pegar o índice dado pelo usuário é observado se é um valor válido, caso não seja é printado uma mensagem dizendo que não foi possível executar o comando
    * Caso seja, a música é removida e uma mensagem de sucesso é mostrada.
    */
    static class RemoveSongThread extends Thread {
        int idx;

        public RemoveSongThread(int removeIndx){
            this.idx = removeIndx;
        }

        @Override public void run(){
            Song aux = songsList.remove(idx);
            System.out.println("A música " + aux.title + " foi removida com sucesso!");
            
        }
    }

    /*
    * Essa é a thread usada para lidar com os comandos do usuário. 
    * Assim que essa thread tem inicio, mostrará uma mensagem de boas vindas e os comandos que possui
    * O comando 'add' inicia uma nova thread para adicionar uma música à lista
    * O comando 'lst' inicia uma nova thread para listar as músicas da lista
    * O comando 'rmv' inicia uma nova thread para remover uma música da lista
    * O comando 'help' serve para imprimir os comandos novamente
    * O comando 'ext' serve finalizar o sistema
    * Quaisquer outros comandos farão o programa emitir um aviso de comando inválido e buscarão um novo comando.
    * As ordens de 'join' em cada um dos comandos servem para que a thread de interface só inicie outro comando quando finalizar o que estava executando 
    */
    static Thread UserInterfaceThread = new Thread() {
        @Override public void run(){
            System.out.println("Bem-Vindo");
            printCommands(); //Serve para printar os comandos que o sistema atende
            String input;

            while (true){ //Esse laço se repetirá até que o usuário indique que deseja sair do programa
                input = in.nextLine(); //Pega cada novo comando
                
                if (input.equals("add")){
                    System.out.println("Digite o nome da música:");
                    String title = in.nextLine();
                    System.out.println("Digite o nome do artista:");
                    String singer = in.nextLine();
                    System.out.println("Digite a duração em segundos:");
                    String duration = in.nextLine();

                    AddSongThread addThread = new AddSongThread(title, singer, Integer.parseInt(duration));
                    addThread.start();
                } else if (input.equals("lst")){
                    ListSongThread listThread = new ListSongThread();
                    listThread.start();
                } else if (input.equals("rmv")){
                    if(songsList.isEmpty()){
                        System.out.println("Parece que você não tem músicas para remover :/");
                    } else {
                        System.out.println("Digite o índice da música que deseja remover:");
        
                        int i = 1; //Posição de cada música na lista de reprodução
                        for (Song song : songsList) {
                            System.out.println(i + ". " + song.title + " do artista " + song.singer);
                            i++;
                        }
                        
                        int idx = in.nextInt(); in.nextLine();
            
                        if(idx < 1 || idx > songsList.size())
                            System.out.println("Esse índice não existe :( Por favor use o comando 'rmv' novamente caso deseje excluir uma música");
                        else{
                            RemoveSongThread removeThread = new RemoveSongThread(idx-1);
                            removeThread.start();
                        }
                    }
                } else if (input.equals("help")){
                    printCommands();
                } else if (input.equals("ext")){
                    System.exit(0);
                } else {
                    System.out.println("Comando inválido");
                }
            }
        }

        //Imprime uma lista com cada um dos comandos e sua função
        private void printCommands() {
            System.out.println("Digite o comando que você deseja:\n" + "add - Adicionar uma música\n" 
            + "rmv - Remover uma música\n" + "lst - Listar as músicas que estão na lista\n" + 
            "help - Mostrar comandos novamente\n" + "ext - Parar a execução");
        }
    };

    //Roda a thread de interface com o usuário para inicializar o programa e só prossegue após sua finalização
    public static void main(final String[] args) throws InterruptedException {
        UserInterfaceThread.start();
        UserInterfaceThread.join();
    }
}
