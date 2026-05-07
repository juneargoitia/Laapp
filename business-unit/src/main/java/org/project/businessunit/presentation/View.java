package org.project.businessunit.presentation;

import org.project.businessunit.core.Datamart;
import org.project.businessunit.model.Flight;
import org.project.businessunit.model.Match;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.Color;
import java.time.DayOfWeek;

public class View extends JFrame {
    private final Datamart datamart;
    private JComboBox<String> dateSelector;
    private JTable matchTable;
    private JTable flightTable;
    private DefaultTableModel matchModel;
    private DefaultTableModel flightModel;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        topPanel.setBackground(new Color(45, 52, 54));
        JLabel label = new JLabel("Selecciona una fecha: ");
        topPanel.add(new JLabel("Selecciona una fecha: "));
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

        String airportCode = (String) matchModel.getValueAt(row, 4);
        String matchDateStr = (String) dateSelector.getSelectedItem();

        if (airportCode == null || airportCode.equals("N/A")) {
            JOptionPane.showMessageDialog(this, "No hay aeropuerto asignado para este equipo.");
            return;
        }

        List<Flight> allFlights = datamart.getFlights(airportCode);
        LocalDate matchDate = LocalDate.parse(matchDateStr, formatter);

        LocalDate dayIda;
        LocalDate dayVuelta;

        if (matchDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            dayIda = matchDate.minusDays(1);
            dayVuelta = matchDate.plusDays(1);
        } else {
            dayIda = matchDate;
            while (dayIda.getDayOfWeek() != DayOfWeek.MONDAY) {
                dayIda = dayIda.minusDays(1);
            }
            dayVuelta = matchDate;
            while (dayVuelta.getDayOfWeek() != DayOfWeek.THURSDAY) {
                dayVuelta = dayVuelta.plusDays(1);
            }
        }
        for (Flight f : allFlights) {
            String flightDateStr = f.departureTime().substring(0, 10);
            LocalDate flightDate = LocalDate.parse(flightDateStr, formatter);

            boolean isIda = f.origin().equals("MAD") && f.destination().equals(airportCode)
                    && flightDate.equals(dayIda);

            boolean isVuelta = f.origin().equals(airportCode) && f.destination().equals("MAD")
                    && flightDate.equals(dayVuelta);

            if (isIda || isVuelta) {
                String etiqueta = isIda ? "IDA (" + dayIda.getDayOfWeek() + ")" : "VUELTA (" + dayVuelta.getDayOfWeek() + ")";
                flightModel.addRow(new Object[]{
                        f.flightNumber(),
                        f.airline(),
                        f.origin(),
                        f.destination(),
                        etiqueta,
                        f.departureTime(),
                        f.status()
                });
            }
        }

        if (flightModel.getRowCount() == 0) {
            System.out.println("No se encontraron vuelos en el Datamart para " + airportCode + " en esas fechas.");
        }
    }
}
