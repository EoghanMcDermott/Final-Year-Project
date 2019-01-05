import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

            dir = new File("resources/old/");

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
        try{

            byte empty = 0;//can use this for an offset perhaps?

            byte[] buffer = new byte[640000000];//arbitrarily large buffer

            ArrayList<byte[]> byteArrays = new ArrayList<>();

            for(File f: files)
                byteArrays.add(toByteArray(f));//now have a list of the files in byte array form


            byte temp;

            for(int i=0;i<byteArrays.get(0).length;i++)
            {
                buffer[i]=byteArrays.get(0)[i];
            }

            int offest1 = byteArrays.get(0).length * 1/2;
            int offest2 = byteArrays.get(0).length * 3/4;

            for(int i=0;i<byteArrays.get(1).length;i++)
            {
                buffer[i+offest1] += byteArrays.get(1)[i] ;
                buffer[i+offest2] += byteArrays.get(2)[i] ;

            }


            AudioFileFormat format = AudioSystem.getAudioFileFormat(files.get(0));
            //need to create an audio file format object for this to work properly

//            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(output.toByteArray()), format.getFormat(), 1560000);
            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),format.getFormat(),1560000);
            //need to find a proper value for length and ideally a more static version of the audio format

            AudioSystem.write(out, AudioFileFormat.Type.WAVE, new File(filename));//writing the out buffer to a file

      }
        catch (Exception e){
            e.printStackTrace();
        }

        return;
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