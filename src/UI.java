import javax.swing.*;
import java.awt.*;

public class UI{

    private JFrame frame = new JFrame();
    private static final int frameWidth = 720;
    private static final int frameHeight = 480;
    private MixingBuffer mixer = new MixingBuffer();

    public UI()
    {
        frame.setSize(frameWidth,frameHeight);
        frame.setTitle("Synthesising the Sound of Crowds");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        JButton generate = new JButton("Generate\nCrowd");
        generate.setSize(150,150);
        generate.addActionListener(e -> mixer.generateOld());

        JButton playButton = new JButton("Play");
        playButton.setSize(150,150);
        playButton.addActionListener(e -> mixer.play("old_crowd.wav"));

        frame.add(generate, BorderLayout.WEST);
        frame.add(playButton, BorderLayout.EAST);

        return;
    }

}
