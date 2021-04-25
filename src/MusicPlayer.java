import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.DefaultListModel;

public class MusicPlayer {
    
    public Lock lock = new ReentrantLock(); //Lock que será usado na região critica (lista das músicas)
    public Condition songslistOperation = lock.newCondition();
    public boolean songsListInUse = false; //Para saber se alguma thread está fazendo alguma operação com a lista de reprodução
    public DefaultListModel<String> songsList = new DefaultListModel<>(); // Lista que possui strings que definem o nome da música e o artista
    public ArrayList<String> songsListAux = new ArrayList<>();
    public ArrayList<Integer> duration = new ArrayList<>(); //	Lista que possui as durações das músicas de mesmo indice na lista anterior
    public ArrayList<Integer> durationAux = new ArrayList<>();

    /*
    * Essa é a classe usada para adicionar uma música à lista de reprodução.
    * Com os parâmetros passados no seu construtor, faremos uma nova música 
    * Ao executar essa thread, usaremos um lock para evitar uma condição de corrida
    * Enquanto o boolean 'songsListInUse' significa que alguma thread está operando na região crítica
    * e portanto, daremos um await no nosso condicional.
    * Quando a outra thread terminar, esta thread de adição pode adicionar a música, enviar uma mensagem de sucesso e notificar as outras threads que terminou
    */
    class AddSongThread extends Thread{
        String musicName;
        int musicDuration;

        public AddSongThread(String title, int duration) {
            this.musicName = title;
            this.musicDuration = duration;
        }

        @Override public void run(){
            try {
                lock.lock();
                
                while(songsListInUse){
                    songslistOperation.await();
                }
                
                songsListInUse = true;

                songsList.addElement(this.musicName);
                songsListAux.add(this.musicName);
                
                duration.add(this.musicDuration);
                durationAux.add(this.musicDuration);
                
                songsListInUse = false;
                songslistOperation.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }          
        }
    }

    /*
    * Essa é a classe usada para imprimir a lista de reprodução. 
    * A lógica dessa thread para evitar a condição de corrida é a mesma comentada na thread de adição
    * com exceção, claro, de que essa apenas lista as músicas que estão na lista de reprodução.
    */
    public class RandomSongThread extends Thread {

        @Override public void run(){
            try {
                lock.lock();
                
                while(songsListInUse){
                    songslistOperation.await();
                }
                                
                songsListInUse = true;

                Random random = new Random();

                for (int i = 0; i < songsList.size(); ++i){
                    int j = random.nextInt(songsList.size());

                    String temp = songsList.get(i);
                    songsList.set(i, songsList.get(j));
                    songsList.set(j, temp);

                    int tempDuration = duration.get(i);
                    duration.set(i, duration.get(j));
                    duration.set(j, tempDuration);
                }
                
                
                songsListInUse = false;
                songslistOperation.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }

    public class SequentialSongThread extends Thread {

        @Override public void run(){
            try {
                lock.lock();
                
                while(songsListInUse){
                    songslistOperation.await();
                }
                                
                songsListInUse = true;

                for (int i = 0; i < songsList.size(); i++) {
                    songsList.set(i, songsListAux.get(i));
                    duration.set(i, durationAux.get(i));
                }
                
                songsListInUse = false;
                songslistOperation.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }

    /*
    * Essa é a classe usada para remover músicas da lista de reprodução. 
    * A lógica dessa thread para evitar a condição de corrida é a mesma comentada na thread de adição
    * com exceção, claro, de que essa removerá uma música de índice pedido da lista de reprodução.
    */
    class RemoveSongThread extends Thread {
        int idx;

        public RemoveSongThread(int removeIdx){
            this.idx = removeIdx;
        }

        @Override public void run(){

            try {
                lock.lock();
                
                while(songsListInUse){
                    songslistOperation.await();
                }
                
                songsListInUse = true;

                String removedSong = songsList.remove(this.idx);
                duration.remove(this.idx);

                int idxRemovedSong = songsListAux.indexOf(removedSong);
                songsListAux.remove(idxRemovedSong);
                durationAux.remove(idxRemovedSong);
                
                songsListInUse = false;
                songslistOperation.signalAll();
            }  catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            } 
            
        }
    }

    public void addThread(String title, int duration){
        new AddSongThread(title, duration).start();
    }

    public void removeThread(int index){
        new RemoveSongThread(index).start();
    }

    public void shuffleTread(){
        new RandomSongThread().start();
    }

    public void sequentialTread(){
        new SequentialSongThread().start();
    }
}
