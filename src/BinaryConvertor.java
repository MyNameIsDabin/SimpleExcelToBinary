import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import common.util.FileUtil;
import common.util.LittleEndianOutputStream;

public class BinaryConvertor
{
	public void convertAndExport(HashMap<String, ArrayList<XlsxRowData>> mapListXlsxRowData, String encodingType)
	{
		Iterator<String> iter = mapListXlsxRowData.keySet().iterator();
		
		while(iter.hasNext())
		{
			String filePath = iter.next();
			//String sheetName = iter.next();
			
			ArrayList<XlsxRowData> listXlsxRowData = mapListXlsxRowData.get(filePath);
				
			try
			{
				String binFilePath = FileUtil.changeExtension(filePath, "bin");
				
				File binFile = new File(binFilePath);
				FileOutputStream out = new FileOutputStream(binFile);
				FilterOutputStream filterOut = new FilterOutputStream(out);
				LittleEndianOutputStream dataOutStream = new LittleEndianOutputStream(filterOut);
				
				for(int i=0; i<listXlsxRowData.get(0).getListRowData().size(); i++)
				{
					for(int j=0; j<listXlsxRowData.size(); j++)
					{
						XlsxRowData xlsxRowData = listXlsxRowData.get(j);
					
						int dataType = xlsxRowData.getDataType();
						Object dataValue = xlsxRowData.getListRowData(i);
						
						if (dataType == DATA_TYPE.STRING)
						{
							String value = (String)dataValue;
							
							if (encodingType.compareTo("UTF-8") == 0 ||
									encodingType.compareTo("사용자설정") == 0)
							{
								dataOutStream.writeUTF(value); // writeUTF(value);
							}
							else if (encodingType.compareTo("UNICODE") == 0)
							{
								dataOutStream.writeChar(value.getBytes().length*2);
								dataOutStream.writeChars(value);
							}
						}
						else if  (dataType == DATA_TYPE.UNICODE)
						{
							String value = (String)dataValue;
							dataOutStream.writeChar(value.getBytes().length);
							dataOutStream.writeChars(value);
						}	
						else if (dataType == DATA_TYPE.INT32)
						{
							dataOutStream.writeInt((int)dataValue);
						}
						else if (dataType == DATA_TYPE.INT64)
						{
							dataOutStream.writeLong((long)dataValue);
						}
						else if (dataType == DATA_TYPE.FLOAT)
						{
							dataOutStream.writeFloat((float)dataValue);
						}
						else if (dataType == DATA_TYPE.DOUBLE)
						{
							dataOutStream.writeDouble((double)dataValue);
						}
						else if (dataType == DATA_TYPE.BOOL)
						{
							dataOutStream.writeBoolean((boolean)dataValue);
						}
						
						//System.out.println(dataValue);
					}
				}
				
				/* 첫번째에 파일 사이즈를 저장해둔다 -> 필요 없는 데이터로 판단해서 제거
				randomAccessStream.seek(0);
				ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				buffer.putInt(dataOutStream.size());
				randomAccessStream.writeInt(buffer.getInt());
				randomAccessStream.close();
				*/
				
				dataOutStream.close();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
