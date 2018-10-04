import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;

public class MixingBuffer {//placeholder class name for now

    private ArrayList<File> samples = new ArrayList<>();//instantiate list so can add to it

    public MixingBuffer(File... audioIn) {//variable arguments to add audio files

        for (File f : audioIn)
            samples.add(f);
    }

    public void add(File f) {//option to add audio files later
        samples.add(f);
    }

    public String samplesToString() {
        return samples.toString();

    }

    public void synthesise() {//returns the synthesised crowd
        try{
           AudioInputStream[]streams = new AudioInputStream[samples.size()];

           for(int i=0;i<streams.length;i++)
               streams[i] = AudioSystem.getAudioInputStream(samples.get(i));
            //have an audio input stream for each sample audio file

            //now need to read each stream byte by byte, add the bytes together (?) - then write to crowds file
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return;
    }
}