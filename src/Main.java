import java.io.File;

public class Main {

    public static void main(String args[]){

       File v1 = new File("voice_1.wav");
       File v2 = new File("voice_2.wav");
       File v3 = new File("voice_3.wav");
       //audio samples

        MixingBuffer mix = new MixingBuffer(v1,v2,v3);//instantiate mixing buffer with audio files

       System.out.println(mix.samplesToString());

       mix.synthesise();
    }
}
