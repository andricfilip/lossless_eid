package com.andric;

import com.itextpdf.text.DocumentException;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Image;

import static com.andric.sample.Lossless_eid.pickTerminal;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Kreiranje i inicijalizacija TerminalFactory-a i čitanje kartice
        try {
            // Prvo, pronađi i odaberi terminal (uređaj za čitanje kartica)
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                throw new CardException("No card terminals found.");
            }

            // Odaberite terminal
            CardTerminal terminal = pickTerminal(terminals);
            System.out.println("Using reader: " + terminal);

            // Usmeravanje prema kartici
            Card card = terminal.connect("*");
            EidCard eidcard = EidCard.fromCard(card);

            // Čitanje podataka sa kartice
            EidInfo info = eidcard.readEidInfo();
            Image photo = eidcard.readEidPhoto();

            // Traži korisniku da odabere lokaciju i ime PDF fajla
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("report_" + info.getPersonalNumber() + ".pdf"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF", "pdf");
            fc.setFileFilter(filter);
            int returnVal = fc.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Generiši PDF sa podacima
                String filename = fc.getSelectedFile().toString();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }

                // Kreiraj PDF iz podataka sa kartice
                PdfReport report = new PdfReport(info, photo);
                report.write(filename);

                JOptionPane.showMessageDialog(null, "PDF saved successfully at: " + filename);
            } else {
                System.out.println("File saving cancelled.");
            }

        } catch (CardException e) {
            JOptionPane.showMessageDialog(null, "Card error: " + e.getMessage(), "Card Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IO error: " + e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "PDF creation error: " + e.getMessage(), "PDF Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Funkcija za izbor terminala iz liste dostupnih terminala
    private static CardTerminal pickTerminal(List<CardTerminal> terminals) {
        // Ako postoji samo jedan terminal, automatski ga izaberemo
        if (terminals.size() == 1) {
            return terminals.get(0);
        }

        // Ako ima više terminala, prikazujemo korisniku izbor
        StringBuilder options = new StringBuilder("Available Terminals:\n");
        for (int i = 0; i < terminals.size(); i++) {
            options.append(i + 1).append(") ").append(terminals.get(i).getName()).append("\n");
        }
        String selectedTerminal = JOptionPane.showInputDialog(options.toString() + "\nSelect a terminal by number:");

        int index = Integer.parseInt(selectedTerminal) - 1;
        if (index < 0 || index >= terminals.size()) {
            throw new IllegalArgumentException("Invalid terminal selection.");
        }

        return terminals.get(index);
    }
}