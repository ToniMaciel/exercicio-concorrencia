public class Song extends Thread{
    String name, artist;
    int duration, id;

    public Song(String name, String artist, int duration, int id){
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println(this.id + " \t " + this.name.replaceAll(" ", "") + " \t " + this.artist.replaceAll(" ", "")  + " \t " + this.duration);
    }
}
