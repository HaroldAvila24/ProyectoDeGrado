/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.udenarsql;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class UDENARVIEW extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextArea commandField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private DefaultTableModel queryModel;
    private JLabel welcomeLabel1;
    private JTabbedPane tabbedPane;

    public UDENARVIEW() {
        // Configurar la ventana JFrame
        setTitle("UDENARSQL GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana

        // Crear el CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Agregar los paneles al CardLayout
        mainPanel.add(createHomePanel(), "HomePanel");
        mainPanel.add(query(), "Query");
        mainPanel.add(createTablePanel(), "TablePanel");

        // Establecer el fondo en el mainPanel (contiene el CardLayout)
        //   mainPanel.setBackground(new Color(255, 193, 7));  // Color de fondo del panel principal
        // Agregar el mainPanel al JFrame
        add(mainPanel);

        // Crear el menú y configurar las acciones para cambiar entre paneles
        setJMenuBar(createMenuBar());
    }


    private JPanel createHomePanel() {
        // Crear el panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.decode("#FFFBF0")); // Fondo en color pastel suave

        // Logotipo principal color: #333; border-bottom: 2px solid #333; display: inline-block; padding-bottom: 5px;
        String title = "<html><div style='text-align: center;'>"
                + "<p style='color: #666666; font-size: 18px;'>Bienvenido  </p>"
                + "<h1 style='color: #333333; font-size: 36px;border-bottom: 2px solid #333; display: inline-block; padding-bottom: 5px'><b>UdenarSQL</b></h1>"
                + "</div></html>";
        ImageIcon logoIcon = new ImageIcon("src/img/icon.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Texto descriptivo
        String description = "<html><div style='text-align: center; color: #555555; font-size: 14px;'>"
                + "Integrado con rutinas ANN para optimizar<br>"
                + "la gestión de imágenes en operaciones SQL.<br>"
                + "Evalúa la eficiencia con modelos  modelos ANN<br>"
                + "y personalízalo según tus necesidades."
                + "</div></html>";
        JLabel descriptionLabel = new JLabel(description, SwingConstants.CENTER);
        descriptionLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Subpanel para la documentación
        Image scaledImageDoc = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel logoLabelDoc = new JLabel("Documentación", new ImageIcon(scaledImageDoc), SwingConstants.CENTER);
        logoLabelDoc.setHorizontalTextPosition(SwingConstants.CENTER);
        logoLabelDoc.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Agregar un MouseListener para capturar clics
        logoLabelDoc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String rutaPDF = "src/informacion/MANUAL_DEL_USUARIO.pdf";

                // Llamar al método para abrir el PDF
                documentacion(rutaPDF);
            }
        });
        // Subpanel para la comunidad
        ImageIcon iconComu = new ImageIcon("src/img/comunidad.gif");
        JLabel iconComuLabel = new JLabel("Comunidad", iconComu, SwingConstants.CENTER);
        iconComuLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        iconComuLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Agregar un MouseListener para capturar clics
        iconComuLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String rutaPDF = "src/informacion/udenarDBMShtml.html";

                // Llamar al método para abrir el PDF
                documentacion(rutaPDF);
            }
        });

        ImageIcon iconModel = new ImageIcon("src/img/AIModel.gif");
        JLabel iconModelLabel = new JLabel("Modelos", iconModel, SwingConstants.CENTER);
        iconModelLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        iconModelLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Agregar un MouseListener para capturar clics
        iconModelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model();
            }
        });
        // Crear un panel superior con el logotipo y el texto de bienvenida
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(descriptionLabel, BorderLayout.CENTER);
        topPanel.setBackground(Color.decode("#FFFBF0")); // Color de fondo del panel

        // Crear un panel inferior para la documentación y la comunidad
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 0, 0)); //Dos columnas
        bottomPanel.add(logoLabelDoc);
        bottomPanel.add(iconModelLabel);

        bottomPanel.add(iconComuLabel);

        bottomPanel.setBackground(Color.decode("#FFFBF0")); // Color de fondo del panel

        // Agregar componentes al panel principal
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(logoLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(descriptionLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        //  mainPanel.add(Box.createHorizontalStrut(50) );
        mainPanel.add(bottomPanel);

        return mainPanel;
    }

// Carga del modelo
    private void model() {
        // Crear un JDialog para mostrar la ventana emergente
        JDialog dialogo = new JDialog((Frame) null, "Gestión de Modelos", true);
        dialogo.setSize(500, 300); // Ajustamos el tamaño para acomodar los botones
        dialogo.setLayout(new BorderLayout());
        dialogo.setLocationRelativeTo(this);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
 
        // Texto descriptivo en la parte superior del diálogo
        JLabel titleLabel= new JLabel("<html><div style='text-align: center;'>"
                + "<h2 style='color: #333; border-bottom: 2px solid #333; display: inline-block; padding-bottom: 5px;'>"
                + "Selecciona o carga un modelo"
                + "</h2>"
                +"<br>"
                +"<h3> Sección en construcción...</h3>"
                + "</div></html>", SwingConstants.CENTER);
        dialogo.add(titleLabel, BorderLayout.NORTH);

        // Panel para los botones dinámicos con GridLayout (máximo 3 botones por filas)
        JPanel panelModelos = new JPanel(new GridLayout(0, 3, 10, 10)); // Máximo 3 columnas, filas dinámicas
        panelModelos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ruta de los modelos
        Path destinoDir = Paths.get("src/modelos/");
        try {
            Files.list(destinoDir).forEach(archivo -> {
                if (Files.isRegularFile(archivo)) {
                    // Crear un botón por archivo
                    JButton boton = new JButton(archivo.getFileName().toString());
                    boton.setBackground(Color.decode("#F28705"));
                    boton.setForeground(Color.WHITE);
                    boton.setFocusPainted(false);
                    boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                    // Agregar ActionListener al botón
                    boton.addActionListener(e -> {
                        System.out.println("Se seleccionó el archivo: " + archivo);
                        // Aquí puedes agregar lógica para cargar o trabajar con el archivo
                    });

                    // Agregar el botón al panel de modelos
                    panelModelos.add(boton);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(UDENARVIEW.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Panel en la parte inferior para botones "Cargar" y "Cancelar"
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton cargarButton = new JButton("Cargar Modelo");
        cargarButton.setBackground(Color.decode("#000000"));
        cargarButton.setForeground(Color.WHITE);
        cargarButton.setFocusPainted(false);
        cargarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.setBackground(Color.decode("#FFDF0C"));
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.setFocusPainted(false);
        cancelarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Evento para el botón "Cargar Modelo"
        cargarButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int opcion = fileChooser.showOpenDialog(dialogo);
            if (opcion == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = fileChooser.getSelectedFile();
                guardarModelo(archivoSeleccionado);

                // Crear un nuevo botón para el modelo cargado
                JButton nuevoBoton = new JButton(archivoSeleccionado.getName());
                nuevoBoton.setBackground(Color.decode("#F29F05"));
                nuevoBoton.setForeground(Color.WHITE);
                nuevoBoton.setFocusPainted(false);
                nuevoBoton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                nuevoBoton.addActionListener(evt -> System.out.println("archivoSeleccionado.getName() = " + archivoSeleccionado.getName()));//JOptionPane.showMessageDialog(dialogo, "Modelo seleccionado: " + archivoSeleccionado.getName()));

                // Agregar el nuevo botón al panel
                panelModelos.add(nuevoBoton);
                dialogo.revalidate();
                dialogo.repaint();
            }
        });

        // Evento para el botón "Cancelar"
        cancelarButton.addActionListener(e -> dialogo.dispose());

        // Añadir botones al panel inferior
        panelInferior.add(cargarButton);
        panelInferior.add(cancelarButton);

        // Agregar paneles al diálogo
        dialogo.add(panelModelos, BorderLayout.CENTER);
        dialogo.add(panelInferior, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

// Método para guardar el modelo seleccionado
    private void guardarModelo(File modelo) {
        try {
            // Crear el directorio de destino si no existe
            Path destinoDir = Paths.get("src/modelos/");
            if (!Files.exists(destinoDir)) {
                Files.createDirectories(destinoDir);
            }

            // Copiar el archivo al directorio de destino falta hacer que la ruta se envie al modelo de IA..
            Path destino = destinoDir.resolve(modelo.getName());
            Files.copy(modelo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("destino = " + destino.toString());
            // JOptionPane.showMessageDialog(null, "Modelo guardado con éxito: " + destino.toString());
        } catch (IOException ex) {
            System.out.println("\"Error al guardar el modelo: \" = " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error al cargar modelo: " + ex.getMessage());
            ex.printStackTrace(); // Para depurar
        }
    }

  
    private JPanel query() {
        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new BorderLayout());

        // Crear el componente JTextArea para múltiples líneas
        commandField = new JTextArea(6, 40);  // 5 filas, 40 columnas
        //commandField.setWrapStyleWord(true); // Evitar que las palabras se dividan a la mitad
        // commandField.setLineWrap(true); // Permitir el salto de línea automáticamente
        commandField.setToolTipText("Ingrese comando SQL...");
        commandField.setText(""); // Inicializar el campo vacío
        commandField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        //commandField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        commandField.setBackground(new Color(251, 248, 242));

        JScrollPane commandScrollPane = new JScrollPane(commandField);

        // Crear el botón para ejecutar el comando
        JButton executeButton = new JButton("");
        executeButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        executeButton.setBackground(new Color(255, 193, 7));
        executeButton.setIcon(new ImageIcon("src\\img\\play-24.png")); // Coloca un ícono adecuado aquí
        executeButton.setFocusPainted(true);

        // Crear el botón "Save" con ícono
        JButton saveButton = new JButton("");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        saveButton.setBackground(new Color(255, 193, 7));
        saveButton.setIcon(new ImageIcon("src\\img\\guardar.png")); // Coloca un ícono adecuado aquí
        saveButton.setFocusPainted(false);

        // Panel para los botones "Run" y "Save"
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(executeButton);
        buttonPanel.add(saveButton);

        // Crear el modelo de la tabla
        queryModel = new DefaultTableModel(new Object[0][0], new Object[0]);
        resultTable = new JTable(queryModel);
        resultTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        resultTable.setBackground(new Color(243, 234, 210));
        resultTable.setForeground(Color.BLACK);
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        resultTable.getTableHeader().setBackground(new Color(221, 131, 44));
        resultTable.getTableHeader().setForeground(Color.black);

        JScrollPane tableScrollPane = new JScrollPane(resultTable);

        // Área para mensajes
        welcomeLabel1 = new JLabel(""); // Inicializar el JLabel
        welcomeLabel1.setForeground(Color.BLACK); // Color inicial
        welcomeLabel1.setBackground(new Color(251, 248, 242));

        welcomeLabel1.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.add(welcomeLabel1, BorderLayout.NORTH);

        // Crear el componente JTabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 14));

        tabbedPane.setBackground(new Color(243, 234, 210));
        tabbedPane.addTab("Data Output", tableScrollPane);
        tabbedPane.addTab("Messages", messagePanel);

        //      tabbedPane.setBackgroundAt(1, new Color(243, 234, 210));
        // tabbedPane.setBackgroundAt(2, new Color(243, 234, 210));
        // tabbedPane.setForeground(Color.red);
        // Configurar el diseño del panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(buttonPanel, BorderLayout.NORTH); // Agregar los botones en la parte superior
        topPanel.add(commandScrollPane, BorderLayout.CENTER); // Usar JScrollPane para JTextArea
        //  topPanel.add(welcomeLabel1, BorderLayout.SOUTH); // Agregar el mensaje en la parte inferior
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Añadir componentes al panel principal
        queryPanel.add(topPanel, BorderLayout.NORTH);
        queryPanel.add(tabbedPane, BorderLayout.CENTER);

        // Acción del botón para ejecutar el comando
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeSQLCommand();
            }
        });

        // Acción del botón "Save"
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });

        return queryPanel;
    }

    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // Menú "Scripts"
        JMenu archivoMenu = new JMenu("Scripts");
        JMenu inicio = new JMenu("Inicio");
        JMenu query = new JMenu("Query");

        JMenuItem homeItem = new JMenuItem("Inicio");
        JMenuItem tableItem = new JMenuItem("Query");
        JMenuItem CreateItem = new JMenuItem("Create");
        JMenuItem InsertItem = new JMenuItem("Insert");
        JMenuItem UpdateItem = new JMenuItem("Update");
        JMenuItem DeleteItem = new JMenuItem("Delete");
        JMenuItem SelectItem = new JMenuItem("Select");
        JMenuItem SelectIAItem = new JMenuItem("Select IA");

        // Acción para mostrar el panel de inicio
        homeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "HomePanel");
            }
        });
        // Acción para creart tabla
        tableItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Query");
            }
        });

        // Acción para creart tabla
        CreateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "TablePanel");
            }
        });

        //SELECT botton 
        SelectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                select();
            }
        });
        SelectIAItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                selectIA();
            }
        });
        //INSERT botton
        InsertItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insert();
            }
        });

        //botón "Update"
        UpdateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });

        DeleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                delete();
            }
        });
