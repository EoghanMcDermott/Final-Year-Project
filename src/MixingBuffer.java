import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MixingBuffer {//placeholder class name for now

    private ArrayList<File> files = new ArrayList<>();//instantiate list so can add to it
    private static final int bufferLength = 15600;//value subject to change
    private static final String filename = "crowd.wav";//might want to change this later to reflect the type of crowd
    private int numSeconds = 10;

    private byte[] toByteArray(File file){
        try{
            AudioInputStream in = AudioSystem.getAudioInputStream(file);

            byte[] byteArray = new byte[in.available()];//make sure the size is correct

            while(in.read(byteArray) != -1);//read in byte by byte until end of buffer reached

            return byteArray;//return the new byte array
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;//encountered a problem with converting the file to byte array
    }

    private void populate(int numSamples)//add samples to list to create crowd sound from
    {
        files.clear();//clear out any old files

        int count = 0;

        File dir = new File("resources/audio_samples/");
        File[] listFiles = dir.listFiles();

        while (count < numSamples)
        {
            files.add(listFiles[count]);
            count++;
        }

        Collections.shuffle(files);

        System.out.println(files.toString());

    } //can use this to vary list of samples rather than everything in all together

    public void synthesise(int numSamples, int numSeconds) {//creates the synthesised crowd

        try{
           // Files.deleteIfExists(Paths.get("crowd.wav"));//delete any old file - not working now maybe in future

            byte[] buffer = new byte[bufferLength*numSeconds];//arbitrarily large buffer
            byte emptyByte = 0;

            Arrays.fill(buffer, emptyByte);

            LinkedList<byte[]> byteArrays = new LinkedList<>();

            populate(numSamples);//add files to sample list

            for(File f: files)
                byteArrays.add(toByteArray(f));//now have a list of the files in byte array form


            int offset = 0;//no offset for the very first file

            while(!byteArrays.isEmpty())//until every sample has been added - no repetition of samples - change populate method
            {
                byte[] curr = byteArrays.pop();//get a sample from list

                for(int i=offset;i<curr.length && i<(bufferLength*numSeconds) ;i++)//iterate through that sample
                {
                    buffer[i] += curr[i];
                }//add a sample to buffer

                offset = randomiseOffset();
                //next sample placed in a random location in the buffer
            }


            AudioFileFormat format = AudioSystem.getAudioFileFormat(files.get(0));
            //need to create an audio file format object for this to work properly

            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),format.getFormat(),bufferLength*numSeconds);
            //need to find a proper value for length and ideally a more static version of the audio format

            AudioSystem.write(out, AudioFileFormat.Type.WAVE, new File(filename));//writing the out buffer to a file

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return;
    }

    private int randomiseOffset()//want to randomly offset different samples
    {
        Random rand = new Random();

        int offset = rand.nextInt(bufferLength*numSeconds);
        //return a number uniformly anywhere through (most of) the buffer

        return offset;
    }

    public void play(String filename){//method to play our newly synthesised crowd audio file

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