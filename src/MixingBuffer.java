import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MixingBuffer {

    private ArrayList<File> files = new ArrayList<>();//instantiate list so can add to it
    private static final int oneSecond = 176400;//1 second of 16bit 44.1khz of pcm audio
    private int bufferLength;
    private String filename = "crowd.wav";//might want to change this later to reflect the type of crowd
    private int crowdIterations = 0;

    private byte[] toByteArray(File file){
        try{
            AudioInputStream in = AudioSystem.getAudioInputStream(file);

            byte[] byteArray = new byte[in.available()];//make sure the size is correct

            while (in.read(byteArray) != -1);//read in byte by byte until end of audio input stream reached

            return byteArray;//return the new byte array
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;//encountered a problem with converting the file to byte array
    }

    private void updateFilename()
    {
        filename = "crowd" + Integer.toString(crowdIterations)+".wav";

    }

    private void populate(int numSamples)//add samples to list to create crowd sound from
    {
        files.clear();//clear out any old files

        Random rand = new Random();

        int count = 0;

        File dir = new File("resources/audio_samples/");
        File[] listFiles = dir.listFiles();

        while (count < numSamples)
        {
            int index = rand.nextInt(listFiles.length);
            files.add(listFiles[index]);
            count++;
        }

        Collections.shuffle(files);

    } //can use this to vary list of samples rather than everything in all together

    private void setBufferLength(int seconds)
    {
        bufferLength = oneSecond * seconds;
    }

    public void synthesise(int numSamples, int duration) {//creates the synthesised crowd

        try{

           setBufferLength(duration);//want buffer of appropriate length

            updateFilename();

            byte[] buffer = new byte[bufferLength];//arbitrarily large buffer
            byte emptyByte = 0;

            Arrays.fill(buffer, emptyByte);

            LinkedList<byte[]> byteArrays = new LinkedList<>();

            populate(numSamples);//add files to sample list

            for(File f: files)
                byteArrays.add(toByteArray(f));//now have a list of the files in byte array form


            int offset = 0;//no offset for the very first file

            AudioFileFormat format = AudioSystem.getAudioFileFormat(files.get(0));
            //need to create an audio file format object for this to work properly

            while(!byteArrays.isEmpty())//until every sample has been added - no repetition of samples - change populate method
            {
                byte[] curr = byteArrays.pop();//get a sample from list

                for(int i=offset;i<curr.length && i<bufferLength ;i++)//iterate through that sample
                {
                    buffer[i] += curr[i];
                }//add a sample to buffer

                offset = randomiseOffset();
                //next sample placed in a random location in the buffer
            }

            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),format.getFormat(),bufferLength/4);
            //need to find a proper value for length and ideally a more static version of the audio format
            out.mark(bufferLength);

            AudioSystem.write(out, AudioFileFormat.Type.WAVE, new File(filename));//writing the out buffer to a file

            out.reset();

            crowdIterations++;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private int randomiseOffset()//want to randomly offset different samples
    {
        Random rand = new Random();

        return rand.nextInt(bufferLength);// *95/100;
        //return a number uniformly anywhere through (most of) the buffer
    }

    public void play(){//method to play our newly synthesised crowd audio file

        try{
            Clip clip = AudioSystem.getClip();

            clip.open(AudioSystem.getAudioInputStream(new File(filename)));

            clip.start();

            clip.drain();

            if(!clip.isRunning())
                System.out.println("DONE");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getFilename()//return name of generated crowd file to display in ui
    {
        return filename;
    }

    public String getFiles()//list files to display in ui
    {
        String str = "";

        for(File f: files)
            str += f.toString() + "\n";

        return str;
    }
}