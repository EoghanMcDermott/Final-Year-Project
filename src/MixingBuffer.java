import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class MixingBuffer {//placeholder class name for now

    private ArrayList<File> files = new ArrayList<>();//instantiate list so can add to it
    private static final int bufferLength = 1560000;//value subject to change
    private static final String filename = "crowd.wav";//might want to change this later to reflect the type of crowd

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

    //private void populate(){} - can use this to vary list of samples rather than everything in all together

    public void synthesise() {//creates the synthesised crowd

        try{

            byte[] buffer = new byte[bufferLength];//arbitrarily large buffer
            byte emptyByte = 0;

            Arrays.fill(buffer, emptyByte);

            File dir = new File("resources/audio_samples/");
            for(File f: dir.listFiles())
                files.add(f);
            //add all audio files in resources folder to list to draw crowd sounds from

            LinkedList<byte[]> byteArrays = new LinkedList<>();

            for(File f: files)
                byteArrays.add(toByteArray(f));//now have a list of the files in byte array form


            int offset = 0;//no offset for the very first file

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


            AudioFileFormat format = AudioSystem.getAudioFileFormat(files.get(0));
            //need to create an audio file format object for this to work properly

            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),format.getFormat(),bufferLength);
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

        int offset = rand.nextInt(1560000/9*10);
        //return a number uniformly anywhere through (most of) the buffer

        return offset;
    }

    public void play(String filename){//method to play our newly synthesised crowd audio file

        try{
            Thread.sleep(2000);
            Clip clip = AudioSystem.getClip();

            clip.open(AudioSystem.getAudioInputStream(new File(filename)));

            clip.start();

            Thread.sleep(clip.getMicrosecondLength()/1000);//need the program to keep running until the sound is played
            //microseconds vs milliseconds so need to divide by 1000

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