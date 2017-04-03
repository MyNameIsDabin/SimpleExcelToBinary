package common.util;
import java.io.File;
import java.util.ArrayList;

public class FileUtil
{
	public static void visitAllDirectory(ArrayList<File> files, File dir)
	{
        if(dir.isDirectory())
        {
            File[] children = dir.listFiles();
            
            for(File f : children)
            {
            	visitAllDirectory(files, f);
            }
        }
        else
        {
            files.add(dir);
        }
    }
	
	 public static boolean isBadExtension(File file, String[] badExtension)
	 {
        String fileName = file.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
        
        for (String mExt : badExtension)
        {
            if (ext.equalsIgnoreCase(mExt))
            {
            	return true;
            }
        }
        
        return false;
	 }
	 
	 public static String changeExtension(String fileName, String aftExt)
	 {
		 String befExt = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		 String extStr = fileName.replace(befExt, aftExt);
			
		 return extStr;
	 }
}
