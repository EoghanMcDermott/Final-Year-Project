import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

public class MixingBuffer {//placeholder class name for now

    private ArrayList<File> files = new ArrayList<>();//instantiate list so can add to it

    public MixingBuffer(File... audioIn) {//variable arguments to add audio files

        for (File f : audioIn)
            files.add(f);
    }

    public void add(File f) {//option to add audio files later
        files.add(f);
    }

    public String filesToString() {
        return files.toString();

    }

    private byte[] toByteArray(File file){
        try{
            AudioInputStream in = AudioSystem.getAudioInputStream(file);

            byte[] byteArray = new byte[in.available()];//make sure the size is correct

            while(in.read(byteArray) != -1);//read in byte by byte until end of buffer reached

            return byteArray;//return the new byte array
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;//encountered a problem with converting the file to byte array
    }

    private int findLongest(ArrayList<byte[]> arrays){//finding the length of longest byte array
        int largest=0;

        for(byte[] b: arrays)//check each array
        {
            if(b.length > largest)
                largest = b.length;
        }

        return largest;//return the length of the longest array - not the byte array itself
    }

    public void synthesise() {//returns the synthesised crowd
        try{


            byte[] buffer = new byte[6400000];

            ArrayList<byte[]> byteArrays = new ArrayList<>();

            for(File f: files)
                byteArrays.add(toByteArray(f));//now have a list of the files in byte array form

            for(int i=0;i<findLongest(byteArrays);i++)//loop the length of the longest byte array
            {
                byte temp = 0;

                for(byte[] b: byteArrays)
                    temp += b[i];

                buffer[i] = temp;
            }

            AudioInputStream out = new AudioInputStream(new ByteArrayInputStream(buffer),AudioSystem.getAudioFileFormat(files.get(0)),);



//
//           for(int i=0;i<streams.length;i++)
//               streams[i] = AudioSystem.getAudioInputStream(samples.get(i));
//            //have an audio input stream for each sample audio file
//
//
//            AudioFormat format = streams[0].getFormat();
//            //16 bits so need 2 bytes per sample, with 4 bytes per frame
//           // format.
//            System.out.println(format.toString());
//
//            byte[][] streamBytes = new byte[streams.length][];//byte array for each corresponding audio stream
//
//            streams[1].read();
//            //now need to read each stream byte by byte, add the bytes together (?) - then write to crowds file
//
//            //outer for loop - number of frames?
//            for(int i=0;i<streams.length;i++)
//            {
//                //read a byte from stream and save it in associated bytes array
//            }

            //add each byte together - watch for overflow - increase buffer size?

            //could i somehow involve hash maps here?
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return;
    }
}