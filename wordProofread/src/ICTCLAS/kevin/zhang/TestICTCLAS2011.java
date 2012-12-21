package ICTCLAS.kevin.zhang;

//import java.util.*;
//import java.io.*;


class Result {
	public int start; //start position,词语在输入句子中的开始位置
	public int length; //length,词语的长度
	//public char sPOS[8]; //词性
	public int posId;//word type，词性ID值，可以快速的获取词性表
	public int wordId; //如果是未登录词，设成0或者-1
	public int word_type; //add by qp 2008.10.29 区分用户词典;1，是用户词典中的词；0，非用户词典中的词

  public int weight;//add by qp 2008.11.17 word weight
};



public class TestICTCLAS2011 {

	public static void main(String[] args) throws Exception
	{
		try
		{
			String sInput = "传奇故事121213";

			//分词
			Split(sInput);

			//对UTF8进行分词处理
			//SplitUTF8();

			//对BIG5进行分词处理
			//SplitBIG5();
		}
		catch (Exception ex)
		{
		} 


	}

	public static void Split(String sInput)
	{	
	try{	
		ICTCLAS2011 testICTCLAS2011 = new ICTCLAS2011();

		String argu = "F:\\workspace\\wordAutoErrorCorrection\\";
		System.out.println("ICTCLAS_Init");
		if (ICTCLAS2011.ICTCLAS_Init(argu.getBytes("GB2312"),0) == false)
		{
			System.out.println("Init Fail!");
			return;
		}

		/*
		 * 设置词性标注集
		        ID		    代表词性集 
				1			计算所一级标注集
				0			计算所二级标注集
				2			北大二级标注集
				3			北大一级标注集
		*/
		testICTCLAS2011.ICTCLAS_SetPOSmap(2);

		//导入用户词典前
		byte nativeBytes[] = testICTCLAS2011.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 0);
		String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");

		System.out.println("未导入用户词典： " + nativeStr);
		
		//文件分词
		String argu1 = "movie.txt";
		String argu2 = "TestGBK_result.txt";
		testICTCLAS2011.ICTCLAS_FileProcess(argu1.getBytes("GB2312"), argu2.getBytes("GB2312"), 0);
		
/*
		//导入用户词典
		String sUserDict = "userdic.txt";
		int nCount = testICTCLAS2011.ICTCLAS_ImportUserDict(sUserDict.getBytes("GB2312"));
		testICTCLAS2011.ICTCLAS_SaveTheUsrDic();//保存用户词典
		System.out.println("导入个用户词： " + nCount);

		nativeBytes = testICTCLAS2011.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 1);
		nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");

		System.out.println("导入用户词典后： " + nativeStr);

		//动态添加用户词
		String sWordUser = "973专家组组织的评测	ict";
		testICTCLAS2011.ICTCLAS_AddUserWord(sWordUser.getBytes("GB2312"));
		testICTCLAS2011.ICTCLAS_SaveTheUsrDic();//保存用户词典			
		
		nativeBytes = testICTCLAS2011.ICTCLAS_ParagraphProcess(sInput.getBytes("GB2312"), 1);
		nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
		System.out.println("动态添加用户词后: " + nativeStr);

		//分词高级接口
		nativeBytes = testICTCLAS2011.nativeProcAPara(sInput.getBytes("GB2312"));

		int nativeElementSize = testICTCLAS2011.ICTCLAS_GetElemLength(0);//size of result_t in native code
		int nElement = nativeBytes.length / nativeElementSize;

		byte nativeBytesTmp[] = new byte[nativeBytes.length];

		//关键词提取
		int nCountKey = testICTCLAS2011.ICTCLAS_KeyWord(nativeBytesTmp, nElement);

		Result[] resultArr = new Result[nCountKey];
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(nativeBytesTmp));

		int iSkipNum;
		for (int i = 0; i < nCountKey; i++)
		{
			resultArr[i] = new Result();
			resultArr[i].start = Integer.reverseBytes(dis.readInt());
			iSkipNum = testICTCLAS2011.ICTCLAS_GetElemLength(1) - 4;
			if (iSkipNum > 0)
			{
				dis.skipBytes(iSkipNum);
			}

			resultArr[i].length = Integer.reverseBytes(dis.readInt());
			iSkipNum = testICTCLAS2011.ICTCLAS_GetElemLength(2) - 4;
			if (iSkipNum > 0)
			{
				dis.skipBytes(iSkipNum);
			}

			dis.skipBytes(testICTCLAS2011.ICTCLAS_GetElemLength(3));

			resultArr[i].posId = Integer.reverseBytes(dis.readInt());
			iSkipNum = testICTCLAS2011.ICTCLAS_GetElemLength(4) - 4;
			if (iSkipNum > 0)
			{
				dis.skipBytes(iSkipNum);
			}

			resultArr[i].wordId = Integer.reverseBytes(dis.readInt());
			iSkipNum = testICTCLAS2011.ICTCLAS_GetElemLength(5) - 4;
			if (iSkipNum > 0)
			{
				dis.skipBytes(iSkipNum);
			}

			resultArr[i].word_type = Integer.reverseBytes(dis.readInt());
			iSkipNum = testICTCLAS2011.ICTCLAS_GetElemLength(6) - 4;
			if (iSkipNum > 0)
			{
				dis.skipBytes(iSkipNum);
			}
			resultArr[i].weight = Integer.reverseBytes(dis.readInt());
			iSkipNum = testICTCLAS2011.ICTCLAS_GetElemLength(7) - 4;
			if (iSkipNum > 0)
			{
				dis.skipBytes(iSkipNum);
			}				

		}

		dis.close();

		for (int i = 0; i < resultArr.length; i++)
		{
			System.out.println("start=" + resultArr[i].start + ",length=" + resultArr[i].length + "pos=" + resultArr[i].posId + "word=" + resultArr[i].wordId + "  weight=" + resultArr[i].weight);
		}*/

		
		//释放分词组件资源
		ICTCLAS2011.ICTCLAS_Exit();
	}
	catch (Exception ex)
	{
	} 

	}

	public static void SplitUTF8()
	{
		try
		{

		
		ICTCLAS2011 testICTCLAS2011 = new ICTCLAS2011();

		String argu = ".";
		if (ICTCLAS2011.ICTCLAS_Init(argu.getBytes("GB2312"),1) == false)
		{//UTF8切分
			System.out.println("Init Fail!");
			return;
		}
		String argu1 = "TestUTF.txt";
		String argu2 = "TestUTF_result.txt";
		testICTCLAS2011.ICTCLAS_FileProcess(argu1.getBytes("GB2312"), argu2.getBytes("GB2312"), 1);


		//释放分词组件资源
		ICTCLAS2011.ICTCLAS_Exit();
		}
		catch (Exception ex)
		{
		} 
	}
	
	public static void SplitBIG5()
	{
		try
		{

		
		ICTCLAS2011 testICTCLAS2011 = new ICTCLAS2011();

		String argu = ".";
		if (ICTCLAS2011.ICTCLAS_Init(argu.getBytes("GB2312"),2) == false)
		{//UTF8切分
			System.out.println("Init Fail!");
			return;
		}
		String argu1 = "TestBIG.txt";
		String argu2 = "TestBIG_result.txt";
		testICTCLAS2011.ICTCLAS_FileProcess(argu1.getBytes("GB2312"), argu2.getBytes("GB2312"), 1);


		//释放分词组件资源
		ICTCLAS2011.ICTCLAS_Exit();
		}
		catch (Exception ex)
		{
		} 
	}
}
 
