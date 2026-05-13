package org.project.businessunit.presentation;

import org.project.businessunit.core.Datamart;
import org.project.businessunit.model.Flight;
import org.project.businessunit.model.Match;
import org.project.businessunit.model.RecommendedTrip;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class View extends JFrame {
    private final Datamart datamart;
    private JComboBox<String> dateSelector;
    private JTable matchTable;
    private JTable flightTable;
    private DefaultTableModel matchModel;
    private DefaultTableModel flightModel;

    public View(Datamart datamart) {
        this.datamart = datamart;
        initUI();
    }

    private void initUI() {
        setTitle("Business Unit - Gestor de Viajes y Fútbol");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(45, 52, 54));
        JLabel label = new JLabel("Selecciona una fecha de partido: ");
        label.setForeground(Color.WHITE);
        topPanel.add(label);

        dateSelector = new JComboBox<>();
        updateDates();
        dateSelector.addActionListener(e -> updateMatches());
        topPanel.add(dateSelector);

        JButton refreshBtn = new JButton("Refrescar");
        refreshBtn.addActionListener(e -> { updateDates(); updateMatches(); });
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        matchModel = new DefaultTableModel(new String[]{"Local", "Visitante", "Competición", "Estado", "Aeropuerto"}, 0);
        matchTable = new JTable(matchModel);
        matchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showFlightsForSelectedMatch();
            }
        );

        flightModel = new DefaultTableModel(new String[]{"Vuelo", "Aerolínea", "Origen", "Destino", "Tipo", "Salida", "Llegada", "Estado"}, 0);
        flightTable = new JTable(flightModel);
        tablesPanel.add(new JScrollPane(matchTable));
        tablesPanel.add(new JScrollPane(flightTable));

        add(tablesPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    private void updateDates() {
        String current = (String) dateSelector.getSelectedItem();
        dateSelector.removeAllItems();
        for (String date : datamart.getAvailableDates()) {
            dateSelector.addItem(date);
        }
        if (current != null) dateSelector.setSelectedItem(current);
    }

    private void updateMatches() {
        matchModel.setRowCount(0);
        flightModel.setRowCount(0);
        String selectedDate = (String) dateSelector.getSelectedItem();
        if (selectedDate == null) return;

        List<Match> matches = datamart.getMatches(selectedDate);
        for (Match m : matches) {
            matchModel.addRow(new Object[]{
                    m.localTeam(), m.visitorTeam(), m.matchStatus(), m.competition(), m.airportCode()
            });
        }
    }

    private void showFlightsForSelectedMatch() {
        flightModel.setRowCount(0);
        int row = matchTable.getSelectedRow();
        if (row == -1) return;

        String local = (String) matchModel.getValueAt(row, 0);
        String visitor = (String) matchModel.getValueAt(row, 1);
        String selectedDate = (String) dateSelector.getSelectedItem();

        Match selectedMatch = datamart.getMatches(selectedDate).stream()
                .filter(m -> m.localTeam().equals(local) && m.visitorTeam().equals(visitor))
                .findFirst()
                .orElse(null);

        if (selectedMatch == null) return;

        RecommendedTrip trip = datamart.getTrip(selectedMatch);

        if (trip == null || (trip.getDepartureFlights().isEmpty() && trip.getReturnFlights().isEmpty())) {
            System.out.println("Aún no hay vuelos vinculados para este viaje.");
            return;
        }

        for (Flight f : trip.getDepartureFlights()) {
            flightModel.addRow(new Object[]{
                    f.flightNumber(), f.airline(), f.origin(), f.destination(),
                    "IDA", f.departureTime(), f.arrivalTime(), f.status()
            });
        }
        for (Flight f : trip.getReturnFlights()) {
            flightModel.addRow(new Object[]{
                    f.flightNumber(), f.airline(), f.origin(), f.destination(),
                    "VUELTA", f.departureTime(), f.arrivalTime(), f.status()
            });
        }
    }
}
