package org.project.businessunit.presentation;

import org.project.businessunit.core.Datamart;
import org.project.businessunit.model.Flight;
import org.project.businessunit.model.Match;
import org.project.businessunit.model.RecommendedTrip;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.List;
import java.awt.*;
import java.util.Map;

public class View extends JFrame {
    private final Datamart datamart;
    private JComboBox<String> dateSelector;
    private JTable matchTable, departureTable, returnTable;
    private DefaultTableModel matchModel, departureModel, returnModel;
    private JLabel totalLabel;

    private double selectedDeparturePrice = 0.0;
    private double selectedReturnPrice = 0.0;

    private final Color PRIMARY_BLUE = new Color(0, 51, 102);
    private final Color ACCENT_BLUE = new Color(0, 102, 204);
    private final Color BG_WHITE = Color.WHITE;

    private final Map<String, String> cityNames = Map.ofEntries(
            Map.entry("LHR", "Heathrow (Londres)"),
            Map.entry("MUC", "Múnich"),
            Map.entry("LPL", "Liverpool"),
            Map.entry("STN", "Stansted (Londres)"),
            Map.entry("BCN", "Barcelona"),
            Map.entry("LGW", "Gatwick (Londres)"),
            Map.entry("LIS", "Lisboa"),
            Map.entry("MAN", "Mánchester"),
            Map.entry("MAD", "Madrid"),
            Map.entry("CDG", "París"),
            Map.entry("BGY", "Bérgamo / Milán"),
            Map.entry("DUS", "Düsseldorf"),
            Map.entry("IST", "Estambul"),
            Map.entry("OSL", "Oslo"),
            Map.entry("EDI", "Edimburgo"),
            Map.entry("BUD", "Budapest")
    );

    public View(Datamart datamart) {
        this.datamart = datamart;
        initUI();
    }

