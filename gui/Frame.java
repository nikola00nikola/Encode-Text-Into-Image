package gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import converter.Converter;

public class Frame extends JFrame{

	final static int W=400, H=400;
	private int maxLen=0;
	private File file=null;
	private Converter converter;
	private String filename=null;
	private int wS;
	private Frame() {
		config();
		file = null;
		converter= new Converter();
		this.setVisible(true);
	}
	
	private void config() {
		wS = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		this.setBounds(wS/2-W/2, 100, W, H);
		JTextArea jt=new JTextArea();
		JScrollPane scroll = new JScrollPane(jt);
		this.add(scroll, BorderLayout.CENTER);
		JButton button1=new JButton("Write");
		JButton button2=new JButton("Read");
		JPanel down=new JPanel();
		down.add(button1);
		down.add(button2);
		button1.setEnabled(false);
		button2.setEnabled(false);
		JLabel label=new JLabel();
		this.add(label, BorderLayout.NORTH);
		jt.setDropTarget(new DropTarget() {
		    public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		        	//if(file!=null)
		        		//return;
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            List<File> droppedFiles = (List<File>)
		                evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            for (File f : droppedFiles) {
		                String path = f.getPath();
		                if(!path.substring(path.length()-4).equals(".bmp")) {
		                	label.setText("Error: Image format is not .bmp");
		                	converter.clearFile();
		                	button1.setEnabled(false);
		                	button2.setEnabled(false);
		                	return;
		                }
		                int val=converter.setFile(Files.readAllBytes(f.toPath()));
		                if (val<0) {
		                	converter.clearFile();
		                	button1.setEnabled(false);
		                	button2.setEnabled(false);
		                }
		                if(val==-1) {
		                	label.setText("Error: Image is compressed");
		                	return;
		                }
		                if(val==-2) {
		                	label.setText("Error: Bits Per Pixel != 24 ");
		                	return;
		                }
		                for(int i=path.length()-1; i>=0; i--) {
		                	if(path.charAt(i)=='\\') {
		                		filename=path.substring(i+1);
		                		break;
		                	}
		                }
		                maxLen=val;
		                file=f;
		                label.setText(filename+"       Text maximum length: "+maxLen);
		                button1.setEnabled(true);
		                button2.setEnabled(true);
		                //System.out.println("OK");
		            }
		        } catch (Exception ex) {
		            ex.printStackTrace();
		            label.setText("Greska");
		        }
		    }
		});
		button1.addActionListener((ae)->{
			JDialog dialog = new JDialog(Frame.this, true);
			dialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					dialog.dispose();
				}
			});
			dialog.setBounds(wS/2-W/4, 110, W/2, H/2);

			if(jt.getText().length() > maxLen) {
				dialog.add(new JLabel("Error: Text length is too big ("+jt.getText().length()+")"), BorderLayout.CENTER);
			}else {
				label.setText(filename+"       Text maximum length: "+maxLen);
				converter.writeMessage(jt.getText().toCharArray(), file.getPath());
				dialog.add(new JLabel("Message is succesfully written into image, len = "+jt.getText().length()), BorderLayout.CENTER);
			}
			dialog.pack();
			dialog.setVisible(true);
		});
		
		button2.addActionListener((ae)->{
			label.setText(filename+"       Text maximum length: "+maxLen);
			JDialog dialog = new JDialog(Frame.this, "Message", true);
			JTextArea tt=new JTextArea(converter.readMessage());
			tt.setEditable(false);
			dialog.add(new JScrollPane(tt), BorderLayout.CENTER);
			dialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					dialog.dispose();
				}
			});
			dialog.setBounds(wS/2-W/2, 90, W, H);
			dialog.setVisible(true);
			
		});
		this.add(down, BorderLayout.SOUTH);
		
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				Frame.this.dispose();
			}
		});
	}
	
	public static void main(String[] args) {
		new Frame();
	}
}
