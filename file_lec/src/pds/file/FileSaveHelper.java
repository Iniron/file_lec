package pds.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class FileSaveHelper {
	
	private static Random random = new Random();
	
	//----- 현재시간과 랜덤값을 이용하여 파일을 생성한다. -----
	
	public static String save(String directory, InputStream is) throws IOException{
		long currentTime = System.currentTimeMillis();//현재시간저장
		int randomValue = random.nextInt(50); //랜덤값 생성
		//랜덤값과 현재시간값을 fileName에 저장
		String fileName = Long.toString(currentTime)+"_"+Integer.toString(randomValue);
		
		//인자로 받은 디렉토리에 "현재시간_랜덤"으로 된 이름의 파일을 생성
		File file = new File(directory, fileName);
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			byte[] data = new byte[8096];
			int len = -1;
			while((len=is.read())!=-1){	//input으로부터 파일입력
				os.write(data, 0, len); //파일을 ouput에 출력
			}
		} finally {
			if(os!=null)
				try {os.close();} catch (Exception e) {}
		}
		return file.getAbsolutePath();	//file의 절대 경로를 리턴
	}
}
