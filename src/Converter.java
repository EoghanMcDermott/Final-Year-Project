import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class Converter {

    public int[] convertToInt(File f)
    {
        try
        {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);

            byte[] bytes = new byte[(int) f.length()];

            while(audioIn.read(bytes) != -1);
            //read to byte array

            int[] output = new int[(int) f.length()/2];//need half as many int values as bytes

            for(int i=0;i<output.length-2;i+=2)
            {
                output[i] = bytes[i] + bytes[i+1];//one int value represents two bytes i.e. 1 16 bit audio chunk
            }

            return output;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;//error converting file
    }

    public byte[] convertToByte(int[] input)
    {
        try
        {
            //convert back to byte[]
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
