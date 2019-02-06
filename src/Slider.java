import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Slider extends JPanel{

    private static final int MIN_SAMPLES = 0;
    private static final int MAX_SAMPLES = 50;
    private static final int DEFAULT_SAMPLES = 25;

    private JSlider sliderBar = new JSlider(MIN_SAMPLES, MAX_SAMPLES, DEFAULT_SAMPLES);

    public Slider()
    {
        setLayout(new BorderLayout());

        setSize(420,150);

        JLabel sliderLabel = new JLabel("Num Samples", JLabel.CENTER);

        sliderBar.setOrientation(JSlider.HORIZONTAL);

        sliderBar.setMinorTickSpacing(1);
        sliderBar.setMajorTickSpacing(10);
        sliderBar.setPaintTicks(true);

        //sliderBar.createStandardLabels(5,10);
       // sliderBar.setPaintLabels(true);

        add(sliderLabel, BorderLayout.NORTH);
        add(sliderBar, BorderLayout.SOUTH);
    }

    public int getValue() {
        return sliderBar.getValue();
    }
}
