public class Main {

    public static void main(String args[]){

       MixingBuffer mix = new MixingBuffer();//instantiate mixing buffer with(out) audio files

       mix.generateNormal();
       mix.play("normal.wav");

       //audio samples are okay but aren't mixed properly together
       mix.generateSoft();
       mix.play("soft.wav");

       mix.generateShout();
       mix.play("shout.wav");


    }
}
