package cn.pipi.words.util;

import ICTCLAS.kevin.zhang.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * proofread input words and suggest 
 *
 */

public class ChineseWordProofread {
	public static int hasError = 0;    // tag whether there is a spelling error nor not
	boolean isICTCLASFileProcessSucceed ;
	long totalTokensCount;                // the number of tokens
    Map<String,Integer> wordCountMap = null;   // the match between token and occurrence number
    List<String> movieName = null;             // movie names list   
    ICTCLAS2011 wordSeg = null;
    
    public ChineseWordProofread(String argu1,String argu2){
    	this.totalTokensCount = 0L;
    	this.wordSeg = initWordSegmentation();
    	this.isICTCLASFileProcessSucceed = wordSegmentate(argu1,argu2);
    	this.wordCountMap = calculateTokenCount(argu2);
    	this.movieName = cacheMovieName(argu1);
    }
    
    
    public ICTCLAS2011 initWordSegmentation(){
    	
    	ICTCLAS2011 wordSeg = new ICTCLAS2011();
    	try{
			String argu = "F:\\Java\\workspace\\wordProofread";
			System.out.println("ICTCLAS_Init");
			if (ICTCLAS2011.ICTCLAS_Init(argu.getBytes("GB2312"),0) == false)
			{
				System.out.println("Init Fail!");
				//return null;
			}

			/*
			 * 设置词性标注集
			        ID		    代表词性集 
					1			计算所一级标注集
					0			计算所二级标注集
					2			北大二级标注集
					3			北大一级标注集
			*/
			wordSeg.ICTCLAS_SetPOSmap(2);
			
		}catch (Exception ex){
			System.out.println("words segmentation initialization failed");
			System.exit(-1);
		}
    	return wordSeg;
    }
	
	public boolean wordSegmentate(String argu1,String argu2){
		boolean ictclasFileProcess = false;
		try{
			//文件分词
			ictclasFileProcess = wordSeg.ICTCLAS_FileProcess(argu1.getBytes("GB2312"), argu2.getBytes("GB2312"), 0);
			
			//ICTCLAS2011.ICTCLAS_Exit();
			
		}catch (Exception ex){
			System.out.println("file process segmentation failed");
			System.exit(-1);
		}
		return ictclasFileProcess;
	}

