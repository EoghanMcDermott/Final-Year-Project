import javax.swing.*;
import java.awt.*;

public class Slider extends JPanel{

    private JSlider sliderBar = new JSlider();

    public Slider(String name, int increment)
    {
        int MIN = increment/10;
        int MAX = increment;
        int DEFAULT = increment/2;

        sliderBar = new JSlider(MIN,MAX,DEFAULT);
        setLayout(new BorderLayout());

        setSize(420,150);

        JLabel sliderLabel = new JLabel(name, JLabel.CENTER);

        sliderBar.setOrientation(JSlider.HORIZONTAL);

        sliderBar.setMinorTickSpacing(1);
        sliderBar.setMajorTickSpacing(5);
        sliderBar.setPaintTicks(true);

        //sliderBar.createStandardLabels(5,10);
        sliderBar.setPaintLabels(true);

        add(sliderLabel, BorderLayout.NORTH);
        add(sliderBar, BorderLayout.SOUTH);
    }

    public int getValue() {
        return sliderBar.getValue();
    }
}
