import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

public class MixingBuffer {//placeholder class name for now

    private ArrayList<File> files = new ArrayList<>();//instantiate list so can add to it

    public MixingBuffer(File... audioIn) {//variable arguments to add audio files

        for (File f : audioIn)
            files.add(f);
    }

    public void add(File f) {//option to add audio files later
        files.add(f);
    }

    public String filesToString() {
        return files.toString();

    }

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

    private int findLongest(ArrayList<byte[]> arrays) {//finding the length of longest byte array
        int largest = 0;

        for (byte[] b : arrays)//check each array
        {
            if (b.length > largest)
                largest = b.length;
        }

        return largest;//return the length of the longest array - not the byte array itself
    }

    private int findShortest(ArrayList<byte[]> arrays){//finding the length of shortest byte array
            int shortest=10000000;//arbitrary max value, could use first value either

            for(byte[] b: arrays)//check each array
            {
                if(b.length < shortest)//sort of guaranteed the 1st is shortest here
                    shortest = b.length;
            }

            return shortest;//return the shortest of the longest array - not the byte array itself
    }

    public void synthesise() {//returns the synthesised crowd
        try{

            AudioInputStream test = AudioSystem.getAudioInputStream(files.get(0));//hopefully won't need this after some changes

            byte[] buffer = new byte[640000000];//arbitrarily large buffer

            ArrayList<byte[]> byteArrays = new ArrayList<>();

            for(File f: files)
                byteArrays.add(toByteArray(f));//now have a list of the files in byte array form

            for(int i=0;i<findShortest(byteArrays);i++)//loop the length of the shortest byte array so no null pointer exceptions
                //need to put more time into this loop so that when the end of one file's buffer is reached it keeps adding the rest of the other files
            {
                byte temp = 0;

                for(byte[] b: byteArrays)
                {
                    if(i <= b.length)//avoid null pointer exceptions
                    {
                        temp += b[i];//just lazily adding the files together byte by byte
                    }
                }

                buffer[i] = temp;//storing the added bytes in the buffer
            }

            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),test.getFormat(),156000);
            //need to find a proper value for length and ideally a more static version of the audio format

            AudioSystem.write(out, AudioFileFormat.Type.WAVE, new File("crowd.wav"));//writing the out buffer to a file
      }
        catch (Exception e){
            e.printStackTrace();
        }

        return;
    }

    public void play(){//method to play our newly synthesised crowd audio file

        try{
            Clip clip = AudioSystem.getClip();

            clip.open(AudioSystem.getAudioInputStream(new File("crowd.wav")));

            clip.start();

            System.out.println("Now playing: crowd.wav" );

            Thread.sleep( clip.getMicrosecondLength()/1000);//need the program to keep running until the sound is played
            //microseconds vs miliseconds so need to divide by 1000

            System.out.println("Playback finished");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}