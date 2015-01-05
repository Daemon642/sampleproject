package nassoleng.oracle;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class OPCMain {
    public OPCMain() {
        JFrame guiFrame = new JFrame();
                
        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Example GUI");
        guiFrame.setSize(300,250);
              
        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);
                
        //Options for the JComboBox 
        String[] taskOptions = {"Delete JCS", "Delete All Containers"};
                
        //The first JPanel contains a JLabel and JCombobox
        final JPanel comboPanel = new JPanel();
        JLabel comboLbl = new JLabel("Task:");
        JComboBox opcTasks = new JComboBox(taskOptions);
             
        opcTasks.setSelectedIndex(0);
        opcTasks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox jcmbType = (JComboBox) e.getSource();
                String selectedItem = (String) jcmbType.getSelectedItem();
                System.out.println ("\nSelected Item = " + selectedItem);
            }
        });
                        
        comboPanel.add(comboLbl);
        comboPanel.add(opcTasks);
                
        JButton goButtton = new JButton( "GO");
                
        //The ActionListener class is used to handle the
        //event that happens when the user clicks the button.
        //As there is not a lot that needs to happen we can 
        //define an anonymous inner class to make the code simpler.
        goButtton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
            }
        });
                
        //The JFrame uses the BorderLayout layout manager.
        //Put the two JPanels and JButton in different areas.
        guiFrame.add(comboPanel, BorderLayout.NORTH);
        guiFrame.add(goButtton,BorderLayout.SOUTH);

        guiFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new OPCMain();
    }
}
