import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

public class MixingBuffer {//used to mix .WAV files together to make new .WAV file

    private ArrayList<File> files = new ArrayList<>();//instantiate list so can add to it
    private static final int oneSecond = 176400/2;//1 second of 16bit 44.1khz of pcm audio
    private int bufferLength;
    private String filename = "crowd.wav";//might want to change this later to reflect the type of crowd
    private int crowdIterations = 0;


    private void updateFilename()//use to create new files rather than constantly overwrite one file only
    { filename = "crowd" + crowdIterations + ".wav"; }


    private void populate(int numSamples, int mfRatio)//add samples to list to create crowd sound from
    {
        files.clear();//clear out any old files

        Random rand = new Random();

        File[] dirMale = new File("resources/audio_samples/male/").listFiles();
        File[] dirFemale = new File("resources/audio_samples/female/").listFiles();

        List<File> temp = Arrays.asList(dirFemale);
        Collections.shuffle(temp);
        dirFemale = (File[]) temp.toArray();

        temp = Arrays.asList(dirMale);
        Collections.shuffle(temp);
        dirMale = (File[]) temp.toArray();
        //shuffling both directories to get more varied results

        int percentage = numSamples*mfRatio/100;
        int count = 0;

        while(count < percentage)
        {
            int index = rand.nextInt(dirMale.length);
            files.add(dirMale[index]);
            count++;
        }

        while (count < numSamples)
        {
            int index = rand.nextInt(dirFemale.length);
            files.add(dirFemale[index]);
            count++;
        }//randomly add female samples to list

        Collections.shuffle(files);//shuffling said list to improve variability

    } //can use this to vary list of samples rather than everything in all together


    private void setBufferLength(int seconds)
    {
         bufferLength = oneSecond * seconds;
    }


    public void synthesise(int numSamples, int duration, int mfRatio) {//creates the synthesised crowd

        try{

           setBufferLength(duration);//want buffer of appropriate length

            updateFilename();

            short[] buffer = new short[bufferLength];//buffer of appropriate length

            byte emptyByte = 0;
            Arrays.fill(buffer, emptyByte);//fill buffer with 0's so += works okay

            populate(numSamples, mfRatio);//add files to sample list

            LinkedList<short[]> convertedFiles = new LinkedList<>();

            Converter converter = new Converter();

            for(File f: files)
                convertedFiles.add(converter.toShortArray(f));//now have a list of the files in short array form


            int offset = 0;//no offset for the very first file

            while(!convertedFiles.isEmpty())//until every sample has been added
            {
                short[] curr = convertedFiles.pop();//get a sample from list


                    for (int i =0; i < curr.length; i++)
                    {
                       if(i+offset < bufferLength)//avoid out of bounds exception
                           buffer[i+offset] += curr[i];

                       //need to add scaling if go over/under max/min short values
                    }

               offset = randomiseOffset();//next sample placed in a random location in the buffer
            }

            byte[] bufferInBytes = converter.shortToByteArray(buffer);
            //convert back to byte[] so can write to new file with AudioSystem object


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