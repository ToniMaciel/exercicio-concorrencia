import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.DefaultListModel;

import java.util.Collections;
import java.util.List;

public class MusicPlayer {
    
    public Lock lock = new ReentrantLock(); //Lock que será usado na região critica (lista das músicas)
    public Condition songslistOperation = lock.newCondition();
    public boolean songsListInUse = false; //Para saber se alguma thread está fazendo alguma operação com a lista de reprodução
    public DefaultListModel<String> songsList = new DefaultListModel<>(); // Lista que possui strings que definem o nome da música e o artista
    public ArrayList<Integer> duration = new ArrayList<>(); //	Lista que possui as durações das músicas de mesmo indice na lista anterior
    public List<Integer> indexOrder = new ArrayList<>();
    public boolean random = false;
    private int idxp = 0;

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
                duration.add(this.musicDuration);

                indexOrder.add(songsList.size() - 1);
                
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
    public class ListSongThread extends Thread {

        @Override public void run(){
            try {
                lock.lock();
                
                while(songsListInUse){
                    songslistOperation.await();
                }
                                
                songsListInUse = true;

                for (int i = 0; i < songsList.size(); ++i)
                    System.out.println(songsList.get(i));
                
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

                songsList.remove(this.idx);
                duration.remove(this.idx);
                
                songsListInUse = false;
                songslistOperation.signalAll();
            }  catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            } 
            
        }
    }

    class ShuffleSongThread extends Thread {

        @Override public void run() {
            int idx = indexOrder.get(idxp);

            try {
                lock.lock();

                while(songsListInUse){
                    songslistOperation.await();
                }
                
                songsListInUse = true;

                random ^= true;
                if (random)
                    Collections.shuffle(indexOrder);
                else {
                    indexOrder.clear();
                    for (int i = 0; i < songsList.size(); ++i)
                        indexOrder.add(i);
                }

                System.out.println(indexOrder);

                songsListInUse = false;
                songslistOperation.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }

            getIndex(true, idx);
        }
    }

    public void randomMusicThread(){
        new ShuffleSongThread().start();
    }

    public void addThread(String title, int duration){
        new AddSongThread(title, duration).start();
    }

    public void removeThread(int index){
        new RemoveSongThread(index).start();
    }

    public int getIndex(boolean force, int update) {
        try {
            lock.lock();

            while(songsListInUse){
                songslistOperation.await();
            }
            
            songsListInUse = true;

            if (force)
                idxp = indexOrder.indexOf(update);
            else 
                idxp = (idxp + indexOrder.size() + update) % indexOrder.size();

            songsListInUse = false;
            songslistOperation.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
        }

        return indexOrder.get(idxp);
    }
}
