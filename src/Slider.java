import javax.swing.*;
import java.awt.*;

public class Slider extends JPanel{

    private JSlider sliderBar;

    public Slider(String name, int min, int max, int increment)
    {
        sliderBar = new JSlider(min,max);
        setLayout(new BorderLayout());

        JLabel sliderLabel = new JLabel(name, JLabel.CENTER);

        sliderBar.setOrientation(JSlider.HORIZONTAL);

        sliderBar.setMinorTickSpacing(1);
        sliderBar.setMajorTickSpacing(increment);
        sliderBar.setPaintTicks(true);
        //dealing with border ticks

        sliderBar.setPaintLabels(true);
        //adding numeric values instead of just ticks

        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        add(sliderLabel, BorderLayout.NORTH);
        add(sliderBar, BorderLayout.SOUTH);
        //display title of slider then actual slider underneath
    }

    public int getValue() {
        return sliderBar.getValue();
    }
}
