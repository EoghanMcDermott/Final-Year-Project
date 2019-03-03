import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class WAVConverter {

    public int[] toIntArray(File f)
    {
        try
        {
           byte[] byteArray = toByteArray(f);//first get byte representation of file

           int[] intArray = new int[byteArray.length];

           for(int i=0;i<byteArray.length/2;i+=2)
               intArray[i] = byteArray[i]+byteArray[i+1];
           //sample is 16-bit pcm so need one int made up of two bytes for each frame of sample

           return intArray;//return array of int - each index is 1 frame of audio
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;//error converting file
    }

    public byte[] toByteArray(File file)
    {
        try
        {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);

            byte[] byteArray = new byte[(int) file.length()];//make sure the size is correct

            while (in.read(byteArray) != -1) ;//read in byte by byte until end of audio input stream reached

            return byteArray;//return the new byte array
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;//encountered a problem with converting the file to byte array
    }

    public Clip toClip(File file)
    {
        try
        {
            Clip clip = AudioSystem.getClip();

            clip.open(AudioSystem.getAudioInputStream(file));
            //get clip object representing a given file

            return clip;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;//error converting file
    }

}
