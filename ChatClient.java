import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient extends JFrame implements Runnable {
    Socket socket;
    JTextArea ta;
    Thread thread;
    DataInputStream din;
    DataOutputStream dout;
    String LoginName;
    JButton send, logout;
    JTextField tf;



    ChatClient(String login) throws UnknownHostException, IOException{
        super(login);
        LoginName = login;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    dout.writeUTF(LoginName + " " + "LOGOUT");
                    System.exit(1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        ta = new JTextArea(18,15);
        tf = new JTextField(50);

        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        if(tf.getText().length() > 0){
                            dout.writeUTF(LoginName + " " + "DATA " + tf.getText().toString());
                            tf.setText("");
                        }

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        send = new JButton("Send");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(tf.getText().length() > 0){
                        dout.writeUTF(LoginName + " " + "DATA " + tf.getText().toString());
                        tf.setText("");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        logout = new JButton("Logout");
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dout.writeUTF(LoginName + " " + "LOGOUT");
                    System.exit(1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });

        socket = new Socket("localhost", 5217);
        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());

        dout.writeUTF(LoginName);
        dout.writeUTF(LoginName + " " + "LOGIN");

        thread = new Thread(this);
        thread.start();

        setup();

    }

    private void setup() {
        setSize(600, 400);
        JPanel jPanel = new JPanel();
        jPanel.add(new JScrollPane(ta));
        jPanel.add(tf);
        jPanel.add(send);
        jPanel.add(logout);
        add(jPanel);
        setVisible(true);
    }

    @Override
    public void run() {
        while(true){
            try{
                ta.append("\n" + din.readUTF());
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
