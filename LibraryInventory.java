import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

    public class LibraryInventory extends JFrame {
        private JTextField txtBookID, txtTitle, txtAuthor, txtYear;
        private JTable bookTable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public LibraryInventory() {
            setTitle("Victoria University Library Book Inventory");
            setSize(1000, 800);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // Title Panel
            JPanel titlePanel = new JPanel();
            JLabel lblTitle = new JLabel("Victoria University Library Book Inventory");
            lblTitle.setFont(new Font("Arial", Font.BOLD, 25));
            lblTitle.setForeground(Color.RED);
            titlePanel.add(lblTitle);
            add(titlePanel, BorderLayout.NORTH);

            // Form Panel
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new GridLayout(4, 2));
            JLabel lblBookID = new JLabel("Book ID:");
            JLabel lblTitleLabel = new JLabel("Title:");
            JLabel lblAuthor = new JLabel("Author:");
            JLabel lblYear = new JLabel("Year:");
            lblBookID.setFont(new Font("Arial", Font.BOLD, 12));
            lblTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
            lblAuthor.setFont(new Font("Arial", Font.BOLD, 12));
            lblYear.setFont(new Font("Arial", Font.BOLD, 12));
            txtBookID = new JTextField(10);
            txtTitle = new JTextField(10);
            txtAuthor = new JTextField(10);
            txtYear = new JTextField(10);
            formPanel.add(lblBookID);
            formPanel.add(txtBookID);
            formPanel.add(lblTitleLabel);
            formPanel.add(txtTitle);
            formPanel.add(lblAuthor);
            formPanel.add(txtAuthor);
            formPanel.add(lblYear);
            formPanel.add(txtYear);
            add(formPanel, BorderLayout.CENTER);

            // Table Panel
            JPanel tablePanel = new JPanel();
            tablePanel.setLayout(new BorderLayout());
            tableModel = new DefaultTableModel();
            bookTable = new JTable(tableModel);
            tableModel.addColumn("Book ID");
            tableModel.addColumn("Title");
            tableModel.addColumn("Author");
            tableModel.addColumn("Year");
            bookTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            bookTable.getTableHeader().setForeground(Color.BLUE);
            JScrollPane scrollPane = new JScrollPane(bookTable);
            tablePanel.add(scrollPane, BorderLayout.AFTER_LAST_LINE);
            add(tablePanel, BorderLayout.AFTER_LAST_LINE);

            // Button Panel
            JPanel buttonPanel = new JPanel();
            JButton btnAdd = new JButton("Add Book");
            JButton btnDelete = new JButton("Delete Book");
            JButton btnRefresh = new JButton("Refresh List");
            btnAdd.setBackground(Color.BLUE);
            btnAdd.setForeground(Color.WHITE);
            btnDelete.setBackground(Color.RED);
            btnDelete.setForeground(Color.WHITE);
            btnRefresh.setBackground(Color.DARK_GRAY);
            btnRefresh.setForeground(Color.WHITE);
            buttonPanel.add(btnAdd);
            buttonPanel.add(btnDelete);
            buttonPanel.add(btnRefresh);
            add(buttonPanel, BorderLayout.AFTER_LINE_ENDS);

            // Database connection
            connectToDatabase();

            // Button actions
            btnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addBook();
                }
            });

            btnDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteBook();
                }
            });

            btnRefresh.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadBooks();
                }
            });

            // Load books initially
            loadBooks();
        }

        private void connectToDatabase() {
            try {
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
                connection = DriverManager.getConnection("jdbc:ucanaccess://C:/Users/USER/Documents/LibraryDB.accdb");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Connection Failed!");
            }
        }

        private void addBook() {
            String bookID = txtBookID.getText();
            String title = txtTitle.getText();
            String author = txtAuthor.getText();
            String year = txtYear.getText();

            try {
                String query = "INSERT INTO Books (BookID, Title, Author, Year) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, bookID);
                stmt.setString(2, title);
                stmt.setString(3, author);
                stmt.setString(4, year);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book Added Successfully!");
                clearForm();
                loadBooks();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Adding Book!");
            }
        }

        private void deleteBook() {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                String bookID = tableModel.getValueAt(selectedRow, 0).toString();
                try {
                    String query = "DELETE FROM Books WHERE BookID = ?";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setString(1, bookID);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book Deleted Successfully!");
                    loadBooks();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error Deleting Book!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please Select a Book to Delete!");
            }
        }

        private void loadBooks() {
            try {
                String query = "SELECT * FROM Books";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                tableModel.setRowCount(0);  // Clear existing rows
                while (rs.next()) {
                    String bookID = rs.getString("BookID");
                    String title = rs.getString("Title");
                    String author = rs.getString("Author");
                    String year = rs.getString("Year");
                    tableModel.addRow(new Object[]{bookID, title, author, year});
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Loading Books!");
            }
        }

        private void clearForm() {
            txtBookID.setText("");
            txtTitle.setText("");
            txtAuthor.setText("");
            txtYear.setText("");
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LibraryInventory().setVisible(true);
                }
            });
        }
    }