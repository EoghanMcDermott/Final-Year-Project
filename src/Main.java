public class Main {

    public static void main(String args[]){

       MixingBuffer mix = new MixingBuffer();//instantiate mixing buffer with(out) audio files

       //no overflow issue with old files
        mix.generateOld();
        mix.play("old_crowd.wav");

        //horrific static when 3rd sample is added to mix - possibly caused by overflow?
//        mix.generateMarcus();
//        mix.play("marcus_crowd.wav");
//
//        mix.generateRod();
//        mix.play("rod_crowd.wav");


    }
}
