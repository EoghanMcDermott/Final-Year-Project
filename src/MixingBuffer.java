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
    private static final int oneSecond = 176400;//1 second of 16bit 44.1khz of pcm audio
    private int bufferLength;
    private int numSeconds;
    private String filename = "crowd.wav";//might want to change this later to reflect the type of crowd
    private int crowdIterations = 0;
    private ArrayList<Clip> clips = new ArrayList<>();

    private byte[] toByteArray(File file){
        try{
            AudioInputStream in = AudioSystem.getAudioInputStream(file);

            byte[] byteArray = new byte[(int)file.length()];//make sure the size is correct

            while (in.read(byteArray) != -1);//read in byte by byte until end of audio input stream reached

//            AudioFileFormat format = AudioSystem.getAudioFileFormat(file);
//            //need to create an audio file format object for this to work properly
//            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(byteArray), format.getFormat(),(int) file.length());
//            AudioSystem.write(out, AudioFileFormat.Type.WAVE,new File("test.wav"));

            return byteArray;//return the new byte array

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;//encountered a problem with converting the file to byte array
    }

    private void toClip(File file)
    {
        try
        {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);

            Clip clip = AudioSystem.getClip();

            clip.open(AudioSystem.getAudioInputStream(file));

            clips.add(clip);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updateFilename()
    { filename = "crowd" + Integer.toString(crowdIterations)+".wav"; }

    private void populate(int numSamples)//add samples to list to create crowd sound from
    {
        files.clear();//clear out any old files

        Random rand = new Random();

        File dirMale = new File("resources/audio_samples/mono/");
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

            byte[] buffer = new byte[bufferLength];//buffer of appropriate length - 8 bit issue?

            byte emptyByte = 0;
            Arrays.fill(buffer, emptyByte);//fill buffer with 0's

            populate(numSamples);//add files to sample list

            LinkedList<byte[]> convertedFiles = new LinkedList<>();

            for(File f: files)
                convertedFiles.add(toByteArray(f));//now have a list of the files in byte array form
//
//            for(Clip c : clips)
//            {
//                c.open();
//                c.start();
//
//                Thread.sleep(randomiseOffset()*1000);
//            }


            int offset = 0;//no offset for the very first file

            while(!convertedFiles.isEmpty())//until every sample has been added
            {
                byte[] curr = convertedFiles.pop();//get a sample from list

//                for(int i=0;i<curr.length && (i+offset)<bufferLength ;i++)//iterate through that sample
//                {
//                    buffer[i+offset] = ((buffer[i+offset] + curr[i])/2);
//
//                }//add a sample to buffer

                for(int i=0;i<curr.length && (i+offset)<bufferLength;i++)
                    buffer[i] += curr[i];

               offset = randomiseOffset();//next sample placed in a random location in the buffer
            }

            AudioFileFormat format = AudioSystem.getAudioFileFormat(files.get(0));
            //need to create an audio file format object for this to work properly

            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),format.getFormat(),bufferLength/2);

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

        return rand.nextInt(bufferLength)*95/100;
        //return a number uniformly anywhere through (most of) the buffer
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


//    public void mixClips()
//    {
//        try
//        {
//            setBufferLength(20);
//
//            ArrayList<Clip> c = clips;
//
//            System
//
//            while(!c.isEmpty()) {
//                Clip curr = c.get(0);
//
//                curr.start();
//
//                //Thread.sleep(randomiseOffset());
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//
//    }
}