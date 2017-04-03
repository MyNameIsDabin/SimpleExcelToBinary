import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;

interface DATA_TYPE
{
	int STRING=0,UTF8=0,UNICODE=1,FLOAT=2,DOUBLE=3,INT32=4,INT64=5,BOOL=6; 
}

public class XlsxRowData
{
	final private int dataType;
	private ArrayList<Object> listRowData = new ArrayList<Object>();
	
	public XlsxRowData(String cellType)
	{
		String type = cellType.toUpperCase();
		
		if (type.indexOf("STRING") == 0 || type.indexOf("UTF8") == 0)
		{
			dataType = DATA_TYPE.STRING;
		}
		else if (type.indexOf("UNICODE") == 0)
		{
			dataType = DATA_TYPE.UNICODE;
		}
		else if (type.indexOf("FLOAT") == 0)
		{
			dataType = DATA_TYPE.FLOAT;
		}
		else if (type.indexOf("DOUBLE") == 0)
		{
			dataType = DATA_TYPE.DOUBLE;
		}
		else if (type.indexOf("INT32") == 0)
		{
			dataType = DATA_TYPE.INT32;
		}
		else if (type.indexOf("INT64") == 0)
		{
			dataType = DATA_TYPE.INT64;
		}
		else if (type.indexOf("BOOL") == 0)
		{
			dataType = DATA_TYPE.BOOL;
		}
		else
		{
			//타입을 찾지 못하면 무조건 문자열로 판단
			dataType = DATA_TYPE.STRING;
		}
	}
	
	public void pushRowData(XSSFCell value)
	{		
		if (dataType == DATA_TYPE.STRING
				|| dataType == DATA_TYPE.UTF8
				|| dataType == DATA_TYPE.UNICODE)
		{
			listRowData.add(value.getStringCellValue());
		}
		else if (dataType == DATA_TYPE.INT32)
		{
			listRowData.add(new Double(value.getNumericCellValue()).intValue());
		}
		else if (dataType == DATA_TYPE.INT64)
		{
			listRowData.add(new Double(value.getNumericCellValue()).longValue());
		}
		else if (dataType == DATA_TYPE.FLOAT)
		{
			listRowData.add(new Double(value.getNumericCellValue()).floatValue());
		}
		else if (dataType == DATA_TYPE.DOUBLE)
		{
			listRowData.add(value.getNumericCellValue());
		}
		else if (dataType == DATA_TYPE.BOOL)
		{
			listRowData.add(value.getBooleanCellValue());
		}
	}
	
	public final int getDataType()
	{
		return this.dataType;
	}
	
	public ArrayList<Object> getListRowData()
	{
		return listRowData; 
	}
	
	public Object getListRowData(int idx)
	{
		return listRowData.get(idx);
	}
}
