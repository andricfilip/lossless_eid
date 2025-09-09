package com.andric.gui;


import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * Simple JPanel with BufferedImage
 * 
 * @author Goran Rakic (grakic@devbase.net)
 */
public class JImagePanel extends JPanel {

    private static final long serialVersionUID = 2272776565547958916L;
    private Image image = null;

    public JImagePanel() {

    }
    
    public JImagePanel(final Image image) {
        this.image = image;
    }
    
    public void setImage(final Image image) {
        this.image = image;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image != null) {
            g.drawImage(image, (this.getWidth()-image.getWidth(null))/2, (this.getHeight()-image.getHeight(null))/2, this);
        }
    }
}
