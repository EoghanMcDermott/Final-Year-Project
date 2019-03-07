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


    private void populate(int numSamples, int mfRatio, int softLoud)//add samples to list to create crowd sound from
    {
        files.clear();//clear out any old files

        Random rand = new Random();

        HashMap<String,File[]> maleDirs = new HashMap<>();
        HashMap<String,File[]> femaleDirs = new HashMap<>();

        File[] tempDir;

        tempDir = new File("resources/audio_samples/male/soft").listFiles();
        maleDirs.put("soft", tempDir);
        tempDir = new File("resources/audio_samples/male/normal").listFiles();
        maleDirs.put("normal", tempDir);
        tempDir = new File("resources/audio_samples/male/loud").listFiles();
        maleDirs.put("loud", tempDir);

        tempDir =  new File("resources/audio_samples/female/soft").listFiles();
        femaleDirs.put("soft", tempDir);
        tempDir =  new File("resources/audio_samples/female/normal").listFiles();
        femaleDirs.put("normal", tempDir);
        tempDir =  new File("resources/audio_samples/female/loud").listFiles();
        femaleDirs.put("loud", tempDir);
        //populating hash maps for male and female audio samples

        if(mfRatio == 0)//if we want a higher percentage of women than men
            mfRatio++;


        //works okay with male - if female > 50% it goes all female

        int totalFemale = numSamples / (100 /mfRatio);
        int numFemaleSoft = (totalFemale / (100 / softLoud)) * 4 / 5;//leave some headroom for normal samples?
        int numFemaleLoud = (totalFemale * softLoud) / 100 * 4 / 5;
        int numFemaleNorm = totalFemale - numFemaleSoft - numFemaleLoud;

        int totalMale = numSamples - totalFemale;
        int numMaleSoft = (totalMale / (100 / softLoud)) * 4 / 5;
        int numMaleLoud = (totalMale * softLoud) / 100 * 4 / 5;
        int numMaleNorm = totalMale - numMaleSoft - numMaleLoud;

        //calculating amount of a sample type to add for each category - formulae might need work

        System.out.println("Total Female: " + totalFemale);
        System.out.println("Loud Female: " + numFemaleLoud);
        System.out.println("Soft Female: " + numFemaleSoft);
        System.out.println("Normal Female: " + numFemaleNorm);
        System.out.println("Total Male: " + totalMale);
        System.out.println("Loud Male: " + numMaleLoud);
        System.out.println("Soft Female: " + numMaleSoft);
        System.out.println("Normal Female: " + numMaleNorm + "\n\n");


        tempDir = femaleDirs.get("soft");//adding soft female samples
        for(int i=0;i<numFemaleSoft && i<tempDir.length;i++)
        {
            int index = rand.nextInt(tempDir.length);
            files.add(tempDir[index]);
        }

        tempDir = femaleDirs.get("normal");//adding normal male samples
        for(int i=0;i<numFemaleNorm && i<tempDir.length;i++)
        {
            int index = rand.nextInt(tempDir.length);
            files.add(tempDir[index]);
        }

        tempDir = femaleDirs.get("loud");//adding loud female samples
        for(int i=0;i<numFemaleLoud && i<tempDir.length;i++)
        {
            int index = rand.nextInt(tempDir.length);
            files.add(tempDir[index]);
        }

        tempDir = maleDirs.get("soft");//adding soft male samples
        for(int i=0;i<numMaleSoft && i<tempDir.length;i++)
        {
            int index = rand.nextInt(tempDir.length);
            files.add(tempDir[index]);
        }

        tempDir = maleDirs.get("normal");//adding normal male samples
        for(int i=0;i<numMaleNorm && i<tempDir.length;i++)
        {
            int index = rand.nextInt(tempDir.length);
            files.add(tempDir[index]);
        }

        tempDir = maleDirs.get("loud");//adding loud male samples
        for(int i=0;i<numMaleLoud && i<tempDir.length;i++)
        {
            int index = rand.nextInt(tempDir.length);
            files.add(tempDir[index]);
        }
        //should have properly added the right amount of samples now
        

        Collections.shuffle(files);//shuffling said list to improve variability

    } //can use this to vary list of samples rather than everything in all together


    private void setBufferLength(int seconds)
    {
         bufferLength = oneSecond * seconds;
    }


    public void synthesise(int numSamples, int duration, int mfRatio, int softVsLoud) {//creates the synthesised crowd

        try
        {
           setBufferLength(duration);//want buffer of appropriate length

            updateFilename();

            short[] buffer = new short[bufferLength];//buffer of appropriate length

            byte emptyByte = 0;
            Arrays.fill(buffer, emptyByte);//fill buffer with 0's so += works okay

            populate(numSamples, mfRatio, softVsLoud);//add files to sample list

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