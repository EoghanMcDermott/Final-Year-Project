public class Main {

    public static void main(String args[]){

       MixingBuffer mix = new MixingBuffer();//instantiate mixing buffer with(out) audio files

       //audio samples are okay but aren't mixed properly together
       mix.generateOld();
       mix.play("old_crowd.wav");
    }
}
