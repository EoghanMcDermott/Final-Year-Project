import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

public class MixingBuffer {//used to mix .WAV files together to make new .WAV file

    private ArrayList<File> samples = new ArrayList<>();//instantiate list so can add to it
    private static final int oneSecond = 176400/2;//1 second of 16bit 44.1khz of pcm audio
    private int bufferLength;
    private String filename = "crowd.wav";//might want to change this later to reflect the type of crowd
    private int crowdIterations = 0;


    private void updateFilename()//use to create new files rather than constantly overwrite one file only
    { filename = "crowd" + crowdIterations + ".wav"; }


    private void populate(int numSamples, int mfRatio, int softLoud)//add samples to list to create crowd sound from
    {
        samples.clear();//clear out any old samples

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

        double malePercentage = (100- mfRatio)/100.0;
        double femalePercentage = mfRatio/100.0;
        //low value = more men and vice versa

        int totalMale = (int) (numSamples * malePercentage);
        int totalFemale = (int) (numSamples * femalePercentage);
        //calculating how many samples per gender

        //now need to calculate what percentage of soft and loud are
        double softPercentage = (100-softLoud)/100.0;
        double loudPercentage = softLoud/100.0;
        //low value = more soft than loud

        int totalMaleSoft = (int) (totalMale * softPercentage);
        int totalMaleLoud = (int) (totalMale * loudPercentage);

        int totalFemaleSoft = (int) (totalFemale * softPercentage);
        int totalFemaleLoud = (int) (totalFemale * loudPercentage);
        //calculating number of male & female samples of each type

        int totalMaleNormal,totalFemaleNormal;

        if(softPercentage < loudPercentage)//if more loud
        {
            totalFemaleNormal = totalFemaleLoud/2;
            totalMaleNormal = totalMaleLoud/2;

            totalFemaleSoft = totalFemale - totalFemaleLoud - totalFemaleNormal;//half as many normal samples as the more dominant in soft vs loud
            totalMaleSoft = totalMale - totalMaleLoud - totalMaleNormal;
        }
        else if(softPercentage > loudPercentage)//if more soft
        {
            totalFemaleNormal = totalFemaleSoft/2;
            totalMaleNormal = totalMaleSoft/2;

            totalFemaleLoud = totalFemale - totalFemaleSoft - totalFemaleNormal;
            totalMaleLoud = totalMale - totalMaleSoft - totalMaleNormal;

        }
        else//if even split
        {
            totalFemaleNormal = totalFemale/2;
            totalMaleNormal = totalMale/2;

            totalFemaleSoft = totalFemale/4;
            totalFemaleLoud = totalFemale/4;

            totalMaleSoft = totalMale/4;
            totalMaleLoud = totalMale/4;
        }//mainly normal with even soft and loud - resembles a bell curve


        //adding in the various types of female samples - NOT SUPER EFFICIENT
        tempDir = femaleDirs.get("soft");
        for(int i=0; i<totalFemaleSoft;i++)
            samples.add(tempDir[rand.nextInt(tempDir.length-1)]);


        tempDir = femaleDirs.get("loud");
        for(int i=0; i<totalFemaleLoud;i++)
            samples.add(tempDir[rand.nextInt(tempDir.length - 1)]);

       //fill rest of samples needed with normal
        tempDir = femaleDirs.get("normal");
        for(int i=0; i<totalFemaleNormal;i++)
            samples.add(tempDir[rand.nextInt(tempDir.length - 1)]);
        //female samples all added

        //time to add male samples
        tempDir = maleDirs.get("soft");
        for(int i=0; i<totalMaleSoft;i++)
            samples.add(tempDir[rand.nextInt(tempDir.length - 1)]);


        tempDir = maleDirs.get("loud");
        for(int i=0; i<totalMaleLoud;i++)
            samples.add(tempDir[rand.nextInt(tempDir.length - 1)]);


        //fill rest of samples needed with normal
        tempDir = maleDirs.get("normal");
        for(int i=0; i<totalMaleNormal;i++)
            samples.add(tempDir[rand.nextInt(tempDir.length - 1)]);
        
        //have now added all the samples from the various categories to the overall list


        Collections.shuffle(samples);//shuffling said list to improve variability
        
        //debug info
        System.out.println("Total Female: " + totalFemale + "   Total Male: " + totalMale);
        System.out.println("Soft Female: " + totalFemaleSoft);
        System.out.println("Loud Female: " + totalFemaleLoud);
        System.out.println("Normal Female: " + totalFemaleNormal);
        System.out.println("Soft Male: " + totalMaleSoft);
        System.out.println("Loud Male: " + totalMaleLoud);
        System.out.println("Normal Male: " + totalMaleNormal + "\n\n");

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

            populate(numSamples, mfRatio, softVsLoud);//add samples to sample list

            LinkedList<short[]> convertedFiles = new LinkedList<>();

            Converter converter = new Converter();

            for(File f: samples)
                convertedFiles.add(converter.toShortArray(f));//now have a list of the samples in short array form


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


            AudioFileFormat format = AudioSystem.getAudioFileFormat(samples.get(0));
            //need to create an audio file format object for this to work properly

            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(bufferInBytes),format.getFormat(),bufferLength);

            AudioSystem.write(out, AudioFileFormat.Type.WAVE, new File(filename));//writing the out buffer to a file

            crowdIterations++;//don't want weird overlapping samples
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


    public String getFiles()//list samples to display in ui
    {
        String str = "";

        for(File f: samples)
            str += f.toString() + "\n";

        return str;
    }
}