import javax.swing.*;
import java.awt.*;

//VERY RUDIMENTARY FOR NOW

public class UI{

    private JFrame frame = new JFrame();
    private InfoPanel infoPanel = new InfoPanel();
    private static final int frameWidth = 720;
    private static final int frameHeight = 480;
    private MixingBuffer mixer = new MixingBuffer();
    private ImageIcon waveform;

    public UI()
    {
        Slider numSamplesSlider = new Slider();

        waveform = new ImageIcon("resources/images/waveform.JPG");
        //maybe can use praat to generate a proper waveform each time to display

        frame.setSize(frameWidth,frameHeight);
        frame.setTitle("Synthesising the Sound of Crowds");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        //general setup of the overall ui

        infoPanel.setSize(420,250);
        infoPanel.setVisible(true);
        //info panel to show what's going on


        JButton generate = new JButton("Generate Crowd");
        generate.setSize(150,150);
        generate.setVisible(true);
        generate.addActionListener(e -> {
            infoPanel.displayString("Generating crowd...\n");
            mixer.synthesise(numSamplesSlider.getValue());
            infoPanel.displayString(mixer.getFiles());
            infoPanel.displayString("Crowd Generated");
        });
        //button used to generate crowds and push relevant info to display

        JButton playButton = new JButton("Play");
        playButton.setSize(150,150);
        playButton.setVisible(true);
        playButton.addActionListener(e -> {
            infoPanel.displayString("Now playing: " + mixer.getFilename());
            mixer.play("crowd.wav");
        });
        //play button for our new crowd sound

        JButton placeholder = new JButton("Parameterisation Stuff");
        placeholder.setSize(420,150);
        placeholder.setVisible(true);


        frame.add(numSamplesSlider, BorderLayout.SOUTH);

        frame.add(generate, BorderLayout.WEST);
        frame.add(playButton, BorderLayout.EAST);
        frame.add(infoPanel, BorderLayout.CENTER);

       // frame.add(placeholder, BorderLayout.SOUTH);
        frame.add(new JLabel(waveform), BorderLayout.NORTH);
        //(poorly) arranging things in the overall ui panel
        return;
    }

}
