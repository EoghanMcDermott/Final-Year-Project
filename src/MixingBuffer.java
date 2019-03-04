import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MixingBuffer {

    private ArrayList<File> files = new ArrayList<>();//instantiate list so can add to it
    private static final int oneSecond = 176400/2;//1 second of 16bit 44.1khz of pcm audio
    private int bufferLength;
    private int numSeconds;
    private String filename = "crowd.wav";//might want to change this later to reflect the type of crowd
    private int crowdIterations = 0;


    private void updateFilename()
    { filename = "crowd" + crowdIterations + ".wav"; }


    private void populate(int numSamples)//add samples to list to create crowd sound from
    {
        files.clear();//clear out any old files

        Random rand = new Random();

        File dirMale = new File("resources/audio_samples/male/");
        File dirFemale = new File("resources/audio_samples/female/");

        ArrayList<File> listFiles = new ArrayList<>();

        for(File f: dirFemale.listFiles())
            listFiles.add(f);

        for(File f: dirMale.listFiles())
            listFiles.add(f);
        //listfiles now contains all files from both male and female directories

        int count = 0;

        while (count < numSamples)
        {
            int index = rand.nextInt(listFiles.size());
            files.add(listFiles.get(index));
            count++;
        }//randomly add samples to list

        Collections.shuffle(files);//shuffling said list to improve variability

    } //can use this to vary list of samples rather than everything in all together


    private void setBufferLength(int seconds)
    {
        numSeconds = seconds;//might be handy to globally keep track of this - if using clip method
        bufferLength = oneSecond * seconds;
    }


    public void synthesise(int numSamples, int duration) {//creates the synthesised crowd

        try{

           setBufferLength(duration);//want buffer of appropriate length

            updateFilename();

            short[] buffer = new short[bufferLength];//buffer of appropriate length - 8 bit issue?

            byte emptyByte = 0;
            Arrays.fill(buffer, emptyByte);//fill buffer with 0's

            populate(numSamples);//add files to sample list

            LinkedList<short[]> convertedFiles = new LinkedList<>();

            Converter converter = new Converter();

            for(File f: files)
                convertedFiles.add(converter.toShortArray(f));//now have a list of the files in byte array form


            int offset = 0;//no offset for the very first file

            while(!convertedFiles.isEmpty())//until every sample has been added
            {
                short[] curr = convertedFiles.pop();//get a sample from list


                    for (int i =0; i < curr.length; i++)
                    {
                       if(i+offset < bufferLength)
                           buffer[i+offset] += curr[i];
                    }

               offset = randomiseOffset();//next sample placed in a random location in the buffer
            }

            byte[] bufferInBytes = converter.shortToByteArray(buffer);


            AudioFileFormat format = AudioSystem.getAudioFileFormat(files.get(0));
            //need to create an audio file format object for this to work properly

            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(bufferInBytes),format.getFormat(),bufferLength);

            AudioSystem.write(out, AudioFileFormat.Type.WAVE, new File(filename));//writing the out buffer to a file

            crowdIterations++;//don't want weird overlapping files
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private int randomiseOffset()//want to randomly offset different samples
    {
        Random rand = new Random();

        return rand.nextInt(bufferLength/2)*2;
        //want an even value so that mixing high and low is avoided
    }


    public void play(){//method to play our newly synthesised crowd audio file

        try{
            Clip clip = AudioSystem.getClip();

            clip.open(AudioSystem.getAudioInputStream(new File(filename)));

            clip.start();

            clip.drain();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getFilename()//return name of generated crowd file to display in ui
    {return filename;}


    public String getFiles()//list files to display in ui
    {
        String str = "";

        for(File f: files)
            str += f.toString() + "\n";

        return str;
    }
}