	public String[] wordSegmentate(String sInput){
		String nativeStr = null;
		try{
			//导入用户词典前
			//String sInput = "非诚勿扰，非常勿扰";
			byte nativeBytes[] = wordSeg.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 0);
			nativeStr = new String(nativeBytes, "GBK");

			System.out.println("未导入用户词典： " + nativeStr);
			
			ICTCLAS2011.ICTCLAS_Exit();

		}catch (Exception ex){
			System.out.println("file process segmentation failed");
			System.exit(-1);
		}
		//System.out.println(nativeStr);
		return nativeStr.trim().split(" ");	
	}
	
	public Map<String,Integer> calculateTokenCount(String afterWordSegFile){
		Map<String,Integer> wordCountMap = new HashMap<String,Integer>();
		File movieInfoFile = new File(afterWordSegFile);
		BufferedReader movieBR = null;
		try {
			movieBR = new BufferedReader(new FileReader(movieInfoFile));
		} catch (FileNotFoundException e) {
			System.out.println("movie_result.txt file not found");
			e.printStackTrace();
		}
		
		String wordsline = null;
		try {
			while ((wordsline=movieBR.readLine()) != null){
				String[] words = wordsline.trim().split(" ");
				for (int i=0;i<words.length;i++){
					int wordCount = wordCountMap.get(words[i])==null ? 0:wordCountMap.get(words[i]);
					wordCountMap.put(words[i], wordCount+1);
					totalTokensCount += 1;
					
					if (words.length > 1 && i < words.length-1){
						StringBuffer wordStrBuf = new StringBuffer();
						wordStrBuf.append(words[i]).append(words[i+1]);
						int wordStrCount = wordCountMap.get(wordStrBuf.toString())==null ? 0:wordCountMap.get(wordStrBuf.toString());
						wordCountMap.put(wordStrBuf.toString(), wordStrCount+1);
						totalTokensCount += 1;
					}
					
				}				
			}
		} catch (IOException e) {
			System.out.println("read movie_result.txt file failed");
			e.printStackTrace();
		}
		
		return wordCountMap;
	}
	
	public List<String> cacheMovieName(String movieTXT){
		List<String> movieName = new ArrayList<String>();
		File movieNameFile = new File(movieTXT);
		BufferedReader movieBR = null;
		try {
			movieBR = new BufferedReader(new FileReader(movieNameFile));
		} catch (FileNotFoundException e) {
			System.out.println("movie.txt file not found");
			e.printStackTrace();
		}
		
		String moviename = null;
		try {
			while ((moviename=movieBR.readLine()) != null){
				movieName.add(moviename.trim());
			}
		} catch (IOException e) {
			System.out.println("read movie.txt file failed");
			e.printStackTrace();
		}
		return movieName;
	}
	
	public float probBetweenTowTokens(String t1,String t2){
		String t1t2 = t1+t2;
		int count = wordCountMap.get(t1t2)==null? 0: wordCountMap.get(t1t2);
		//System.out.println(t1t2+"words string count is : "+count);
		if (totalTokensCount > 0 ){
			return (float)count/totalTokensCount;
		}
		else
			return (float) 0.0;
		
	}
	
	public float probBetweenTowTokens(String token){
		int count = wordCountMap.get(token)==null? 0: wordCountMap.get(token);
		//System.out.println(token+"words string count is : "+count);
		if (totalTokensCount > 0 ){
			return (float)count/totalTokensCount;
		}
		else
			return (float) 0.0;
		
	}
	
	public List<String> getCorrectTokens(String[] sInputResult){
		List<String> correctTokens = new ArrayList<String>();
		float probOne = 0;
		//float probTwo = 0;
		List<Integer> isCorrect = new ArrayList<Integer>();
		for (int i=0;i<sInputResult.length;i++){
			//System.out.println(sInputResult[i]);
			probOne=probBetweenTowTokens(sInputResult[i]);
			 if (probOne <= 0){
				 isCorrect.add(i, 0);
				 //System.out.println("there is a spell error at word index : "+i);
			 } else {
				 isCorrect.add(i, 1);
				 //correctTokens.add(sInputResult[i]);
			 }
			 
		}
		
	 
		if (sInputResult.length>2){
			if (!isCorrect.contains(0)){
				//TODO;
				//System.out.println("isCorrect don't contain 0");
				for (int i=0;i<sInputResult.length-1;i++){
					StringBuffer tokenbuf = new StringBuffer();
					//float a = probBetweenTowTokens(sInputResult[i]);
					//float b = probBetweenTowTokens(sInputResult[i]+sInputResult[i+1]);
					tokenbuf.append(sInputResult[i]);
					for(int j=i+1;j<sInputResult.length;j++){
						float b = probBetweenTowTokens(tokenbuf.toString()+sInputResult[j]);
						if (b>0) tokenbuf.append(sInputResult[j]);
						else{
							hasError = 1;
							if (j<sInputResult.length-1 && probBetweenTowTokens(tokenbuf.toString()+sInputResult[j]+sInputResult[j+1])>0){
								tokenbuf.append(sInputResult[j]+sInputResult[j+1]);
							}
							else break;
						}						
					}
					correctTokens.add(tokenbuf.toString());
				}
				
				if (probBetweenTowTokens(sInputResult[sInputResult.length-1]) > 0){
					correctTokens.add(sInputResult[sInputResult.length-1]);
				}
				
			}
			else {
				//TODO
				//System.out.println("isCorrect contains 0");
				for (int i=0;i<sInputResult.length-1;i++){
					StringBuffer tokenbuf = new StringBuffer();
					int a = isCorrect.get(i);
					if (a>0){
						tokenbuf.append(sInputResult[i]);
						for(int j=i+1;j<sInputResult.length;j++){
							float b = probBetweenTowTokens(tokenbuf.toString()+sInputResult[j]);
							if (b>0) tokenbuf.append(sInputResult[j]);
							else{
								hasError = 2;
								break;
							}
								
						}
						correctTokens.add(tokenbuf.toString());
					}
					else if (probBetweenTowTokens(sInputResult[i]+sInputResult[i+1]) > 0.0){
						tokenbuf.append(sInputResult[i]).append(sInputResult[i+1]);
						for(int j=i+2;j<sInputResult.length;j++){
							float b = probBetweenTowTokens(tokenbuf.toString()+sInputResult[j]);
							if (b>0) tokenbuf.append(sInputResult[j]);
							else{
								hasError = 2;
								break;
							}
						}
						correctTokens.add(tokenbuf.toString());
					}
					
				}
				
			}
		} else if (sInputResult.length == 2){
			if (probBetweenTowTokens(sInputResult[0]+sInputResult[1]) > 0) correctTokens.add(sInputResult[0]+sInputResult[1]);
		}
		return correctTokens ;
	}
	
	
	public String[] getMaxAndSecondMaxSequnce(String[] sInputResult){
		List<String> correctTokens = getCorrectTokens(sInputResult);
		//TODO
		//System.out.println(correctTokens);
		String[] maxAndSecondMaxSeq = new String[2];
		if (correctTokens.size() == 0) return null;
		else if (correctTokens.size() == 1){
			maxAndSecondMaxSeq[0]=correctTokens.get(0);
			maxAndSecondMaxSeq[1]=correctTokens.get(0);
			return maxAndSecondMaxSeq;
		}
		
		String maxSequence = correctTokens.get(0);
		String maxSequence2 = correctTokens.get(correctTokens.size()-1);
		String littleword = "";
		for (int i=1;i<correctTokens.size();i++){
			//System.out.println(correctTokens);
			if (correctTokens.get(i).length() > maxSequence.length()){
				maxSequence = correctTokens.get(i);
			} else if (correctTokens.get(i).length() == maxSequence.length()){
				
				//select the word with greater probability for single-word
				if (correctTokens.get(i).length()==1){
					if (probBetweenTowTokens(correctTokens.get(i)) > probBetweenTowTokens(maxSequence)) {
						maxSequence2 = correctTokens.get(i);
					}
				}
				//select words with smaller probability for multi-word, because the smaller has more self information
				else if (correctTokens.get(i).length()>1){
					if (probBetweenTowTokens(correctTokens.get(i)) <= probBetweenTowTokens(maxSequence)) {
						maxSequence2 = correctTokens.get(i);
					}
				}
				
			} else if (correctTokens.get(i).length() > maxSequence2.length()){
				maxSequence2 = correctTokens.get(i);
			} else if (correctTokens.get(i).length() == maxSequence2.length()){
				if (probBetweenTowTokens(correctTokens.get(i)) > probBetweenTowTokens(maxSequence2)){
					maxSequence2 = correctTokens.get(i);
				}
			}
		}
		//TODO
		//System.out.println(maxSequence+" : "+maxSequence2);
		//delete the sub-word from a string
		if (maxSequence2.length() == maxSequence.length()){
			int maxseqvaluableTokens = maxSequence.length();
			int maxseq2valuableTokens = maxSequence2.length();
			float min_truncate_prob_a = 0 ;
			float min_truncate_prob_b = 0;
			String aword = "";
			String bword = "";
			for (int i=0;i<correctTokens.size();i++){
				float tokenprob = probBetweenTowTokens(correctTokens.get(i));
				if ((!maxSequence.equals(correctTokens.get(i))) && maxSequence.contains(correctTokens.get(i))){
					if ( tokenprob >= min_truncate_prob_a){
						min_truncate_prob_a = tokenprob ;
						aword = correctTokens.get(i);
					}
				}
				else if ((!maxSequence2.equals(correctTokens.get(i))) && maxSequence2.contains(correctTokens.get(i))){
					if (tokenprob >= min_truncate_prob_b){
						min_truncate_prob_b = tokenprob;
						bword = correctTokens.get(i);
					}
				}
			}
			//TODO
			//System.out.println(aword+" VS "+bword);
			System.out.println(min_truncate_prob_a+" VS "+min_truncate_prob_b);
			if (aword.length()>0 && min_truncate_prob_a < min_truncate_prob_b){
				maxseqvaluableTokens -= 1 ;
				littleword = maxSequence.replace(aword,"");
			}else {
				maxseq2valuableTokens -= 1 ;
				String temp = maxSequence2;
				if (maxSequence.contains(temp.replace(bword, ""))){
					littleword =  maxSequence2;
				}
				else littleword =  maxSequence2.replace(bword,"");
				
			}
			
			if (maxseqvaluableTokens < maxseq2valuableTokens){
				maxSequence = maxSequence2;
				maxSequence2 = littleword;
			}else {
				maxSequence2 = littleword;
			}
			
		}
		maxAndSecondMaxSeq[0] = maxSequence;
		maxAndSecondMaxSeq[1] = maxSequence2;
		
		return maxAndSecondMaxSeq ;
	}
	
	
	public List<String> proofreadAndSuggest(String sInput){
		//List<String> correctTokens = new ArrayList<String>();
		List<String> correctedList = new ArrayList<String>();
		List<String> crtTempList = new ArrayList<String>();

		//TODO 
		Calendar startProcess = Calendar.getInstance();
		char[] str2char = sInput.toCharArray();
		String[] sInputResult = new String[str2char.length];//cwp.wordSegmentate(sInput);
		for (int t=0;t<str2char.length;t++){
			sInputResult[t] = String.valueOf(str2char[t]);
		}
		//String[] sInputResult = cwp.wordSegmentate(sInput);
		//System.out.println(sInputResult);
		//float re = probBetweenTowTokens("非","诚");
		String[] MaxAndSecondMaxSequnce = getMaxAndSecondMaxSequnce(sInputResult);
		
		// display errors and suggest correct movie name
		//System.out.println("hasError="+hasError);
		if (hasError !=0){
			if (MaxAndSecondMaxSequnce.length>1){
				String maxSequence = MaxAndSecondMaxSequnce[0];
				String maxSequence2 = MaxAndSecondMaxSequnce[1];
				for (int j=0;j<movieName.size();j++){
					//boolean isThisMovie = false;
					String movie = movieName.get(j);
					
					
					//System.out.println("maxseq is "+maxSequence+", maxseq2 is "+maxSequence2);
					
					//select movie
					if (maxSequence2.equals("")){
						if (movie.contains(maxSequence)) correctedList.add(movie);
					}
					else {
						if (movie.contains(maxSequence) && movie.contains(maxSequence2)){
							//correctedList.clear();
							crtTempList.add(movie);
							//correctedList.add(movie);
							//break;
						}
						//else if (movie.contains(maxSequence) || movie.contains(maxSequence2)) correctedList.add(movie);
						else if (movie.contains(maxSequence)) correctedList.add(movie);
					}
					
				}
				
				if (crtTempList.size()>0){
					correctedList.clear();
					correctedList.addAll(crtTempList);
				}
				
				//TODO　
				if (hasError ==1) System.out.println("No spellig error,Sorry for having no this movie,do you want to get :"+correctedList.toString()+" ?");
				//TODO 
				else System.out.println("Spellig error,do you want to get :"+correctedList.toString()+" ?");
			} //TODO 
			else System.out.println("there are spellig errors, no anyone correct token in your spelled words,so I can't guess what you want, please check it again");
			
		} //TODO 
		else System.out.println("No spelling error");
		
		//TODO
		Calendar endProcess = Calendar.getInstance();
		long elapsetime = (endProcess.getTimeInMillis()-startProcess.getTimeInMillis()) ;
		System.out.println("process work elapsed "+elapsetime+" ms");
		ICTCLAS2011.ICTCLAS_Exit();
		
		return correctedList ;
	}
	
	
	
	public static void main(String[] args) {
		
		String argu1 = "movie.txt";          //movies name file
		String argu2 = "movie_result.txt";   //words after segmenting name of all movies
		
		SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
		String startInitTime = sdf.format(new java.util.Date()); 
		System.out.println(startInitTime+" ---start initializing work---");
		ChineseWordProofread cwp = new ChineseWordProofread(argu1,argu2);
	
		String endInitTime = sdf.format(new java.util.Date());
		System.out.println(endInitTime+" ---end initializing work---");
		
		Scanner scanner = new Scanner(System.in);
		while(true){
			System.out.print("请输入影片名：");
			
			String input = scanner.next();
			
			if (input.equals("EXIT")) break;
			
			cwp.proofreadAndSuggest(input);
			
		}
		scanner.close();
	}

}
