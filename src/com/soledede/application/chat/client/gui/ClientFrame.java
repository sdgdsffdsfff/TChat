package com.soledede.application.chat.client.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.soledede.application.chat.client.action.MessageAction;


public class ClientFrame extends JFrame {
  JPanel contentPane;

  JPanel jPanel1 = new JPanel();

  JLabel userLabel = new JLabel();
  JTextField userValue = new JTextField();
  JLabel jLabel1 = new JLabel();
  JButton connectButton = new JButton();

  //Construct the frame
  public ClientFrame() {

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();

      //以下参数可以在用户注册后自动获取。
      String url = "220.112.110.61";
      int port = 81;
//      NonBlockingSocket nonBlockingSocket = new NonBlockingSocket(url, port);
//      nonBlockingSocket.setDaemon(true);
//W      nonBlockingSocket.start();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();

    //  contentPane.setLayout(null);
    this.setSize(new Dimension(334, 199));
    this.setTitle("J道聊天系统");
    userLabel.setFont(new java.awt.Font("Dialog", 1, 15));
    userLabel.setPreferredSize(new Dimension(65, 19));
    userLabel.setText("用户名");
    userLabel.setBounds(new Rectangle(53, 79, 81, 19));
    userValue.setBounds(new Rectangle(136, 75, 81, 25));
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 16));
    jLabel1.setToolTipText("");
    jLabel1.setText("J道高性能Java聊天系统");
    jLabel1.setBounds(new Rectangle(62, 22, 191, 14));
    connectButton.setBounds(new Rectangle(112, 116, 83, 29));
    connectButton.setText("连接");
    connectButton.addActionListener(new ClientFrame_connectButton_actionAdapter(this));

    jPanel1.add(userValue, null);
    jPanel1.add(connectButton, null);
    jPanel1.add(userLabel, null);
    jPanel1.add(jLabel1, null);
    jPanel1.setLayout(null);

    contentPane.add(jPanel1, null);

  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }


  void connectButton_actionPerformed(ActionEvent e) {

    MessageAction.send(userValue.getText());

    ChatPanel chatPanel = new ChatPanel();
    contentPane.remove(jPanel1);
    contentPane.add(chatPanel, BorderLayout.CENTER);
    setVisible(true);

  }
}

class ClientFrame_connectButton_actionAdapter implements java.awt.event.
    ActionListener {
  ClientFrame adaptee;

  ClientFrame_connectButton_actionAdapter(ClientFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.connectButton_actionPerformed(e);
  }
}