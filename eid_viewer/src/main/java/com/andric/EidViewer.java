package com.andric;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;


import com.andric.gui.GUIPanel;
import com.formdev.flatlaf.FlatDarkLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;


/**
 * EidViewer is a singleton class behind EidViewer application
 *
 * @author Goran Rakic (grakic@devbase.net)
 */
@SuppressWarnings("restriction")  // Access to restricted card APIs
public class EidViewer extends JPanel implements Reader.ReaderListener {

    private static final long serialVersionUID = -2497143822816312498L;

    private static final String[] ICON_FILES = {
            "eidviewer20.png", "eidviewer26.png", "eidviewer32.png"};

    private static final String ICON_RESOURCE =
            "smart-card-reader2.jpg";

    private final static Logger logger = LoggerFactory.getLogger(EidViewer.class);

    private static final ResourceBundle bundle = ResourceBundle.getBundle(
            "viewer");

    private EidInfo info;
    private Image photo;

    private JFrame frame;
    private GUIPanel details;
    private JButton saveButton;
    private JButton scriptButton;

    private static EidViewer instance;

    public EidViewer() {
        setSize(new Dimension(750, 350));
        setLayout(new CardLayout(0, 0));

        /* Create "insert card" splash screen */
        JPanel splash = new JPanel();
        splash.setBackground(Color.WHITE);
        splash.setLayout(new GridBagLayout());
        ImageIcon insertCardIcon = new ImageIcon(getClass().getResource("/"+ICON_RESOURCE));
        JLabel label = getLabel(insertCardIcon);
        splash.add(label, new GridBagConstraints());

        add(splash, "splash");

        /* Add card details screen */
        details = new EidViewerPanel();
        add(details, "details");
    }

    private JLabel getLabel(ImageIcon insertCardIcon) {
        JLabel label = new JLabel(
                bundle.getString("InsertCard"), insertCardIcon, SwingConstants.CENTER);
        Font labelFont = label.getFont();
        label.setFont(
                labelFont.deriveFont(labelFont.getStyle() | Font.BOLD,
                        labelFont.getSize() + 4f));
        return label;
    }

