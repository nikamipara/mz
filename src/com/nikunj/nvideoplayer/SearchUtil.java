
package com.nikunj.nvideoplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.database.CursorJoiner.Result;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.util.Log;

public class SearchUtil {

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    public static ArrayList<String> searchText(File file, String searchText,
            ArrayList<String> result) {
    	/*searchText=searchText.toLowerCase();*/
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        StringBuffer buffer = new StringBuffer();
        try {
            while (br != null && (line = br.readLine()) != null) {
                buffer.delete(0, buffer.length());
                boolean hasString = false;
                int skipSubNumber = 2; // Do not search sub number and time
                do {
                    if (line.isEmpty()) {
                        break;
                    }
                    buffer.append(line).append("\n");
                    skipSubNumber--;
                   /* line =line.toLowerCase();*/
                    if (searchinline(line,searchText)&& skipSubNumber < 0) {
                        hasString = true;
                    }
                } while ((line = br.readLine()) != null);

                if (hasString) {
                    result.add(clean(buffer.toString()));
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }
	public static boolean searchinline(String line, String searchText) {
		if (null == line || null == searchText)
			return false;
		line = line.toLowerCase();
		searchText = searchText.toLowerCase().trim();
		if (line.contains(" " + searchText))
			return true;
		if (line.startsWith(searchText))
			return true;
		int i = line.indexOf(searchText);
		if(i!= -1 && line.charAt(i-1)=='>')
			return true;
		return false;
	}
	private static String clean(String string) {
		String regularExpression="<.?>";
		string = string.replaceAll(regularExpression, " ");
		regularExpression="</.?>";
		string = string.replaceAll(regularExpression, " ");
		return string;
	}

	/*public void searchFiles() {
        String path;
        path = "E:\\EclipseWorkspace_older\\SearchString\\bin\\searchstring";
        File f = new File(path);

        ThreadPoolExecutor t = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 1,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        if (f != null && f.isDirectory()) {

            File[] list = f.listFiles();

            for (File searchFile : list) {
                if (searchFile.isDirectory()) {
                    continue;
                }
                String searchString = "tingling";
                t.execute(new SearchThread(searchFile, searchString));
            }
            t.shutdown();
        }

        System.out.println("Main finished " + NUMBER_OF_CORES);
    }*/

    public static FileFilter txtFilter() {

        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile()
                        && (pathname.getAbsolutePath().endsWith(".sub") || pathname
                                .getAbsolutePath().endsWith(".srt"))) {
                    return true;
                }
                return false;
            }
        };

        return filter;
    }
    public static ArrayList<File> result;
    public static ArrayList<File> searchallfiles(ArrayList<File> filelist, String query){
		
		result = new ArrayList<File>();

		// ArrayList<File> srtfiles = getsrtarray(filelist);

		ThreadPoolExecutor t = new ThreadPoolExecutor(NUMBER_OF_CORES,
				NUMBER_OF_CORES, 1, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		if (filelist == null || filelist.isEmpty())
			return null;
		for (File searchFile : filelist) {
			if (searchFile.isDirectory()) {
				continue;
			}
			/*File srt = getsrt(searchFile);
			if (srt != null && srt.exists()) {*/
				t.execute(new SearchThread(searchFile, query));
				System.out.println("searching for "+query+" in file "+searchFile.getName());
			//}
		}
		t.shutdown();
		try {
			t.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println("Something went wrong while waiting...");
		}
		System.out.println("Main finished " + NUMBER_OF_CORES);
		StringBuffer s = new StringBuffer("searching for" + query);
		for (File f : result) {
			s.append(f.getAbsolutePath() + "     ");
		}
		System.out.println("Main finished returning " + s.toString());
		return result;
    	
    }

	public static File getsrt(File f) {
		String s = f.getAbsolutePath();
		s= s.substring(0,s.lastIndexOf("."))+".srt";
		//System.out.println("nikunj srt path"+s);
		File fsrt  = new File(s);
		return fsrt;
		
	}
	public static Uri converttoUri(URI sourceURI) {
		return new Uri.Builder().scheme(sourceURI.getScheme())
				.encodedAuthority(sourceURI.getRawAuthority())
				.encodedPath(sourceURI.getRawPath())
				.query(sourceURI.getRawQuery())
				.fragment(sourceURI.getRawFragment()).build();
	}
	/*private static ArrayList<File> getsrtarray(ArrayList<File> filelist) {
		ArrayList<File> resultt = new ArrayList<File>();
		for(File f :filelist){
			String s = f.getAbsolutePath();
			s= s.substring(0,s.lastIndexOf("."))+".srt";
			System.out.println("nikunj srt path"+s);
			File fsrt  = new File(s);
			if(fsrt!=null) resultt.add(fsrt);
		}
		return resultt;
	}*/
}

class SearchThread implements Runnable {
    private File file;

    private String searchText;

	private File filevideo ;

    private static int count = 0;

    SearchThread(File file, String text) {
        this.filevideo = file;
    	this.file = SearchUtil.getsrt(file);
        this.searchText = text;
        // System.out.println(file.getName()+ "  " + count++);
    }
	@Override
    public void run() {
    	System.out.println("search for" + ": " + file.getName() + ": started  key work "+searchText);
        if (searchText()) {
            SearchUtil.result.add(filevideo);
        	/*System.out.println(this.searchText.toString() + " found in: " + this.file.getName());*/
            
        }
        System.out.println(this.toString() + ": " + file.getName() + ": finished ");
    }
	private boolean searchText() {
		if (this.file.exists() && this.file.length() != 0) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String line;
			boolean found = false;
			try {
				while (br != null && (line = br.readLine()) != null) {
					/*line = line.toLowerCase();*/
					if (SearchUtil.searchinline(line, searchText)){
						found = true;
						System.out.println("found in line "+line);
						System.out.println(this.searchText.toString() + " found in: " + this.file.getName());
						break;
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return found;
		}
		return false;
	}
    
}
