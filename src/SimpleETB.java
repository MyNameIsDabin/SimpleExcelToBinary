import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

interface APP_INFO
{
	String TITLE = "SETB";
	String VERSION = "0.5"; 
}

public class SimpleETB
{
	private JFrame frame;
	private JButton exportButton;
	private JComboBox cBoxEncodingType;
	private DropTarget dropTarget;
	private BinaryConvertor binConvertor = new BinaryConvertor();
	private ExcelScanner excelScanner = new ExcelScanner();
	private HashMap<String, ArrayList<XlsxRowData>> mapListXlsxRowData = new HashMap<String, ArrayList<XlsxRowData>>();
	private boolean isLoadedExcelFile = false;

	private String[] encodingType = { "UTF-8", "UNICODE", "사용자설정" };
	
	public SimpleETB()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		
		initialize();
	}

	private void initialize()
	{		
		frame = new JFrame();
		frame.setBounds(100, 100, 273, 208);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("파일");
		menuBar.add(mnFile);
		
		cBoxEncodingType = new JComboBox(encodingType);
		cBoxEncodingType.setFocusable(false);
		cBoxEncodingType.setBounds(186, 1, 79, 21);
		frame.getContentPane().add(cBoxEncodingType);
		
		JLabel labelStartCell = new JLabel("첫번째 셀");
		labelStartCell.setBounds(8, 4, 75, 15);
		frame.getContentPane().add(labelStartCell);
		
		JLabel labelRow = new JLabel("행:");
		labelRow.setEnabled(false);
		labelRow.setBounds(69, 4, 75, 15);
		frame.getContentPane().add(labelRow);
		
		JTextField textFieldRow = new JTextField();
		textFieldRow.setText("2");
		textFieldRow.setBounds(88, 1, 32, 21);
		textFieldRow.setColumns(10);
		textFieldRow.setEnabled(true);
		frame.getContentPane().add(textFieldRow);
		
		JLabel labelColumn = new JLabel("열:");
		labelColumn.setEnabled(false);
		labelColumn.setBounds(125, 4, 22, 15);
		frame.getContentPane().add(labelColumn);
		
		JTextField textFieldColumn = new JTextField();
		textFieldColumn.setText("A");
		textFieldColumn.setColumns(10);
		textFieldColumn.setBounds(144, 1, 32, 21);
		frame.getContentPane().add(textFieldColumn);
		
		JMenuItem mntmImportExcel = new JMenuItem("엑셀파일 가져오기(.xlsx)");
		mntmImportExcel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				JFileChooser jfChooser = new JFileChooser();
				SwingUtilities.updateComponentTreeUI(jfChooser);
				
				jfChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				if (jfChooser.showDialog(frame, null) != 0)
				{
					return;
				}
								
				mapListXlsxRowData.clear();
				//엑셀파일 스캔 후 데이터 mapListXlsxRowData에 저장
				int resultCode = excelScanner.scanXlsx(mapListXlsxRowData, jfChooser.getSelectedFile(),
						Integer.parseInt(textFieldRow.getText()), textFieldColumn.getText());
				if (resultCode == 0)
				{
					loadSuccess();
				}
				else
				{
					loadFailure();
					
					if (resultCode == 1)
						JOptionPane.showMessageDialog(frame, "불러오던 중 에러가 발생했습니다", "알림", JOptionPane.PLAIN_MESSAGE);
					else if (resultCode == 2)
						JOptionPane.showMessageDialog(frame, "해당 파일은 지원하지않습니다", "알림", JOptionPane.PLAIN_MESSAGE);
				}
				
			}
		});
		mnFile.add(mntmImportExcel);
		
		JMenuItem menuItem = new JMenuItem("바이너리로 추출하기");
		menuItem.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				//바이너리 파일로 추출한다
				if (isLoadedExcelFile)
				{
					binConvertor.convertAndExport(mapListXlsxRowData, cBoxEncodingType.getSelectedItem().toString());
					
					JOptionPane.showMessageDialog(frame, "성공", "알림", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		mnFile.add(menuItem);
		frame.getContentPane().setLayout(null);
		
		exportButton = new JButton("엑셀파일을 가져와라(.xlsx)");
		exportButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				//바이너리 파일로 추출한다
				if (isLoadedExcelFile)
				{
					binConvertor.convertAndExport(mapListXlsxRowData, cBoxEncodingType.getSelectedItem().toString());

					JOptionPane.showMessageDialog(frame, "성공", "알림", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});		
		exportButton.setFocusable(false);
		exportButton.setBounds(0, 23, 267, 136);
		exportButton.setEnabled(false);
		frame.getContentPane().add(exportButton);
		
		dropTarget = new DropTarget(exportButton, new DropTargetListener()
				{
					@Override
					public void drop(DropTargetDropEvent dtde)
					{
						try
						{
							dtde.acceptDrop(DnDConstants.ACTION_COPY);
							List<File> droppedFiles = (List<File>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			                
							mapListXlsxRowData.clear();
							//엑셀파일 스캔 후 데이터 mapListXlsxRowData에 저장
							int resultCode = excelScanner.scanXlsx(mapListXlsxRowData, droppedFiles.get(0), 
									Integer.parseInt(textFieldRow.getText()), textFieldColumn.getText());
							
							if (resultCode == 0)
							{
								loadSuccess();
							}
							else
							{
								loadFailure();
								
								if (resultCode == 1)
									JOptionPane.showMessageDialog(frame, "불러오던 중 에러가 발생했습니다", "알림", JOptionPane.PLAIN_MESSAGE);
								else if (resultCode == 2)
									JOptionPane.showMessageDialog(frame, "해당 파일은 지원하지않습니다", "알림", JOptionPane.PLAIN_MESSAGE);
							}
						  }
						  catch (Exception e)
						  {
						      e.printStackTrace();
						  }
					}
					@Override
					public void dragEnter(DropTargetDragEvent dtde) {}
					@Override
					public void dragExit(DropTargetEvent dte) {}
					@Override
					public void dragOver(DropTargetDragEvent dtde) {}
					@Override
					public void dropActionChanged(DropTargetDragEvent dtde) {}
				});
			}
	
	public void loadFailure()
	{		
		isLoadedExcelFile = false;
		exportButton.setEnabled(false);
		exportButton.setText("엑셀파일을 가져와라(.xlsx)");
	}

	public void loadSuccess()
	{
		isLoadedExcelFile = true;
		exportButton.setEnabled(true);
		exportButton.setText("바이너리로 추출하기");
	}
	
	//--------------------------------------
	//Getter&Setter
	//--------------------------------------
	
	public JFrame getFrame()
	{
		return frame;
	}
}
