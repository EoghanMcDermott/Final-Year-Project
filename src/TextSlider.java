import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class TextSlider extends JPanel //slider but with text labels rather than numbers
{
    private JSlider sliderBar;

    public TextSlider(String name, String left, String right)
    {
        sliderBar = new JSlider();//using jslider swing object
        setLayout(new BorderLayout());

        JLabel sliderLabel = new JLabel(name, JLabel.CENTER);

        sliderBar.setOrientation(JSlider.HORIZONTAL);

        sliderBar.setMinorTickSpacing(1);
        sliderBar.setMajorTickSpacing(10);
        sliderBar.setPaintTicks(true);
        //dealing with border ticks

        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 1 ), new JLabel(left));
        labelTable.put( new Integer( 100 ), new JLabel(right));
        //using hash table to store the different text labels for the slider

        sliderBar.setLabelTable(labelTable);
        sliderBar.setPaintLabels(true);
        //adding text values

        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        add(sliderLabel, BorderLayout.NORTH);
        add(sliderBar, BorderLayout.SOUTH);
        //display title of slider then actual slider underneath
    }

    public int getValue()//used to pass values from ui
    {
        return sliderBar.getValue();
    }
}