    public static EidViewer getInstance()
    {
        if(instance == null) {
            instance = new EidViewer();
        }
        return instance;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Return JVM version
     */
    private static double getVersion () {
        String version = System.getProperty("java.version");
        int pos = 0, count = 0;
        for ( ; pos<version.length() && count < 2; pos ++) {
            if (version.charAt(pos) == '.') count ++;
        }
        return Double.parseDouble(version.substring(0, pos-1));
    }

    /**
     * Create the GUI and show it.
     */
    private static void createAndShowGUI() {
    // Enable font anti aliasing
    System.setProperty("awt.useSystemAAFontSettings","on");
    System.setProperty("swing.aatext", "true");

    // Set sr_RS locale as default
    Locale.setDefault(new Locale("sr", "RS"));

    // 🌟 Modern look & feel (FlatLaf)
    try {
        // Možeš ovde da biraš temu:
        // UIManager.setLookAndFeel(new FlatLightLaf()); // svetla
        UIManager.setLookAndFeel(new FlatDarkLaf());   // tamna
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Create and set up the window
    JFrame frame = new JFrame(bundle.getString("lossless_title"));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);
    frame.setMinimumSize(new Dimension(750, 400));
    UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

    // Set window icon
    List<Image> icons = new ArrayList<>();
    for (String iconFile : ICON_FILES) {
        try {
            URL iconUrl = EidViewer.class.getResource("/" + iconFile);
            if (iconUrl != null) {
                icons.add(ImageIO.read(iconUrl));
            }
        } catch (IOException ignored) {}
    }
    frame.setIconImages(icons);

    // Test for Java 1.6 or newer
    if(getVersion() < 1.6) {
        JOptionPane.showMessageDialog(frame,
                bundle.getString("JavaError"),
                bundle.getString("JavaErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    // Get the list of terminals
    CardTerminal terminal = null;
    try {
        TerminalFactory factory = TerminalFactory.getDefault();
        terminal = pickTerminalGUI(frame, factory.terminals().list());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame,
                bundle.getString("ReaderError") + ": " + e.getMessage(),
                bundle.getString("ReaderErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
        logger.error("Reader error", e);
        System.exit(1);
    }

    // Create and set up the content pane
    EidViewer app = EidViewer.getInstance();
    app.setFrame(frame);

    frame.getContentPane().add(app, BorderLayout.CENTER);
    frame.pack();

    // Create reader and add GUI as the listener
    Reader reader = new Reader(terminal);
    reader.addCardListener(app);

    // Display the window
    frame.setVisible(true);
}

    public static CardTerminal pickTerminalGUI(JFrame frame, List<CardTerminal> terminals) {
        if (terminals.size() == 1) {
            return terminals.get(0);
        }

        CardTerminal terminal = (CardTerminal) JOptionPane.showInputDialog(
                frame,
                bundle.getString("SelectReader"),
                bundle.getString("SelectReaderTitle"),
                JOptionPane.PLAIN_MESSAGE,
                null,
                terminals.toArray(),
                terminals.get(0));

        // Cancel clicked
        if(terminal == null) System.exit(1);

        return terminal;
    }

    private void showCardError(Exception e) {
        String message = (e.getMessage() != null && !e.getMessage().isEmpty())
                ? e.getMessage()
                : e.getClass().getSimpleName();
        JOptionPane.showMessageDialog(this,
                bundle.getString("CardError") + ": " + message,
                bundle.getString("CardErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
        logger.error("Card error", e);
    }

    public void inserted(final EidCard card) {
        logger.info("Card inserted");
        try {
            final EidInfo readInfo = card.readEidInfo();
            final Image readPhoto = card.readEidPhoto();
            SwingUtilities.invokeLater(() -> {
                info = readInfo;
                photo = readPhoto;
                CardLayout cl = (CardLayout) this.getLayout();
                cl.show(this, "details");
                details.setDetails(info);
                details.setPhoto(photo);
                saveButton.setEnabled(true);
                updateScriptButtonText();
            });
        } catch (CardException e) {
            SwingUtilities.invokeLater(() -> showCardError(e));
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> showCardError(e));
        }
    }

    public void removed() {
        logger.info("Card removed");
        SwingUtilities.invokeLater(() -> {
            CardLayout cl = (CardLayout) this.getLayout();
            cl.show(this, "splash");

            saveButton.setEnabled(false);
            info = null;
            photo = null;
            details.clearDetailsAndPhoto();
        });
    }

    private void updateScriptButtonText() {
        if (scriptButton != null) {
            scriptButton.setText("Pismo: " + SerbianScript.modeLabel());
        }
    }

    private void refreshCurrentCardDetails() {
        if (info != null) {
            details.setDetails(info);
            details.setPhoto(photo);
        }
    }

    /** UI panel for the application */
    class EidViewerPanel extends GUIPanel {

        private static final long serialVersionUID = 1L;

        public EidViewerPanel() {
            super();
            saveButton = newSaveButton();
            scriptButton = newScriptButton();
            toolbar.add(saveButton, BorderLayout.WEST);
            toolbar.add(scriptButton, BorderLayout.EAST);
        }

        private JButton newSaveButton() {
            JButton button = new JButton(bundle.getString("SavePDF"));
            button.setEnabled(false);
            button.setPreferredSize(new Dimension(130, 36));
            button.setMargin(new Insets(5,0,5,0));
            button.setSize(new Dimension(200, 0));
            button.addActionListener(new ButtonActionListener());
            return button;
        }

        private JButton newScriptButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(150, 36));
            button.setMargin(new Insets(5, 0, 5, 0));
            button.addActionListener(new ScriptActionListener());
            updateScriptButtonText();
            return button;
        }
    }

    /** The action taken on the UI action button press. */
    private class ButtonActionListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent ev) {

            final JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("report_" + info.getNameFull() + ".pdf"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "PDF", "pdf");
            fc.setFileFilter(filter);
            int returnVal = fc.showSaveDialog(frame);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                // Append correct extension if missing
                String filename = fc.getSelectedFile().toString();
                if(!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }

                try {
                    logger.info("Saving " + filename);
                    PdfReport report = new PdfReport(info, photo);
                    report.write(filename);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame,
                            bundle.getString("SavePDFError") + ": " + e.getMessage(),
                            bundle.getString("SavePDFErrorTitle"),
                            JOptionPane.ERROR_MESSAGE);
                    logger.error("Error saving PDF file", e);
                } catch (DocumentException e) {
                    JOptionPane.showMessageDialog(frame,
                            bundle.getString("CreatePDFError") + ": " + e.getMessage(),
                            bundle.getString("CreatePDFErrorTitle"),
                            JOptionPane.ERROR_MESSAGE);
                    logger.error("Error creating PDF file", e);
                }
            }
        }
    }

    private class ScriptActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ev) {
            SerbianScript.setMode(SerbianScript.getMode().next());
            updateScriptButtonText();
            refreshCurrentCardDetails();
        }
    }
}
