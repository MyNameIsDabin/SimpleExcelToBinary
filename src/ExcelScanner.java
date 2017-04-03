
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import common.util.FileUtil;

public class ExcelScanner
{
	final private String[] SUPPORT_EXTENSION = { "xlsx" };
	
	private ArrayList<File> listFile = new ArrayList<File>();
	private int startCellRow;
	private int startCellColumn;
	
	public int scanXlsx(HashMap<String, ArrayList<XlsxRowData>> mapListXlsxRowData, File parentFile, int startRow, String startColumn)
	{
		startCellRow = startRow-1;
		startCellColumn = ComlumnTextToInt(startColumn)-1;
		int errorCode = 0;
		boolean isNotSupport = true;
	
		listFile.clear();
		FileUtil.visitAllDirectory(listFile, parentFile);
		
		for(File file : listFile)
		{
			if (FileUtil.isBadExtension(file, SUPPORT_EXTENSION))
			{
				isNotSupport = false;
				
				int sheet=0;
				int row=startCellRow;
				int column=startCellColumn;
				
				try
				{
					FileInputStream excelFile = new FileInputStream(file);
					XSSFWorkbook workbook=new XSSFWorkbook(excelFile);
					
					for(sheet = 0; sheet<workbook.getNumberOfSheets(); sheet++)
					{
						XSSFSheet sheetData = workbook.getSheetAt(sheet);
						String sheetName = sheetData.getSheetName();
						
						ArrayList<XlsxRowData> listXlsxRowData = new ArrayList<XlsxRowData>();
						mapListXlsxRowData.put(file.getParent()+"\\"+ sheetName+".bin", listXlsxRowData);
						
						//System.out.println(file.getName() + "(시트:" + sheetName +")");
						
						for(row=startCellRow; row<sheetData.getPhysicalNumberOfRows(); row++)
						{
							XSSFRow rowData = sheetData.getRow(row);
							XlsxRowData xlsxRowData = null;
							
							if (rowData == null)
								continue;
							
							for(column=startCellColumn; column<rowData.getPhysicalNumberOfCells(); column++)
							{
								XSSFCell cellData = rowData.getCell(column);
								
								if (cellData == null || isCellEmpty(cellData))
									continue;
								
								if (row == startCellRow)
								{
									xlsxRowData = new XlsxRowData(cellData.getStringCellValue());
									listXlsxRowData.add(xlsxRowData);
								}
								else
								{
									listXlsxRowData.get(column).pushRowData(cellData);
								}
							}
						}
					}
					
					workbook.close();
				}
				catch (IOException e1)
				{
					errorCode = 1;
					JOptionPane.showMessageDialog(ProgramMain.window.getFrame(),
							file.getName()+"의"+ +row+"행 " +IntToColumnText(column)+ "열 에서 오류발생", "오류", JOptionPane.PLAIN_MESSAGE);
					
					e1.printStackTrace();
				}
			}
		}
		
		if (isNotSupport)
		{
			errorCode = 2;
		}
		
		return errorCode;
	}
	
	public boolean isCellEmpty(XSSFCell cell)
	{
		if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK || cell.getCellType() == XSSFCell.CELL_TYPE_ERROR)
		{
			return true;
		}
		
		if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
		{
			if (cell.getStringCellValue().isEmpty())
			{
				return false;
			}
		}
			
		return false;
	}
	
	public int ComlumnTextToInt(String ColumnNumber)
	{
		int resultNum = 0;
		char[] charArr = ColumnNumber.toCharArray();
		
		for(char c : charArr)
		{
			resultNum += resultNum*26 + c - 'A' + 1;
		}
		
		return resultNum;
	}
	
	public String IntToColumnText(int number)
	{
		String columnString = "";
	    int columnNumber = number;
	    
	    while (columnNumber > 0)
	    {
	    	int currentLetterNumber = (columnNumber - 1) % 26;
	    	char currentLetter = (char)(currentLetterNumber + 65);
	    	columnString = currentLetter + columnString;
	    	columnNumber = (columnNumber - (currentLetterNumber + 1)) / 26;
	    }
	    
	    return columnString;
	}
}