// Agregar los ítems al menú "Inicio"
        inicio.add(homeItem);

        query.add(tableItem);
        archivoMenu.add(CreateItem);
        archivoMenu.add(SelectItem);
        archivoMenu.add(SelectIAItem);
        archivoMenu.add(InsertItem);
        archivoMenu.add(UpdateItem);
        archivoMenu.add(DeleteItem);
        menuBar.add(inicio);
        menuBar.add(query);
        menuBar.add(archivoMenu);
        menuBar.setBackground(Color.decode("#F2EC91"));

        return menuBar;
    }

    // Panel con la tabla (TablePanel)

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.decode("#0E151B")); // Fondo principal

        // Campo de texto para el nombre de la tabla tableNameField
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#E4DDC3")); // Fondo del panel superior
        
        JLabel TittleLabel = new JLabel("Crear Tabla");
        TittleLabel.setForeground(Color.decode("#0E151B")); // Color del texto
        TittleLabel.setFont(new Font("SansSerif", Font.BOLD, 20)); // Fuente de la tabla

        JLabel tableNameLabel = new JLabel("Nombre de la Tabla:");
        tableNameLabel.setForeground(Color.decode("#0E151B")); // Color del texto
        tableNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Fuente de la tabla

        JTextField tableNameField = new JTextField(20); // Campo para ingresar el nombre de la tabla
        tableNameField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Fuente de la tabla
        tableNameField.setBackground(Color.decode("#F8F6E1")); // Fondo del botón
        
        JPanel  centerTopPanel= new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerTopPanel.setBackground(Color.decode("#E4DDC3"));
        centerTopPanel.add(TittleLabel);
        centerTopPanel.add(TittleLabel);
        //#F9B40A
        JPanel leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftTopPanel.setBackground(Color.decode("#E4DDC3"));
        leftTopPanel.add(tableNameLabel);
        leftTopPanel.add(tableNameField);

        JButton addRowButton = new JButton();
        addRowButton.setIcon(new ImageIcon("src\\img\\add_column.png"));
        addRowButton.setBackground(Color.decode("#F8F6E1")); // Fondo del botón

        JButton deleteRowButton = new JButton();
        deleteRowButton.setIcon(new ImageIcon("src\\img\\delete_row.png"));
        deleteRowButton.setBackground(Color.decode("#F8F6E1")); // Fondo del botón para eliminar filas

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightTopPanel.setBackground(Color.decode("#E4DDC3"));
        rightTopPanel.add(addRowButton);
        rightTopPanel.add(deleteRowButton);

        topPanel.add(leftTopPanel, BorderLayout.WEST);
        topPanel.add(rightTopPanel, BorderLayout.EAST);
        topPanel.add(centerTopPanel, BorderLayout.NORTH);

        tablePanel.add(topPanel, BorderLayout.NORTH);

        // Modelo de la tabla
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Tipo de Texto");
        tableModel.addColumn("Especificaciones");

        // Tabla
        JTable table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Fuente de la tabla
        table.setBackground(Color.decode("#F8F6E1")); // Fondo de la tabla
        table.setForeground(Color.decode("#1E2A38")); // Color del texto de la tabla
        table.setGridColor(Color.decode("#938875")); // Color de las líneas de la tabla
        table.getTableHeader().setBackground(Color.decode("#F9B40A"));

        // Asignar JComboBox como editor para las columnas "Tipo de Texto" y "Especificaciones"
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"VARCHAR", "INT", "FLOAT"});
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(typeComboBox));

        JComboBox<String> specsComboBox = new JComboBox<>(new String[]{"", "PRIMARY KEY", "UNIQUE", "NOT NULL", "CLASS"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(specsComboBox));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.decode("#F8F6E1")); // Fondo del scroll
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Barra de herramientas con generar SQL
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(Color.decode("#E4DDC3")); // Fondo de la barra de herramientas

        JButton generateSQLButton = new JButton("Guardar");
        generateSQLButton.setBackground(Color.decode("#F8F6E1"));
        generateSQLButton.setForeground(Color.decode("#F9B40A"));

        toolbar.add(generateSQLButton);
        tablePanel.add(toolbar, BorderLayout.SOUTH);

        // Acción del botón para agregar filas
        addRowButton.addActionListener(e -> {
            tableModel.addRow(new Object[]{"", "", ""}); // Agregar fila con valores iniciales
        });

        // Acción del botón para eliminar filas
        deleteRowButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila para eliminar.");
            }
        });

        // Acción del botón para generar el SQL
        generateSQLButton.addActionListener(e -> {
            String tableName = tableNameField.getText().trim(); // Obtener el nombre de la tabla
            if (tableName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese el nombre de la tabla.");
                return;
            }

            StringBuilder sql = new StringBuilder("CREATE TABLE ");
            sql.append(tableName).append(" (");

            // Recorrer las filas de la tabla y construir la estructura de columnas
            int rowCount = tableModel.getRowCount();
            System.out.println("rowCount = " + rowCount);
            if (rowCount > 0) {
                for (int i = 0; i < rowCount; i++) {
                    String columnName = tableModel.getValueAt(i, 0).toString().trim();
                    String columnType = tableModel.getValueAt(i, 1).toString().trim();
                    String columnSpecs = tableModel.getValueAt(i, 2) != null ? tableModel.getValueAt(i, 2).toString().trim() : "";

                    if (columnName.isEmpty() || columnType.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Por favor, complete los campos 'Nombre' y 'Tipo de Texto' en todas las filas.");
                        return;
                    }

                    sql.append(columnName).append(" ").append(columnType);
                    if (!columnSpecs.isEmpty()) {
                        sql.append(" ").append(columnSpecs);
                    }
                    if (i < rowCount - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(");");
                commandField.setText(sql.toString());
                executeSQLCommand();
                // Limpiar el panel
                tableNameField.setText(""); // Limpiar el nombre de la tabla
                tableModel.setRowCount(0); // Eliminar todas las filas de la
            } else {
                JOptionPane.showMessageDialog(null, "La relacion no puede crear sin columnas");
            }

            // Mostrar el resultado en un cuadro de diálogo
            //  JOptionPane.showMessageDialog(null, "SQL Generado:\n" + sql.toString());
        });

        return tablePanel;
    }

    private void select() {
        // Mostrar cuadro de diálogo para ingresar el nombre de la tabla
        String tableName = JOptionPane.showInputDialog(
                null,
                "Ingrese el nombre de la tabla",
                "Select",
                JOptionPane.QUESTION_MESSAGE
        );

        // Si el usuario cierra el cuadro de diálogo, simplemente salir del método
        if (tableName == null) {
            return; // Salir sin hacer nada
        }

        // Verificar que el nombre de la tabla no esté vacío
        if (!tableName.trim().isEmpty()) {
            String command = "SELECT * FROM " + tableName.trim() + ";";
            commandField.setText(command); // Asignar el comando a commandField

            // Mostrar el panel Query
            cardLayout.show(mainPanel, "Query");
        } else {
            // Mostrar mensaje de error si el campo está vacío
            JOptionPane.showMessageDialog(
                    null,
                    "Debe ingresar un nombre de tabla válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void selectIA() {

        // Mostrar cuadro de diálogo para ingresar el nombre de la tabla
        String tableName = JOptionPane.showInputDialog(null,
                "Ingrese el nombre de la tabla:",
                "Select IA",
                JOptionPane.QUESTION_MESSAGE
        );
        // Si el usuario cierra el cuadro de diálogo, simplemente salir del método
        if (tableName == null) {
            return; // Salir sin hacer nada
        }

        // Verificar que el nombre de la tabla no esté vacío
        if (!tableName.trim().isEmpty()) {
            String command = "SELECT * FROM " + tableName.trim() + " where column = IA('ruta');";
            commandField.setText(command); // Asignar el comando a commandField

            // Mostrar el panel Query
            cardLayout.show(mainPanel, "Query");
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe ingresar un nombre de tabla válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }

    private void insert() {
        // Pedir al usuario el nombre de la tabla
        String tableName = JOptionPane.showInputDialog(null, "Ingrese el nombre de la tabla",
                "Insert",
                JOptionPane.QUESTION_MESSAGE);

        // Si el usuario cierra el cuadro de diálogo, simplemente salir del método
        if (tableName == null) {
            return; // Salir sin hacer nada
        }

        if (!tableName.trim().isEmpty()) {
            // Construir la consulta SELECT * para obtener las columnas
            String query = "SELECT * FROM " + tableName.trim();

            String response = conexion_ser(query);

            // Procesar la respuesta para contar las columnas
            String[] rows = response.split("\n");
            if (rows.length > 0) {
                String[] headers = rows[0].split(","); // Obtener las columnas de la tabla
                int columnCount = headers.length;

                // Generar el comando INSERT con placeholders
                StringBuilder insertCommand = new StringBuilder("INSERT INTO  ");
                /*
                        for (int i = 0; i < columnCount; i++) {
                            insertCommand.append(headers[i].replaceAll("\\[", "(").replaceAll("\\]", ") "));
                            if (i < columnCount - 1) {
                                insertCommand.append(", ");
                            }
                        }*/
                insertCommand.append(tableName).append(" VALUES (");

                for (int i = 0; i < columnCount; i++) {
                    insertCommand.append("?");
                    if (i < columnCount - 1) {
                        insertCommand.append(", ");
                    }
                }
                insertCommand.append(");");

                // Asignar la consulta generada al campo commandField
                commandField.setText(insertCommand.toString());

                // Mostrar el panel Query
                cardLayout.show(mainPanel, "Query");
                // JOptionPane.showMessageDialog(null, "Consulta generada:\n" + insertCommand.toString());
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron columnas en la tabla.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Inserte el nombre de la tabla.");

        }
    }

    private void update() {
        // Solicitar el nombre de la tabla al usuario
        String tableName = JOptionPane.showInputDialog(null,"Ingrese el nombre de la tabla",
                "Update",
                JOptionPane.QUESTION_MESSAGE);
        // Si el usuario cierra el cuadro de diálogo, simplemente salir del método
        if (tableName == null) {
            return; // Salir sin hacer nada
        }

        if (!tableName.trim().isEmpty()) {
            // Realizar consulta para obtener columnas de la tabla
            String selectQuery = "SELECT * FROM " + tableName + " LIMIT 1;";

            String response = conexion_ser(selectQuery);

            // Procesar la respuesta para obtener los nombres de las columnas
            String[] rows = response.split("\n");
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(null, "No se encontraron columnas en la tabla especificada.");
                return;
            }

            // La primera fila contiene los encabezados
            String[] headers = rows[0].split(",");

            // Construir la consulta UPDATE dinámica
            StringBuilder sql = new StringBuilder("UPDATE ");
            sql.append(tableName).append(" SET ");

            for (int i = 0; i < headers.length; i++) {
                sql.append(headers[i].replaceAll("\\[", "").replaceAll("\\]", " ")).append(" = ? ");
                if (i < headers.length - 1) {
                    sql.append(", ");
                }
            }
            sql.append(" WHERE condición;");

            // Establecer la consulta en el campo de comandos
            commandField.setText(sql.toString());

            // Mostrar el panel Query
            cardLayout.show(mainPanel, "Query");
            // Mostrar un cuadro de diálogo con la consulta generada
            //JOptionPane.showMessageDialog(null, "Consulta SQL generada:\n" + sql.toString());
        } else {

            JOptionPane.showMessageDialog(null, "Inserte el nombre de la tabla.");

        }
    }

    private void delete() {
        // Solicitar el nombre de la tabla al usuario
        String tableName = JOptionPane.showInputDialog(null,
                "Ingrese el nombre de la tabla",
                "Delete",
                JOptionPane.QUESTION_MESSAGE
        );
// Si el usuario cierra el cuadro de diálogo, simplemente salir del método
        if (tableName == null) {
            return; // Salir sin hacer nada
        }

        // Verificar que el nombre de la tabla no esté vacío
        if (!tableName.trim().isEmpty()) {
            String command = "DELETE FROM " + tableName.trim() + " WHERE condition;";
            commandField.setText(command); // Asignar el comando a commandField

            // Mostrar el panel Query
            cardLayout.show(mainPanel, "Query");
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe ingresar un nombre de tabla válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void executeSQLCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El comando está vacío. Intente nuevamente.");
            return;
        }
        //    System.out.println("commandField = " + commandField.getText());
        //  System.out.println("command = " + command);

        String response = conexion_ser(command);

        // Limpiar la tabla antes de mostrar nuevos resultados  Failed to parse SQL query: null
        welcomeLabel1.setText("");
        queryModel.setRowCount(0);
        // Mostrar el panel Query
        cardLayout.show(mainPanel, "Query");
        if (!response.toLowerCase().trim().contains("Failed to parse SQL query: null".toLowerCase().trim())) {
            // Procesar la respuesta y actualizar la tabla
            String[] rows = response.split("\n");

            if (rows.length > 0) {

                String[] headers = rows[0].split(",");
                String texto = String.join(",", headers);
                if (texto.isEmpty()) {
                    welcomeLabel1.setText("No existe la relación");
                    welcomeLabel1.setForeground(Color.GREEN); // Cambiar el color a verde
                    tabbedPane.setSelectedComponent(welcomeLabel1.getParent()); // Cambiar a la pestaña "Mensajes" 

                } else if ((texto.startsWith("[") && texto.endsWith("]"))) {

                    // Remover corchetes de los encabezados si están presentes
                    for (int i = 0; i < headers.length; i++) {
                        headers[i] = headers[i].replace("[", "").replace("]", "").trim();
                    }
                    // Agregar "Cantidad" como primera columna
                    Object[] newHeaders = new Object[headers.length + 1];
                    newHeaders[0] = " ";
                    System.arraycopy(headers, 0, newHeaders, 1, headers.length);
                    queryModel.setColumnIdentifiers(newHeaders);

                    // Agregar las filas con el contador
                    for (int i = 1; i < rows.length; i++) {
                        String[] columns = rows[i].split(",");
                        Object[] rowData = new Object[columns.length + 1];
                        rowData[0] = Integer.toString(i); // Columna "Cantidad"
                        System.arraycopy(columns, 0, rowData, 1, columns.length);
                        queryModel.addRow(rowData);
                    }
                    welcomeLabel1.setText("Filas afectadas " + (rows.length - 1) );
                    // welcomeLabel1.setForeground(Color.GREEN); // Cambiar el color a verde

                    tabbedPane.setSelectedComponent(resultTable.getParent().getParent()); // Cambiar a la pestaña "Resultados"
                    // Mostrar la cantidad de filas (sin incluir el encabezado)
                    //JOptionPane.showMessageDialog(this, (rows.length - 1) + " Rows Affected", "Notice", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String textoConSaltos = "<html>" + response.replaceAll("(.{35})", "$1<br>") + "</html>";

                    // Mostrar mensaje de error en el JLabel
                    welcomeLabel1.setText(textoConSaltos);
                    //   welcomeLabel1.setForeground(Color.GREEN); // Cambiar el color a rojo para resaltar el error
                    // Cambiar a la pestaña de "Mensajes"

                    tabbedPane.setSelectedComponent(welcomeLabel1.getParent()); // Cambiar a la pestaña "Mensajes" 
                    // Limpia los datos de la tabla si hay alguno
                    queryModel.setRowCount(0);
                    queryModel.setColumnCount(0);

                    // Mostrar la cantidad de filas (sin incluir el encabezado)
                    // JOptionPane.showMessageDialog(this, " Tabla ", "Notice", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            // Mostrar la cantidad de filas (sin incluir el encabezado)
            //JOptionPane.showMessageDialog(this, "error", "Notice", JOptionPane.INFORMATION_MESSAGE);

            String textoConSaltos = "<html>" + response.replaceAll("(.{35})", "$1<br>") + "</html>";

            welcomeLabel1.setText(textoConSaltos);
            // welcomeLabel1.setForeground(Color.RED); // Cambiar el color a rojo para resaltar el error
            tabbedPane.setSelectedComponent(welcomeLabel1.getParent()); // Cambiar a la pestaña "Mensajes" 

            // Limpia los datos de la tabla si hay alguno
            queryModel.setRowCount(0);
            queryModel.setColumnCount(0);
        }

    }

    private String receiveLargeData(InputStream in) throws Exception {
        DataInputStream dis = new DataInputStream(in);
        int length = dis.readInt();
        byte[] bytes = new byte[length];
        dis.readFully(bytes);
        return new String(bytes, "UTF-8");
    }

    private void documentacion(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);

            // Verificar si el archivo existe
            if (!archivo.exists()) {
                JOptionPane.showMessageDialog(null, "El archivo no existe: " + rutaArchivo, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Usar Desktop para abrir el archivo
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(archivo);
            } else {
                JOptionPane.showMessageDialog(null, "La funcionalidad no está soportada en este sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar abrir el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String conexion_ser(String command) {
        try {
            // Conexión al servidor y envío del comando
            Socket client = new Socket("localhost", 5050);
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            InputStream in = client.getInputStream();

            out.writeUTF(command);
            String response = receiveLargeData(in);
            System.out.println("response-> " + response);
            return response;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
        return "Error";
    }

    private void saveToFile() {
        String content = commandField.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay contenido para guardar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como...");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files (*.sql)", "sql"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "Archivo guardado: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage());
            }
        }
    }

    private static void executeJar() {
        try {
            // Ruta al archivo .jar
            String jarPath = "C:\\Users\\yesec\\OneDrive\\Documentos\\TrabajoDeGrado\\ProyectoYJH\\PROYECTO VERSION FINAL\\UDENARDBMS\\target\\UDENARDBMS-1.0.jar";
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarPath);
            processBuilder.start(); // Ejecutar el archivo .jar
            System.out.println("Ejecutando archivo --jar: " + jarPath);
        } catch (IOException ex) {
            System.err.println("Error al ejecutar el archivo .jar: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                executeJar();
                new UDENARVIEW().setVisible(true);
            }
        });
    }

}
