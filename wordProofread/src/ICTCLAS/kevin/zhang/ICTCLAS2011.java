package ICTCLAS.kevin.zhang;
//import java.io.*;

public class ICTCLAS2011 {
    public static native boolean  ICTCLAS_Init(byte[] sDataPath,int encoding);
    //encoding=0,设置为GBK；encoding=1设置为UTF8,encoding=2设置为BIG5
		public static native boolean  ICTCLAS_Exit();
		public native   int ICTCLAS_ImportUserDict(byte[] sPath);
		public native float ICTCLAS_GetUniProb(byte[] sWord);
		public native boolean ICTCLAS_IsWord(byte[] sWord);
		public native   byte[] ICTCLAS_ParagraphProcess(byte[] sSrc,int bPOSTagged);
		public native   boolean ICTCLAS_FileProcess(byte[] sSrcFilename,byte[] sDestFilename,int bPOSTagged);
		public native   byte[] nativeProcAPara(byte[] src);

	
/*********************************************************************
*
*  Func Name  : ICTCLAS_AddUserWord
*
*  Description: add a word to the user dictionary ,example:你好	
*													 i3s	n
*
*  Parameters : sFilename: file name
*               
*  Returns    : 1,true ; 0,false
*
*  Author     :   
*  History    : 
*              1.create 11:10:2008
*********************************************************************/
public native int ICTCLAS_AddUserWord(byte[] sWord);//add by qp 2008.11.10



/*********************************************************************
*
*  Func Name  : Save
*
*  Description: Save dictionary to file
*
*  Parameters :
*               
*  Returns    : 1,true; 2,false
*
*  Author     :   
*  History    : 
*              1.create 11:10:2008
*********************************************************************/
public native int ICTCLAS_SaveTheUsrDic();

/*********************************************************************
*
*  Func Name  : ICTCLAS_DelUsrWord
*
*  Description: delete a word from the  user dictionary
*
*  Parameters : 
*  Returns    : -1, the word not exist in the user dictionary; else, the handle of the word deleted
*
*  Author     :   
*  History    : 
*              1.create 11:10:2008
*********************************************************************/
public native int ICTCLAS_DelUsrWord(byte[] sWord);

/*********************************************************************
*
*  Func Name  : ICTCLAS_KeyWord
*
*  Description: Extract keyword from paragraph
*
*  Parameters : resultKey, the returned key word 
				nCountKey, resultKey中能存放reslut_t的个数，
*  Returns    : 0, failed; else, 1, successe
*  备注：该函数在nativeProcAPara后调用,resultKey的内存大小应该大于或等于nativeProcAPara的返回结果，nCountKey为resultKey/ICTCLAS_GetElemLength(0)
*  Author     :   
*  History    : 
*              1.create 11:10:2008
*********************************************************************/
public native int ICTCLAS_KeyWord(byte[] resultKey, int nCountKey);

/*********************************************************************
*
*  Func Name  : ICTCLAS_FingerPrint
*
*  Description: Extract a finger print from the paragraph
*
*  Parameters :
*  Returns    : 0, failed; else, the finger print of the content
*
*  Author     :   
*  History    : 
*              1.create 11:10:2008
*********************************************************************/
public native  long ICTCLAS_FingerPrint();

/*********************************************************************
*
*  Func Name  : ICTCLAS_SetPOSmap
*
*  Description: select which pos map will use
*
*  Parameters :nPOSmap, ICT_POS_MAP_FIRST  计算所一级标注集
						ICT_POS_MAP_SECOND  计算所二级标注集
						PKU_POS_MAP_SECOND   北大二级标注集
						PKU_POS_MAP_FIRST 	  北大一级标注集
*  Returns    : 0, failed; else, success
*
*  Author     :   
*  History    : 
*              1.create 11:10:2008
*********************************************************************/
public native int ICTCLAS_SetPOSmap(int nPOSmap);

/*********************************************************************
*
*  Func Name  : ICTCLAS_GetElemLength
*
*  Description: 获取结构体result_t中每个变量占用内存大小
*
*  Parameters :nIndex, 获取第nIndex个变量占用内存大小，例如GetElemLength(1),为获取result_t.length的占用内存大小
*  Returns    :占用内存的大小
*	备注：1.由于内存对齐的问题，每个变量实际占用内存的大小不确定。
 *		  2.ICTCLAS_GetElemLength(0)获取结构体result_t的大小
*  Author     :   
*  History    : 
*             
*********************************************************************/

public native int ICTCLAS_GetElemLength(int nIndex);

    /* Use static intializer */
    static {
			System.loadLibrary("ICTCLAS2011");
    }
}


