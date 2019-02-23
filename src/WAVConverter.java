import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class WAVConverter {//converts 16bit pcm wav file to int array

    public int[] convert(File f)//convert a wav array to an int array
    {
        try
        {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);

            int lengthInBytes = audioIn.available();//need some sort of bound for our output array

            int[] output = new int[lengthInBytes];

            int half1, half2;//16bit int made up of 2 bytes

            int count = 0;

            while(audioIn.available() != -1)
            {
                half1 = audioIn.read();

                if(audioIn.available() != -1)
                {
                    half2 = audioIn.read();

                    output[count] = half1 + half2;//combining two halves of 16bit integer
                }
                else
                {
                    output[count] = half1;
                }

                return output;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;//error with the conversion so return nothing
    }
}
