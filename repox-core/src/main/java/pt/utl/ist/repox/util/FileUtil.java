package pt.utl.ist.repox.util;

import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.util.*;

public class FileUtil {

	public static String sanitizeToValidFilename(String name) {
		String[] invalidSymbols = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
		String sanitizedName = name;
				
		for (String currentSymbol : invalidSymbols) {
			sanitizedName = sanitizedName.replaceAll("[\\" + currentSymbol + "]", "_");
		}
		return sanitizedName;
	}

	
	public static File[]  getChangedFiles(Date fromDate, File[] files) {
		List<File> changedFilesList = getChangedFilesList(fromDate, files);
		File[] changedFiles = new File[changedFilesList.size()];
        changedFilesList.toArray(changedFiles);
        //Arrays.sort(changedFiles, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
        Arrays.sort(changedFiles, new Comparator<File>() {
            public int compare(File file1, File file2) {
                if(file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase()) < 0)
                    return -1;
                else if(file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase()) > 0)
                    return 1;
                return 0;
            }
        });

		return changedFiles;
	}

	public static List<File> getChangedFilesList(Date fromDate, File[] files) {
		List<File> changedFiles = new ArrayList<File>();

		if(files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					changedFiles.addAll(getChangedFilesList(fromDate, file.listFiles()));
				}
				else if(isFileChanged(fromDate, file)) {
					changedFiles.add(file);
				}
			}
		}

		return changedFiles;
	}

	public static boolean isFileChanged(Date fromDate, File file) {
        return fromDate == null || file.lastModified() > fromDate.getTime();
	}
	
}
