import java.io.File;

public class Main {

    public static void main(String args[]){

       File v1 = new File("resources/dwight_1.wav");
       File v2 = new File("resources/jim.wav");
       File v3 = new File("resources/pam.wav");
       File v4 = new File("resources/michael.wav");
       File v5 = new File("resources/erin.wav");
       File v6 = new File("resources/oscar.wav");
       File v7 = new File("resources/phyllis.wav");
       File v8 = new File("resources/catherine.wav");
       //audio samples

       //bit awkward passing the files - maybe just access them directly in mixing buffer class?

       MixingBuffer mix = new MixingBuffer(v1,v2,v3,v4,v5,v6,v7,v8);//instantiate mixing buffer with audio files

       //System.out.println(mix.filesToString());

       mix.synthesise();

       mix.play();
    }
}
