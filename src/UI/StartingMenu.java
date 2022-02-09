package UI;

import dataPrep.DataPrep;
import logic.Parallel;
import logic.Sequential;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Objects;

public class StartingMenu extends JFrame {
    public StartingMenu(){

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(new EmptyBorder(new Insets(150, 200, 150, 200)));

        //restrict text fields to positive longs
        NumberFormatter numberFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
        numberFormatter.setValueClass(Long.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(0L);


        JLabel label = new JLabel("Select settings and click run!");
        JCheckBox checkBox = new JCheckBox("Show map?");

        JLabel accSitesLabel = new JLabel("Enter number of accumulation sites:");
        JFormattedTextField accSites = new JFormattedTextField(numberFormatter);
        JLabel nClustersLabel = new JLabel("Enter number of clusters:");
        JFormattedTextField nClusters = new JFormattedTextField(numberFormatter);
        JLabel iterationsLabel = new JLabel("Enter number of iterations:");
        JFormattedTextField iterations = new JFormattedTextField(numberFormatter);
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Sequential", "Parallel"});
        JButton runButton = new JButton("Run!");

        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        checkBox.setAlignmentX(Checkbox.CENTER_ALIGNMENT);

        accSitesLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        accSites.setAlignmentX(JFormattedTextField.CENTER_ALIGNMENT);
        accSites.setHorizontalAlignment(JFormattedTextField.CENTER);

        nClustersLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        nClusters.setAlignmentX(JFormattedTextField.CENTER_ALIGNMENT);
        nClusters.setHorizontalAlignment(JFormattedTextField.CENTER);

        iterationsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        iterations.setAlignmentX(JFormattedTextField.CENTER_ALIGNMENT);
        iterations.setHorizontalAlignment(JFormattedTextField.CENTER);

        comboBox.setAlignmentX(JComboBox.CENTER_ALIGNMENT);
        runButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

        //default numbers
        accSites.setText("11093");
        nClusters.setText("50");
        iterations.setText("40");
        //checkBox.doClick();

        panel.add(label);
        panel.add(checkBox);
        panel.add(accSitesLabel);
        panel.add(accSites);
        panel.add(nClustersLabel);
        panel.add(nClusters);
        panel.add(iterationsLabel);
        panel.add(iterations);
        panel.add(comboBox);
        panel.add(runButton);

        this.add(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);


        runButton.addActionListener(e -> {
            if (Objects.equals(comboBox.getSelectedItem(), "Sequential")){
                long initTime = System.currentTimeMillis();
                Sequential sequential = new Sequential(
                        new DataPrep(Integer.parseInt(accSites.getText().replaceAll("\\.", ""))).accSites,
                        Integer.parseInt(accSites.getText().replaceAll("\\.", "")),
                        Integer.parseInt(nClusters.getText().replaceAll("\\.", "")),
                        Integer.parseInt(iterations.getText().replaceAll("\\.", "")));
                long computationTime = System.currentTimeMillis()-initTime;
                System.out.println("Time to complete computation: " + computationTime + " ms");
                if (checkBox.isSelected()){
                    new MapWindow(sequential);
                    StartingMenu.super.setVisible(false);
                    System.out.println("Time to draw graphics: " + (System.currentTimeMillis() - initTime - computationTime) + " ms");
                }
            }else if (Objects.equals(comboBox.getSelectedItem(), "Parallel")){
                long initTime = System.currentTimeMillis();
                Parallel parallel = new Parallel(
                        new DataPrep(Integer.parseInt(accSites.getText().replaceAll("\\.", ""))).accSites,
                        Integer.parseInt(accSites.getText().replaceAll("\\.", "")),
                        Integer.parseInt(nClusters.getText().replaceAll("\\.", "")),
                        Integer.parseInt(iterations.getText().replaceAll("\\.", "")));
                long computationTime = System.currentTimeMillis()-initTime;
                System.out.println("Time to complete computation: " + computationTime + " ms");
                if (checkBox.isSelected()){
                    new MapWindow(parallel);
                    StartingMenu.super.setVisible(false);
                    System.out.println("Time to draw graphics: " + (System.currentTimeMillis() - initTime - computationTime) + " ms");
                }
            }

        });

    }
}
