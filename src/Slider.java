import javax.swing.*;
import java.awt.*;

public class Slider extends JPanel{

    private JSlider sliderBar = new JSlider();

    public Slider(String name, int increment)
    {
        int MIN = increment/10;
        int MAX = increment;
        int DEFAULT = increment/2;
        //adjust values to fit as appropriate

        sliderBar = new JSlider(MIN,MAX,DEFAULT);
        setLayout(new BorderLayout());

        JLabel sliderLabel = new JLabel(name, JLabel.CENTER);

        sliderBar.setOrientation(JSlider.HORIZONTAL);

        sliderBar.setMinorTickSpacing(1);
        sliderBar.setMajorTickSpacing(5);
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
