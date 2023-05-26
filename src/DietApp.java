import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DietApp extends JFrame {
    private JTextField idField;
    private JTextField nombreField;
    private JTextField edadField;
    private JTextField pesoField;
    private JTextField alturaField;
    private JComboBox<String> objetivoComboBox;
    private JTextField caloriasField;
    private JLabel imagenLabel;
    private ImageIcon clienteImagen;
    private JButton imagenButton;

    private List<Cliente> clientes;
    private DefaultListModel<String> clientesListModel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/dietApp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin";

    public DietApp() {
        clientes = new ArrayList<>();
        clientesListModel = new DefaultListModel<>();

        setTitle("Aplicació de Dietes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel idLabel = new JLabel("ID del client:");
        idField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(idField, gbc);

        JLabel nombreLabel = new JLabel("Nom del client:");
        nombreField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        addPanel.add(nombreLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(nombreField, gbc);

        JLabel edadLabel = new JLabel("Edat:");
        edadField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPanel.add(edadLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(edadField, gbc);

        JLabel pesoLabel = new JLabel("Pes (kg):");
        pesoField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 3;
        addPanel.add(pesoLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(pesoField, gbc);

        JLabel alturaLabel = new JLabel("Alçada (cm):");
        alturaField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 4;
        addPanel.add(alturaLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(alturaField, gbc);


        JLabel objetivoLabel = new JLabel("Objectiu:");
        objetivoComboBox = new JComboBox<>(new String[]{"Deficit calòric", "Mantenir pes", "Augmentar pes"});
        gbc.gridx = 0;
        gbc.gridy = 5;
        addPanel.add(objetivoLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(objetivoComboBox, gbc);

        JLabel caloriasLabel = new JLabel("Calories a consumir:");
        caloriasField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 6;
        addPanel.add(caloriasLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(caloriasField, gbc);

        JLabel imagenLabel = new JLabel("Imatge:");
        imagenButton = new JButton("Seleccionar imatge");
        gbc.gridx = 0;
        gbc.gridy = 7;
        addPanel.add(imagenLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(imagenButton, gbc);

        JButton agregarButton = new JButton("Agregar client");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        addPanel.add(agregarButton, gbc);

        tabbedPane.addTab("Agregar client", addPanel);

        JPanel listPanel = new JPanel(new BorderLayout());
        JList<String> clientesList = new JList<>(clientesListModel);
        listPanel.add(new JScrollPane(clientesList), BorderLayout.CENTER);
        tabbedPane.addTab("Llista de clients", listPanel);

        add(tabbedPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarCliente();
            }
        });

        imagenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarImagen();
            }
        });

        clientesList.addListSelectionListener(e -> {
            int selectedIndex = clientesList.getSelectedIndex();
            if (selectedIndex != -1) {
                Cliente cliente = clientes.get(selectedIndex);
                mostrarInformacionClientePopup(cliente);
            }
        });


        cargarClientesDeBaseDeDatos();
    }

    private void cargarClientesDeBaseDeDatos() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                int edat = rs.getInt("edat");
                double pes = rs.getDouble("pes");
                double altura = rs.getDouble("altura");
                String objectiu = rs.getString("objectiu");
                int calories = rs.getInt("calories");
                ImageIcon imagen = new ImageIcon(rs.getString("imagen"));

                Cliente client = new Cliente(id, nom, edat, pes, altura, objectiu, calories, imagen);
                clientes.add(client);
                clientesListModel.addElement(client.getNom());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarCliente() {
        int id = Integer.parseInt(idField.getText());
        String nombre = nombreField.getText();
        int edad = Integer.parseInt(edadField.getText());
        double peso = Double.parseDouble(pesoField.getText());
        double altura = Double.parseDouble(alturaField.getText());
        String objetivo = (String) objetivoComboBox.getSelectedItem();
        int calorias = Integer.parseInt(caloriasField.getText());
        String imagenPath = "";

        Cliente cliente = new Cliente(id, nombre, edad, peso, altura, objetivo, calorias, clienteImagen);
        clientes.add(cliente);
        clientesListModel.addElement(cliente.getNom());

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO clients VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            ps.setInt(1, cliente.getId());
            ps.setString(2, cliente.getNom());
            ps.setInt(3, cliente.getEdat());
            ps.setDouble(4, cliente.getPes());
            ps.setDouble(5, cliente.getAltura());
            ps.setString(6, cliente.getObjectiu());
            ps.setInt(7, cliente.getCalories());
            ps.setString(8, imagenPath);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String imagePath = fileChooser.getSelectedFile().getPath();
            clienteImagen = new ImageIcon(imagePath);
            imagenButton.setIcon(clienteImagen);
        }
    }

    private void mostrarInformacionClientePopup(Cliente cliente) {
        JDialog dialog = new JDialog(this, "Informació del client", true);
        dialog.setSize(300, 400);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());

        // Mostrar la imagen del cliente
        JLabel imagenLabel = new JLabel(cliente.getImagen());
        panel.add(imagenLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(7, 2));
        infoPanel.add(new JLabel("ID del client:"));
        infoPanel.add(new JLabel(String.valueOf(cliente.getId())));
        infoPanel.add(new JLabel("Nom del client:"));
        infoPanel.add(new JLabel(cliente.getNom()));
        infoPanel.add(new JLabel("Edat:"));
        infoPanel.add(new JLabel(String.valueOf(cliente.getEdat())));
        infoPanel.add(new JLabel("Pes (kg):"));
        infoPanel.add(new JLabel(String.valueOf(cliente.getPes())));
        infoPanel.add(new JLabel("Alçada (cm):"));
        infoPanel.add(new JLabel(String.valueOf(cliente.getAltura())));
        infoPanel.add(new JLabel("Objectiu:"));
        infoPanel.add(new JLabel(cliente.getObjectiu()));
        infoPanel.add(new JLabel("Calories a consumir:"));
        infoPanel.add(new JLabel(String.valueOf(cliente.getCalories())));

        panel.add(infoPanel, BorderLayout.CENTER);

        dialog.add(panel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Tancar");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    class Cliente {
        private int id;
        private String nom;
        private int edat;
        private double pes;
        private double altura;
        private String objectiu;
        private int calories;
        private ImageIcon imagen;

        public Cliente(int id, String nom, int edat, double pes, double altura, String objectiu, int calories, ImageIcon imagen) {
            this.id = id;
            this.nom = nom;
            this.edat = edat;
            this.pes = pes;
            this.altura = altura;
            this.objectiu = objectiu;
            this.calories = calories;
            this.imagen = imagen;
        }

        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        public int getEdat() {
            return edat;
        }

        public double getPes() {
            return pes;
        }

        public double getAltura() {
            return altura;
        }

        public String getObjectiu() {
            return objectiu;
        }

        public int getCalories() {
            return calories;
        }

        public ImageIcon getImagen() {
            return imagen;
        }
    }

    public static void main(String[] args) {
        new DietApp();
    }
}