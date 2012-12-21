package cn.pipi.words.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TestChineseWordProofread {

	/**
	 * just a unit test
	 */
	public static void main(String[] args) {
		String argu1 = "movie.txt";          //movies name file
		String argu2 = "movie_result.txt";   //words after segmenting name of all movies
		
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
		String startInitTime = sdf.format(new java.util.Date()); 
		System.out.println(startInitTime+" ---start initializing work---");
		ChineseWordProofread cwp = new ChineseWordProofread(argu1,argu2);
		String endInitTime = sdf.format(new java.util.Date());
		System.out.println(endInitTime+" ---end initializing work---");
		
		BufferedReader br = null;
		try {
			 br = new BufferedReader(new FileReader(new File("unitTestMovieList.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int totalLineNum = 0 ;
		int positiveNum = 0 ;
		String line = null;
		List<String> result = new ArrayList<String>();
		Calendar startProcess = Calendar.getInstance();
		try {
			while ((line=br.readLine()) != null){
				totalLineNum += 1 ;
				String[] line_gbk = line.trim().split("\t");
				String errorName = line_gbk[1];
				String normalName = line_gbk[0];
				result.clear();
				result.addAll(cwp.proofreadAndSuggest(errorName));
				if (result.contains(normalName)) positiveNum ++ ;
				else{
					System.out.println("suggest: "+result.toString()+"; but normalName: "+normalName);
				}
			}
			br.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		Calendar endProcess = Calendar.getInstance();
		long elapsetime = (endProcess.getTimeInMillis()-startProcess.getTimeInMillis()) ;
		System.out.println("process work elapsed "+elapsetime+" ms");
		
		System.out.println("-------total forcast result-------");
		float ratio = (float) ((1.0*positiveNum/totalLineNum)*100) ;
		System.out.println("the ratio of shot is : "+positiveNum+"/"+totalLineNum+" = "+ratio+"%");

	}

}
