import javax.swing.*;
import java.awt.*;

public class UI{

    private JFrame frame = new JFrame();
    private InfoPanel infoPanel = new InfoPanel();
    private static final int frameWidth = 720;
    private static final int frameHeight = 480;
    private MixingBuffer mixer = new MixingBuffer();
    private ImageIcon waveform;

    public UI()
    {
        waveform = new ImageIcon("resources/images/waveform.JPG");
        //maybe can use praat to generate a proper waveform each time to display

        Slider numSamplesSlider = new Slider("Number of Samples", 5,50,5);
        Slider duration = new Slider("Duration", 5,30,5);
        Slider mfRatio = new Slider("Male/Female Ratio", 0,100,10);

        frame.setSize(frameWidth,frameHeight);
        frame.setTitle("Synthesising the Sound of Crowds");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        //general setup of the overall ui

        infoPanel.setSize(420,150);
        //info panel to show what's going on


        JButton generate = new JButton("Generate Crowd");
        generate.setSize(150,150);
        generate.addActionListener(e -> {
            infoPanel.displayString("Generating crowd...\n");
            mixer.synthesise(numSamplesSlider.getValue(), duration.getValue(), mfRatio.getValue());
//            System.out.println(numSamplesSlider.getValue() + " samples");
//            System.out.println(duration.getValue() + " seconds");
            infoPanel.displayString(mixer.getFiles());
            infoPanel.displayString("Crowd Generated");
        });
        //button used to generate crowds and push relevant info to display

        JButton playButton = new JButton("Play");
        playButton.setSize(150,150);
        playButton.addActionListener(e -> {
            infoPanel.displayString("Now playing: " + mixer.getFilename());
            mixer.play();
        });
        //play button for our new crowd sound

        frame.add(generate, BorderLayout.WEST);
        frame.add(playButton, BorderLayout.EAST);
        frame.add(infoPanel, BorderLayout.CENTER);
        frame.add(new JLabel(waveform), BorderLayout.NORTH);

        JPanel parameterisation = new JPanel(new GridLayout(2, 1));
        parameterisation.add(duration);
        parameterisation.add(numSamplesSlider);
        parameterisation.add(mfRatio);
        parameterisation.setSize(720,150);
        frame.add(parameterisation, BorderLayout.SOUTH);
        //adding sliders to parameterise crowd

        frame.setVisible(true);

        //(poorly) arranging things in the overall ui panel
    }

}
