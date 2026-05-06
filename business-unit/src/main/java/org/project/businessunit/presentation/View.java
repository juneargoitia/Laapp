package org.project.businessunit.presentation;

import org.project.businessunit.core.Datamart;
import org.project.businessunit.model.Flight;
import org.project.businessunit.model.Match;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Selecciona una fecha: "));

        dateSelector = new JComboBox<>();
        updateDates(); // Llenar con las fechas del Datamart
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
            if (!e.getValueIsAdjusting() && matchTable.getSelectedRow() != -1) {
                showFlightsForSelectedMatch();
            }
        });
        tablesPanel.add(new JScrollPane(matchTable));

        flightModel = new DefaultTableModel(new String[]{"Vuelo", "Aerolínea", "Origen", "Destino", "Salida", "Llegada", "Estado"}, 0);
        flightTable = new JTable(flightModel);
        tablesPanel.add(new JScrollPane(flightTable));

        add(tablesPanel, BorderLayout.CENTER);
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
        String airportCode = (String) matchModel.getValueAt(row, 4);

        List<Flight> flights = datamart.getFlights(airportCode);
        for (Flight f : flights) {
            flightModel.addRow(new Object[]{
                    f.flightNumber(), f.airline(), f.origin(), f.destination(),
                    f.departureTime(), f.arrivalTime(), f.status()
            });
        }
    }
}