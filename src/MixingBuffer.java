import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MixingBuffer {//placeholder class name for now

    private ArrayList<File> files = new ArrayList<>();//instantiate list so can add to it
    private static final int bufferLength = 1560000;

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

    public void generateMarcus()
    {
        populate("marcus");
        synthesise("marcus_crowd.wav");
    }

    public void generateRod()
    {
        populate("rod");
        synthesise("rod_crowd.wav");
    }

    public void generateOld()
    {
        populate("old");
        synthesise("old_crowd.wav");
        //synthesiseOld();
    }

    private void populate(String inputType)//right now only one type of sample to populate
    {
        boolean validInput = true;

        File dir = new File("resources");//need to give a default value - here it is all of the samples

        //check if we want a normal, soft or shouting crowd
        if(inputType.equals("old"))
            dir = new File("resources/shortened/");

        else if(inputType.equals("marcus"))
            dir = new File("resources/marcus/");

        else if(inputType.equals("rod"))
            dir = new File("resources/rod/");

        else//if none of these then the input is invalid
            validInput = false;

        if(validInput)//if our input is valid then populate file arrays list from given resource folder
        {

            for(File sample: dir.listFiles())
                files.add(sample);

            Collections.shuffle(files);//shuffling list to give more varied crowd sounds
        }

    }

    private void synthesise(String filename) {//returns the synthesised crowd

        //hey maybe add samples iteratively rather than all at once
        //wow this might actually be a really good idea - will it work with offsets though?

        try{

            byte[] buffer = new byte[bufferLength];//arbitrarily large buffer
            byte emptyByte = 0;

            Arrays.fill(buffer, emptyByte);

            LinkedList<byte[]> byteArrays = new LinkedList<>();

            for(File f: files)
                byteArrays.add(toByteArray(f));//now have a list of the files in byte array form


            int offset = 0;

            while(!byteArrays.isEmpty())//until every sample has been added - no repetition of samples - change populate method
            {
                byte[] curr = byteArrays.pop();//get a sample from list

                for(int i=offset;i<curr.length && i<bufferLength ;i++)//iterate through that sample
                {
                    buffer[i] += curr[i];
                }//add a sample to buffer

                offset = randomiseOffest();
                //get offset for the next sample to be added
            }


            AudioFileFormat format = AudioSystem.getAudioFileFormat(files.get(0));
            //need to create an audio file format object for this to work properly

//            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(output.toByteArray()), format.getFormat(), 1560000);
            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),format.getFormat(),bufferLength);
            //need to find a proper value for length and ideally a more static version of the audio format

            AudioSystem.write(out, AudioFileFormat.Type.WAVE, new File(filename));//writing the out buffer to a file

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return;
    }

    private int randomiseOffest()//want to randomly offset different samples
    {
        Random rand = new Random();

        int offset = rand.nextInt(1560000/9*10);
        //return a number uniformly anywhere through (most of) the buffer

        return offset;
    }

    private void synthesiseOld()
    {
        try {

            byte empty = 0;//can use this for an offset perhaps?

            byte[] buffer = new byte[6400000];//arbitrarily large buffer

            ArrayList<byte[]> byteArrays = new ArrayList<>();

            for (File f : files)
                byteArrays.add(toByteArray(f));//now have a list of the files in byte array form


            byte temp;
            for (int i = 0; i < findShortest(byteArrays); i++)//loop the length of the shortest byte array so no null pointer exceptions
            //need to put more time into this loop so that when the end of one file's buffer is reached it keeps adding the rest of the other files
            {
                temp = 0;

                for (byte[] b : byteArrays) {
                    if (i <= b.length)//avoid null pointer exceptions
                    {
                        temp += b[i];//just lazily adding the files together byte by byte
                    }
                }

                buffer[i] = temp;//storing the added bytes in the buffer
            }

            AudioFileFormat format = AudioSystem.getAudioFileFormat(files.get(0));
            //need to create an audio file format object for this to work properly

//            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(output.toByteArray()), format.getFormat(), 1560000);
            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),format.getFormat(),156000);
            //need to find a proper value for length and ideally a more static version of the audio format

            AudioSystem.write(out, AudioFileFormat.Type.WAVE, new File("old_crowd.wav"));//writing the out buffer to a file
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void play(String filename){//method to play our newly synthesised crowd audio file

        try{
            Thread.sleep(500);
            Clip clip = AudioSystem.getClip();

            clip.open(AudioSystem.getAudioInputStream(new File(filename)));

            clip.start();

            System.out.println("Now playing: " + filename );

            Thread.sleep( clip.getMicrosecondLength()/1000);//need the program to keep running until the sound is played
            //microseconds vs miliseconds so need to divide by 1000

            System.out.println("Playback finished");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}