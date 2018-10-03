import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;

public class MixingBuffer {//placeholder class name for now

    private ArrayList<File> samples = new ArrayList<>();//instantiate list so can add to it

    public MixingBuffer(File ... audioIn){//variable arguments to add audio files

        for(File f: audioIn)
            samples.add(f);
    }

    public void add(File f){//option to add audio files later
        samples.add(f);
    }

    public String samplesToString(){
        return samples.toString();
    }
}
