import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Converter {

    private static short byteToShortLittleEndian(final byte[] buf, final int offset)
    {
        int sample = (buf[offset] & 0xff) + ((buf[offset+1] & 0xff) << 8);
        return (short)sample;
    }

    private static byte[] shortToByteLittleEndian(final short[] samples, final int offset)
    {
        byte[] buf = new byte[2];
        int sample = samples[offset];
        buf[0] = (byte) (sample & 0xFF);
        buf[1] = (byte) ((sample >> 8) & 0xFF);
        return buf;
    }

    public short[] toShortArray(File f)
    {
        byte[] byteArray = toByteArray(f);

        short[] samples = new short[byteArray.length/2];

        for (int i = 0; i < samples.length; i++) {
            samples[i] = byteToShortLittleEndian(byteArray, i * 2);
        }

        return samples;
    }

    public byte[] shortToByteArray(short[] input)
    {
        byte[] output = new byte[input.length*2];

        for (int i=0; i<input.length; i++) {
            byte[] b = shortToByteLittleEndian(input, i);
            output[2*i] = b[0];
            output[2*i+1] = b[1];
        }

        return output;
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
}