    private void initUI() {
        setTitle("VuelaConTuEquipo - Agencia de Viajes Deportivos");
        setSize(1300, 950);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout(15, 15));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_BLUE);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel logo = new JLabel("VuelaConTuEquipo");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.WEST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filters.setOpaque(false);

        JLabel dateLabel = new JLabel("Seleccione Fecha:");
        dateLabel.setForeground(Color.WHITE);
        filters.add(dateLabel);

        dateSelector = new JComboBox<>();
        updateDates();
        dateSelector.addActionListener(e -> updateMatches());
        filters.add(dateSelector);

        JButton refreshBtn = new JButton("BUSCAR OFERTAS");
        refreshBtn.setBackground(ACCENT_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> { updateDates(); updateMatches(); });
        filters.add(refreshBtn);

        header.add(filters, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setBackground(BG_WHITE);
        mainContent.setBorder(new EmptyBorder(10, 25, 25, 25));

        matchModel = new DefaultTableModel(new String[]{"LOCAL", "VISITANTE", "ESTADO", "HORA", "COMPETICIÓN", "CIUDAD DESTINO"}, 0);
        matchTable = createNonEditableTable(matchModel, PRIMARY_BLUE);
        matchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showFlightsForSelectedMatch();
        });

        mainContent.add(createTablePanel("PARTIDOS DISPONIBLES", matchTable), BorderLayout.NORTH);

        JPanel flightsPanel = new JPanel(new BorderLayout(0, 10));
        flightsPanel.setBackground(BG_WHITE);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setBackground(BG_WHITE);

        departureModel = new DefaultTableModel(new String[]{"Vuelo", "Aerolínea", "Salida", "Llegada", "Precio (USD)"}, 0);
        departureTable = createNonEditableTable(departureModel, new Color(40, 167, 69));
        departureTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateTotal();
        });

        returnModel = new DefaultTableModel(new String[]{"Vuelo", "Aerolínea", "Salida", "Llegada", "Precio (USD)"}, 0);
        returnTable = createNonEditableTable(returnModel, new Color(220, 53, 69));
        returnTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateTotal();
        });

        tablesPanel.add(createTablePanel("VUELOS DE IDA (A DESTINO)", departureTable));
        tablesPanel.add(createTablePanel("VUELOS DE VUELTA (A MADRID)", returnTable));
        flightsPanel.add(tablesPanel, BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(new Color(240, 248, 255));
        totalPanel.setBorder(BorderFactory.createLineBorder(ACCENT_BLUE, 1));

        totalLabel = new JLabel("Selecciona un vuelo de ida y uno de vuelta para ver el precio total");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 15));
        totalLabel.setForeground(PRIMARY_BLUE);
        totalPanel.add(totalLabel);

        flightsPanel.add(totalPanel, BorderLayout.SOUTH);
        mainContent.add(flightsPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    private void updateTotal() {
        int depRow = departureTable.getSelectedRow();
        int retRow = returnTable.getSelectedRow();

        if (depRow != -1) {
            selectedDeparturePrice = parsePrice((String) departureModel.getValueAt(depRow, 4));
        }
        if (retRow != -1) {
            selectedReturnPrice = parsePrice((String) returnModel.getValueAt(retRow, 4));
        }

        if (depRow != -1 && retRow != -1) {
            double total = selectedDeparturePrice + selectedReturnPrice;
            totalLabel.setText(String.format(
                    "✈ Ida: $%.2f  +  ↩ Vuelta: $%.2f  =  TOTAL VIAJE: $%.2f USD",
                    selectedDeparturePrice, selectedReturnPrice, total
            ));
            totalLabel.setForeground(new Color(0, 128, 0));
        } else if (depRow != -1) {
            totalLabel.setText(String.format("✈ Ida seleccionada: $%.2f — Selecciona también un vuelo de vuelta", selectedDeparturePrice));
            totalLabel.setForeground(ACCENT_BLUE);
        } else if (retRow != -1) {
            totalLabel.setText(String.format("↩ Vuelta seleccionada: $%.2f — Selecciona también un vuelo de ida", selectedReturnPrice));
            totalLabel.setForeground(ACCENT_BLUE);
        } else {
            totalLabel.setText("Selecciona un vuelo de ida y uno de vuelta para ver el precio total");
            totalLabel.setForeground(PRIMARY_BLUE);
        }
    }

    private double parsePrice(String priceStr) {
        try {
            return Double.parseDouble(priceStr.replace(",", ".").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private JTable createNonEditableTable(DefaultTableModel model, Color headerColor) {
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(headerColor);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        return table;
    }

    private JPanel createTablePanel(String title, JTable table) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_WHITE);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1), title);
        border.setTitleFont(new Font("Arial", Font.BOLD, 13));
        border.setTitleColor(PRIMARY_BLUE);
        p.setBorder(border);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(500, 250));
        return p;
    }

    private void updateDates() {
        String current = (String) dateSelector.getSelectedItem();
        dateSelector.removeAllItems();
        for (String date : datamart.getAvailableDates()) dateSelector.addItem(date);
        if (current != null) dateSelector.setSelectedItem(current);
    }

    private void updateMatches() {
        matchModel.setRowCount(0);
        departureModel.setRowCount(0);
        returnModel.setRowCount(0);
        selectedDeparturePrice = 0.0;
        selectedReturnPrice = 0.0;
        totalLabel.setText("Selecciona un vuelo de ida y uno de vuelta para ver el precio total");
        totalLabel.setForeground(PRIMARY_BLUE);

        String selectedDate = (String) dateSelector.getSelectedItem();
        if (selectedDate == null) return;

        for (Match m : datamart.getMatches(selectedDate)) {
            String cityName = cityNames.getOrDefault(m.airportCode().toUpperCase(), m.airportCode());
            String time = (m.matchDate() != null) ? m.matchDate().substring(0, 5) : "N/A";
            matchModel.addRow(new Object[]{
                    m.localTeam(), m.visitorTeam(), m.matchStatus(),
                    time, m.competition(), cityName
            });
        }
    }

    private void showFlightsForSelectedMatch() {
        departureModel.setRowCount(0);
        returnModel.setRowCount(0);
        selectedDeparturePrice = 0.0;
        selectedReturnPrice = 0.0;
        totalLabel.setText("Selecciona un vuelo de ida y uno de vuelta para ver el precio total");
        totalLabel.setForeground(PRIMARY_BLUE);

        int row = matchTable.getSelectedRow();
        if (row == -1) return;

        String local = (String) matchModel.getValueAt(row, 0);
        String visitor = (String) matchModel.getValueAt(row, 1);
        String date = (String) dateSelector.getSelectedItem();

        Match selectedMatch = datamart.getMatches(date).stream()
                .filter(m -> m.localTeam().equals(local) && m.visitorTeam().equals(visitor))
                .findFirst().orElse(null);

        if (selectedMatch == null) return;

        RecommendedTrip trip = datamart.getTrip(selectedMatch);
        if (trip == null) return;

        for (Flight f : trip.getDepartureFlights()) {
            departureModel.addRow(new Object[]{
                    f.flightNumber(), f.airline(),
                    f.departureTime().substring(11, 16),
                    f.arrivalTime().substring(11, 16),
                    String.format("%.2f", f.price())
            });
        }

        for (Flight f : trip.getReturnFlights()) {
            returnModel.addRow(new Object[]{
                    f.flightNumber(), f.airline(),
                    f.departureTime().substring(11, 16),
                    f.arrivalTime().substring(11, 16),
                    String.format("%.2f", f.price())
            });
        }
    }
}