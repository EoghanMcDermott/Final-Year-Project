import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class Converter {//used to convert .WAV files to different primitive data types

    private static short byteToShortLittleEndian(final byte[] buf, final int offset)
    //take two bytes and convert to a single sample (represented by a short value)
    {
        int sample = (buf[offset] & 0xff) + ((buf[offset+1] & 0xff) << 8);
        return (short)sample;
    }

    private static byte[] shortToByteLittleEndian(final short[] samples, final int offset)
    //break a sample (16-bit short) into its 2 corresponding byte values
    {
        byte[] buf = new byte[2];
        int sample = samples[offset];
        buf[0] = (byte) (sample & 0xFF);
        buf[1] = (byte) ((sample >> 8) & 0xFF);
        return buf;
    }

    public short[] toShortArray(File f)
    //convert a file into a short array with each value representing 1 sample frame
    {
        byte[] byteArray = toByteArray(f);//first get byte representation of file

        short[] samples = new short[byteArray.length/2];
        //short array half as long - 2 bytes per short value

        for (int i = 0; i < samples.length; i++)
            samples[i] = byteToShortLittleEndian(byteArray, i * 2);
        //take 2 bytes at a time, convert to short value and add to array

        return samples;
    }

    public byte[] shortToByteArray(short[] input)
    //convert back from short (sample representation) to byte array for file writing
    {
        byte[] output = new byte[input.length*2];//2 bytes per sample (short value)

        for (int i=0; i<input.length; i++) {
            byte[] b = shortToByteLittleEndian(input, i);//convert short value
            output[2*i] = b[0];
            output[2*i+1] = b[1];//correctly add split up sample value to array
        }

        return output;
    }


    public byte[] toByteArray(File file)//convert .WAV file to byte representation
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

    public byte[] intToByteArray(int[] input)//will use to convert buffer to something writable with sound api
    {
        short[] scaled = intToShortArray(input);

        return shortToByteArray(scaled);
    }


    public short[] intToShortArray(int[] input)//scale int value to fit in short - avoiding overflow
    {
        short[] output = new short[input.length];

        double max = (double) findMax(input);//find max in original int array

        double scaleFactor = (max - Short.MAX_VALUE)/max;//use that max value to scale other values in int array

        for(int i=0;i<input.length;i++)
        {
            output[i] = (short) (input[i]*scaleFactor);//cast scaled values to short as they are within range short can hold
        }

        return output;
    }

    private int findMax(int[] array)//simple method to find largest value in array
    {
        int max = 0;

        for(int i: array)
        {
            if(i>max)
                max = i;
        }

        return max;
    }
}
