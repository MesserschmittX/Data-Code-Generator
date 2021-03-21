import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileHandler {


	public static void writeToFile(String data, String filePath, boolean append) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				boolean mkdir = Objects.requireNonNull(file.getParentFile()).mkdirs();
				boolean createFile = file.createNewFile();
				if (mkdir && createFile) {
					CodeGeneratorWindow.logCodeGen(file.getAbsolutePath() + " created", Color.YELLOW);
				}
			}
			FileOutputStream fileOutputStream = new FileOutputStream(file, append);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (Exception e) {
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
		}
	}
	

	public static String readFromFile(String filePath) {
		String data;
		try {
			// set up file and various readers
			InputStream inputStream = new FileInputStream(filePath);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String receiveString;
			StringBuilder stringBuilder = new StringBuilder();

			while ((receiveString = bufferedReader.readLine() ) != null) {
				stringBuilder.append(receiveString).append("\n");
			}
			inputStream.close();
			data = stringBuilder.toString();
		} catch (FileNotFoundException e) {
			CodeGeneratorWindow.logCodeGen("\nFileNotFoundException!"
							+ "\n" + filePath + " could not be found", Color.RED);
			
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
			return "";
		} catch (IOException e) {
			CodeGeneratorWindow.logCodeGen("\nIOException during File access\n", Color.RED);
			
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
			return "";
		} catch (Exception e) {
			CodeGeneratorWindow.logCodeGen("\nUnknown Exception for File access\n", Color.RED);
			
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
			return "";
		}
		return data;
	}
}
