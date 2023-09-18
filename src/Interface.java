import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Objects;

public class Interface {
    private static JTextArea editorArea;
    private static JTextArea messageArea;
    private static JLabel statusLabel;
    private static File currentFile = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Minha Interface Java");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(910, 600);
        frame.setMinimumSize(new Dimension(910, 600));
        frame.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setMinimumSize(new Dimension(910, 70));
        toolBar.setFloatable(false);

        JButton novoButton = createButton("Novo [ctrl-n]", "icons/new.png", "Novo [ctrl-n]");
        novoButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_DOWN_MASK),"novoButton");
        novoButton.getActionMap().put("novoButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                novoButton.doClick();
            }
        });
        novoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparEditor();
                limparMessageArea();
                limparStatusBar();
            }
        });
        toolBar.add(novoButton);

        JButton abrir = createButton("Abrir [ctrl-o]", "./icons/open.png", "Abrir [ctrl-o]");
        abrir.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "abrirButton");
        abrir.getActionMap().put("abrirButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrir.doClick();
            }
        });
        abrir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirArquivo();
            }
        });
        toolBar.add(abrir);

        JButton salvar = createButton("Salvar [ctrl-s]", "./icons/save.png", "Salvar [ctrl-s]");
        salvar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "salvarButton");
        salvar.getActionMap().put("salvarButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvar.doClick();
            }
        });
        salvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile == null) {
                    salvarComo();
                } else {
                    salvarArquivo(currentFile);
                }
            }
        });
        toolBar.add(salvar);


        JButton copiar = createButton("Copiar [ctrl-c]", "./icons/copy.png", "Copiar [ctrl-c]");
        copiar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "copiarButton");
        copiar.getActionMap().put("copiarButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copiarTexto();
            }
        });
        copiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copiarTexto();
            }
        });
        toolBar.add(copiar);

        JButton colar = createButton("Colar [ctrl-v]", "./icons/paste.png", "Colar [ctrl-v]");
        colar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "colarButton");
        colar.getActionMap().put("colarButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colarTexto();
            }
        });
        colar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colarTexto();
            }
        });
        toolBar.add(colar);

        JButton recortar = createButton("Recortar [ctrl-x]", "./icons/cut.png", "Recortar [ctrl-x]");
        recortar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), "recortarButton");
        recortar.getActionMap().put("recortarButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recortarTexto();
            }
        });
        recortar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recortarTexto();
            }
        });
        toolBar.add(recortar);


        JButton compilar = createButton("Compilar [F7]", "./icons/compile.png", "Compilar [F7]");
        compilar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "compilarButton");
        compilar.getActionMap().put("compilarButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compilar.doClick();
            }
        });
        compilar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lexico lexico = new Lexico();
                lexico.setInput(editorArea.getText());
                String[] linhas = editorArea.getText().split("\n");

                try {
                    int linhaAtual = 0;
                    String formatacao = "%-15s%-30s%-15s";
                    String message = String.format(formatacao, "linha", "classe", "lexema") + "\n";
                    Token t = null;


                    while ( (t = lexico.nextToken()) != null ) {
                        linhaAtual = getLinha(linhas, t.getLexeme());
                        String classe = getClase(t);

                        if (classe.equals("pr_invalida")) {
                            throw new LexicalError("palavra reservada inválida", t.getPosition());
                        }

                        //message += linhaAtual + " - " + classe + " - " + t.getLexeme() + "\n";
                        message += String.format(formatacao, linhaAtual, classe, t.getLexeme()) + "\n";


                        // só escreve o lexema, necessário escrever t.getId (), t.getPosition()

                        // t.getId () - retorna o identificador da classe. Olhar Constants.java e adaptar, pois
                        // deve ser apresentada a classe por extenso
                        // t.getPosition () - retorna a posição inicial do lexema no editor, necessário adaptar
                        // para mostrar a linha

                        // esse código apresenta os tokens enquanto não ocorrer erro
                        // no entanto, os tokens devem ser apresentados SÓ se não ocorrer erro, necessário adaptar
                        // para atender o que foi solicitado
                    }
                    if (editorArea.getText() != null) {
                        message += "\n" + String.format(formatacao,"","programa compilado com sucesso", "");
                        messageArea.setText(message);
                        System.out.println(message);
                    }
                }
                catch ( LexicalError ex ) {  // tratamento de erros
                    String response = "";
                    String caracter = String.valueOf(editorArea.getText()
                            .substring(ex.getPosition())
                            .charAt(0));
                    int linha = getLinha(linhas, caracter);

                    if (ex.getMessage().equals("símbolo inválido")) {
                        response = "linha " + linha + ": "+ caracter + " " + ex.getMessage();
                    } else if (ex.getMessage().equals("palavra reservada inválida")) {
                        String sequencia = String.valueOf(editorArea.getText()
                                .substring(ex.getPosition())
                                .split("[^a-z]+")[0]);

                        response = "linha " + linha + ": "+ sequencia + " " + ex.getMessage();
                    } else if (ex.getMessage().equals("identificador inválido")) {
                        String sequencia = editorArea.getText()
                                .substring(ex.getPosition())
                                .split(" ")[0];

                        response = "linha " + linha + ": "+ sequencia + " " + ex.getMessage();
                    } else {
                        response = "linha " + linha + ": " + ex.getMessage();
                    }

                    System.out.println(response);
                    messageArea.setText(response);
                    // e.getMessage() - retorna a mensagem de erro de SCANNER_ERRO (olhar ScannerConstants.java
                    // e adaptar conforme o enunciado da parte 2)
                    // e.getPosition() - retorna a posição inicial do erro, tem que adaptar para mostrar a
                    // linha
                }
            }
        });
        toolBar.add(compilar);

        JButton equipe = createButton("Equipe [F1]", "./icons/team.png", "Equipe [F1]");
        equipe.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "equipeButton");
        equipe.getActionMap().put("equipeButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                equipe.doClick();
            }
        });
        equipe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirEquipe();
            }
        });
        toolBar.add(equipe);

        editorArea = new JTextArea();
        editorArea.setFont( new Font("Courier New", Font.PLAIN, 12));
        JScrollPane editorScrollPane = new JScrollPane(editorArea);
        editorArea.setBorder(new NumberedBorder());
        editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont( new Font("Courier New", Font.PLAIN, 12));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScrollPane, messageScrollPane);
        splitPane.setResizeWeight(0.85);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setMinimumSize(new Dimension(910, 25));
        statusLabel = new JLabel("Pasta: N/A | Arquivo: Nenhum");
        statusBar.add(statusLabel, BorderLayout.WEST);

        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static String getClase(Token t) {
        if (t.getId() >= 7 && t.getId() <= 17) {
            return "palavra reservada";
        } else if (t.getId() >= 18 && t.getId() <= 36) {
            return "símbolo especial";
        } else if (t.getId() == 2) {
            return "pr_invalida";
        } else if (t.getId() == 3) {
            return "identificador";
        } else if (t.getId() == 4) {
            return "constante_int";
        } else if (t.getId() == 5) {
            return "constante_float";
        } else if (t.getId() == 6) {
            return "constante_string";
        } else {
            return "";
        }
    }
    private static int getLinha (String[] linhas, String verify) {
        for (int i = 0; i < linhas.length; i++) {
            if (linhas[i].contains(verify)){
                return i+1;
            }
        }
        return -1;
    }

    private static JButton createButton(String text, String iconPath, String tooltip) {
        JButton button = new JButton();
        button.setText(text);
        button.setIcon(new ImageIcon(Objects.requireNonNull(iconPath)));
        button.setToolTipText(tooltip);
        return button;
    }

    private static void limparEditor() {
        editorArea.setText("");
    }

    private static void limparMessageArea() {
        messageArea.setText("");
    }

    private static void limparStatusBar() {
        statusLabel.setText("Pasta: N/A | Arquivo: Nenhum");
    }

    private static void abrirArquivo() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de Texto (.txt)", "txt");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            currentFile = selectedFile;

            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();

                editorArea.setText(content.toString());
                limparMessageArea();
                atualizarStatusBar(selectedFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void atualizarStatusBar(File file) {
        statusLabel.setText("Pasta: " + file.getParent() + " | Arquivo: " + file.getName());
    }

    private static void salvarComo() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de Texto (.txt)", "txt");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            salvarArquivo(selectedFile);
        }
    }

    private static void salvarArquivo(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(editorArea.getText());
            writer.close();

            currentFile = file;
            limparMessageArea();
            atualizarStatusBar(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void exibirMensagem(String mensagem) {
        messageArea.setText(mensagem);
    }

    private static void exibirEquipe() {
        String equipeInfo = "Equipe de Desenvolvimento:\n" +
                "Luigi G. Marchetti\n" +
                "Ari Elias da SIlva\n" +
                "Eduardo Zimmermann\n";

        exibirMensagem(equipeInfo);
    }

    private static void copiarTexto() {
        String selectedText = editorArea.getSelectedText();
        if (selectedText != null && !selectedText.isEmpty()) {
            StringSelection selection = new StringSelection(selectedText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        }
    }

    private static void colarTexto() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                editorArea.replaceSelection(clipboardText);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void recortarTexto() {
        String selectedText = editorArea.getSelectedText();
        if (selectedText != null && !selectedText.isEmpty()) {
            editorArea.replaceSelection("");
            StringSelection selection = new StringSelection(selectedText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        }
    }
}
