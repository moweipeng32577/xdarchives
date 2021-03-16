package com.xdtech.project.lot.eqcall;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.xdtech.project.lot.device.entity.DeviceHistory;

import java.util.List;

/**
 * Created by Administrator on 2019/5/7.
 */
public class EqCallUseDll{


    // 全局参数定义
//    static int m_iCardNum = 1;
//    static int m_iProgramIndex = -1;
//    static int m_iProgramCount = 0;
//    static String m_strUserPath = System.getProperty("user.dir");
//    private static final User32 USER = User32.INSTANCE;
//    private static final GDI32 GDI = GDI32.INSTANCE;

    // 主函数
    public static void main(String[] args) {
        // 1、加载动态库EQ2008_Dll.dll
//        String strDllFileName = m_strUserPath + "\\res\\EQ2008_Dll";
//        String strEQ2008_Dll_Set_Path = m_strUserPath + "\\res\\EQ2008_Dll_Set.ini";
//        m_DllLibrary = (EqCallUse) Native.loadLibrary(strDllFileName,EqCallUse.class);
//        m_DllLibrary.User_ReloadIniFile(strEQ2008_Dll_Set_Path);

        // 2、节目操作函数演示
        ////(1)、删除所有节目
//        OnDelAllProgram();
        ////(2)、添加节目(可以添加多个节目)
//        for(int i=0;i<1;i++)
//        {
//            OnAddProgram();//添加节目
            ////(3)、添加分区窗口到节目,每个节目可以添加多个分区，但分区位置不能重叠
//            OnAddText(); 		//文本窗操作演示
            //OnAddRTF(); 		//RTF窗操作演示
            //OnAddSingleText();//单行文本窗操作演示
            //OnAddbmp(); 		//图片窗操作演示
            //OnAddTime(); 		//时间窗操作演示
            //OnAddTimeCount(); //计时窗操作演示
            //OnAddTemperature();//温度窗操作演示
//        }
        ////(3)、发送节目到显示屏
//        OnSendToScreen();

		/*
		// 3、实时更新数据
		////(0)、清空控制卡原有节目，只需要清空一次
		//OnRealtimeScreenClear();
		////(1)、建立连接
		if(OnRealtimeConnect())
		{
			////(2)、发送数据
			//OnRealtimeSendData(); 	//图片句柄
			//OnRealtimeSendBmpData();//图片路径
			OnRealtimeSendText();	//文本信息
			////(3)、断开连接
			OnRealtimeDisConnect();
		}
		*/

        // 4、显示屏控制函数
        //OnAjusttime();  //校准显示屏时间
        //OnOpenScreen(); //打开显示屏
        //OnCloseScreen();//关闭显示屏
    }

    //函数：添加节目索引
    public static void OnAddProgram(EqCallUse m_DllLibrary,int m_iProgramIndex,int m_iCardNum,int m_iProgramCount)
    {
        m_iProgramIndex= m_DllLibrary.User_AddProgram(m_iCardNum,false,10);
        m_iProgramCount++;
        System.out.println("添加节目"+m_iProgramCount);
    }

    //函数：添加文本窗
    public static void OnAddText(EqCallUse m_DllLibrary,int m_iProgramIndex,int m_iCardNum)
    {
        EqCallUse.User_Text  Text = new EqCallUse.User_Text();

        Text.BkColor = 0;
        Text.chContent = "欢迎使用";

        Text.PartInfo.FrameColor = 0;
        Text.PartInfo.iFrameMode = 0;
        Text.PartInfo.iHeight = 64;
        Text.PartInfo.iWidth = 64;
        Text.PartInfo.iX = 0;
        Text.PartInfo.iY = 0;

        Text.FontInfo.bFontBold = false;
        Text.FontInfo.bFontItaic= false;
        Text.FontInfo.bFontUnderline = false;
        Text.FontInfo.colorFont = 0xFFFF;
        Text.FontInfo.iFontSize = 20;
        Text.FontInfo.strFontName = "";
        Text.FontInfo.iAlignStyle = 1;
        Text.FontInfo.iVAlignerStyle = 1;
        Text.FontInfo.iRowSpace = 0;

        Text.MoveSet.bClear = false;
        Text.MoveSet.iActionSpeed = 1;
        Text.MoveSet.iActionType = 0;
        Text.MoveSet.iHoldTime = 10;
        Text.MoveSet.iClearActionType	= 0;
        Text.MoveSet.iClearSpeed		= 4;
        Text.MoveSet.iFrameTime			= 10;

        if(-1 == m_DllLibrary.User_AddText(m_iCardNum,Text,m_iProgramIndex))
        {
            System.out.println("添加文本失败！");
        }
        else
        {
            System.out.println("添加文本成功！");
        }
    }

    //函数：添加RTF文件
    public static void OnAddRTF(EqCallUse m_DllLibrary,int m_iProgramIndex,int m_iCardNum,String m_strUserPath)
    {
        EqCallUse.User_RTF  RTF = new EqCallUse.User_RTF();

        String strFileName = m_strUserPath + "\\res\\EQ2008_RTF.rtf";
        RTF.strFileName = strFileName;

        RTF.PartInfo.FrameColor		= 0;
        RTF.PartInfo.iFrameMode		= 0;
        RTF.PartInfo.iHeight		= 32;
        RTF.PartInfo.iWidth			= 64;
        RTF.PartInfo.iX				= 0;
        RTF.PartInfo.iY				= 0;

        RTF.MoveSet.bClear			= false;
        RTF.MoveSet.iActionSpeed	= 4;
        RTF.MoveSet.iActionType		= 0;
        RTF.MoveSet.iHoldTime		= 10;
        RTF.MoveSet.iClearActionType= 0;
        RTF.MoveSet.iClearSpeed		= 4;
        RTF.MoveSet.iFrameTime		= 10;

        if(-1 == m_DllLibrary.User_AddRTF(m_iCardNum,RTF,m_iProgramIndex))
        {
            System.out.println("添加rtf文件失败！");
        }
        else
        {
            System.out.println("添加rtf文件成功！");
        }
    }

    //函数：添加单行文本
    public static void OnAddSingleText(EqCallUse m_DllLibrary,int m_iProgramIndex,int m_iCardNum)
    {
        EqCallUse.User_SingleText  SingleText = new EqCallUse.User_SingleText();

        SingleText.BkColor		= 0;
        SingleText.chContent	= "欢迎使用EQ异步控制卡！";
        SingleText.PartInfo.iFrameMode	= 0;
        SingleText.PartInfo.iHeight		= 32;
        SingleText.PartInfo.iWidth		= 64;
        SingleText.PartInfo.iX = 0;
        SingleText.PartInfo.iY = 0;
        SingleText.FontInfo.bFontBold		= false;
        SingleText.FontInfo.bFontItaic		= false;
        SingleText.FontInfo.bFontUnderline	= false;
        SingleText.FontInfo.colorFont		= 0xFF;
        SingleText.FontInfo.iFontSize		= 12;
        SingleText.PartInfo.FrameColor		= 0;
        SingleText.FontInfo.strFontName		= "";
        SingleText.MoveSet.bClear			= false;
        SingleText.MoveSet.iActionSpeed		= 8;
        SingleText.MoveSet.iActionType		= 2;
        SingleText.MoveSet.iHoldTime		= 0;
        SingleText.MoveSet.iClearActionType	= 0;
        SingleText.MoveSet.iClearSpeed		= 4;
        SingleText.MoveSet.iFrameTime		= 20;
        if(-1 == m_DllLibrary.User_AddSingleText(m_iCardNum,SingleText,m_iProgramIndex))
        {
            System.out.println("添加单行文本失败！");
        }
        else
        {
            System.out.println("添加单行文本成功！");
        }
    }

    //函数：添加图片
    public static void OnAddbmp(EqCallUse m_DllLibrary,int m_iProgramIndex,int m_iCardNum,String m_strUserPath,User32 USER,GDI32 GDI)
    {
        EqCallUse.User_Bmp		BmpZone = new EqCallUse.User_Bmp();
        EqCallUse.User_MoveSet	MoveSet = new EqCallUse.User_MoveSet();
        int iBMPZoneNum;

        BmpZone.PartInfo.iX		 = 0;
        BmpZone.PartInfo.iY		 = 0;
        BmpZone.PartInfo.iHeight = 32;
        BmpZone.PartInfo.iWidth  = 64;
        BmpZone.PartInfo.FrameColor = 0xFF00;
        BmpZone.PartInfo.iFrameMode = 1;

        MoveSet.bClear			= true;
        MoveSet.iActionSpeed	= 4;
        MoveSet.iActionType		= 0;
        MoveSet.iHoldTime		= 50;
        MoveSet.iClearActionType= 0;
        MoveSet.iClearSpeed		= 4;
        MoveSet.iFrameTime		= 10;

        iBMPZoneNum = m_DllLibrary.User_AddBmpZone(m_iCardNum,BmpZone,m_iProgramIndex);

        //通过图片路径添加两张图片
        if(m_DllLibrary.User_AddBmpFile(m_iCardNum,iBMPZoneNum,m_strUserPath +"\\res\\BMP1.bmp",MoveSet,m_iProgramIndex))
        {
            System.out.println("添加图片路径1成功！");
        }
        else
        {
            System.out.println("添加图片路径1失败！");
        }

        // 通过图片句柄添加图片
        HANDLE hBitmap = USER.LoadImage(null,           // 模块实例句柄
                m_strUserPath + "\\res\\BMP1.bmp",   // 位图路径
                USER.IMAGE_BITMAP,  // 位图类型
                64,   				// 指定图片宽
                32,  				// 指定图片高
                USER.LR_LOADFROMFILE );
        if(hBitmap != null)
        {
            if(m_DllLibrary.User_AddBmp(m_iCardNum,iBMPZoneNum,hBitmap,MoveSet,m_iProgramIndex))
            {
                System.out.println("添加图片句柄2成功！");
            }
            else
            {
                System.out.println("添加图片句柄2失败！");
            }
            GDI.DeleteObject(hBitmap);
        }
        else
        {
            System.out.println("添加图片句柄2失败！");
        }
    }

    //函数：添加时间
    public static void OnAddTime(EqCallUse m_DllLibrary,int m_iProgramIndex,int m_iCardNum)
    {
        EqCallUse.User_DateTime  DateTime = new EqCallUse.User_DateTime();

        DateTime.bDay = 1;
        DateTime.bHour = 1;
        DateTime.BkColor = 1;
        DateTime.bMin = 1;
        DateTime.bMouth = 1;
        DateTime.bMulOrSingleLine = 1;
        DateTime.bSec =1;
        DateTime.bWeek = 1;
        DateTime.bYear = 1;
        DateTime.bYearDisType = 1;
        DateTime.chTitle = "北京";

        DateTime.PartInfo.iFrameMode = 1;
        DateTime.iDisplayType = 4;
        DateTime.PartInfo.FrameColor =0xFFFF;
        DateTime.PartInfo.iHeight = 64;
        DateTime.PartInfo.iWidth = 64;
        DateTime.PartInfo.iX=0;
        DateTime.PartInfo.iY=0;
        DateTime.FontInfo.bFontBold=false;
        DateTime.FontInfo.bFontItaic=false;
        DateTime.FontInfo.bFontUnderline=false;
        DateTime.FontInfo.colorFont = 0xFFFF;
        DateTime.FontInfo.iAlignStyle = 1;
        DateTime.FontInfo.iFontSize = 12;
        DateTime.FontInfo.strFontName = "";

        if(-1 == m_DllLibrary.User_AddTime(m_iCardNum,DateTime,m_iProgramIndex))
        {
            System.out.println("添加时间失败！");
        }
        else
        {
            System.out.println("添加时间成功！");
        }
    }

    //函数：添加计时
    public static void OnAddTimeCount(EqCallUse m_DllLibrary,int m_iProgramIndex,int m_iCardNum)
    {
        EqCallUse.User_Timer  TimeCount = new EqCallUse.User_Timer();

        TimeCount.bDay = true;
        TimeCount.bHour = false;
        TimeCount.BkColor =0;
        TimeCount.bMin = false;
        TimeCount.bMulOrSingleLine =true;
        TimeCount.bSec =false;
        TimeCount.chTitle = "距离五一";
        TimeCount.FontInfo.bFontBold=false;
        TimeCount.FontInfo.bFontItaic=false;
        TimeCount.FontInfo.bFontUnderline=false;
        TimeCount.FontInfo.colorFont=0xFFFF;
        TimeCount.FontInfo.iAlignStyle=2;
        TimeCount.FontInfo.iFontSize=12;
        TimeCount.FontInfo.strFontName="";
        TimeCount.PartInfo.FrameColor = 0xFF00;
        TimeCount.PartInfo.iFrameMode =0;
        TimeCount.PartInfo.iHeight = 32;
        TimeCount.PartInfo.iWidth = 64;
        TimeCount.PartInfo.iX =0;
        TimeCount.PartInfo.iY=0;
        TimeCount.ReachTimeYear=2015;
        TimeCount.ReachTimeMonth=5;
        TimeCount.ReachTimeDay= 1;
        TimeCount.ReachTimeHour=10;
        TimeCount.ReachTimeMinute=0;
        TimeCount.ReachTimeSecond=0;

        if(-1 == m_DllLibrary.User_AddTimeCount(m_iCardNum,TimeCount,m_iProgramIndex))
        {
            System.out.println("添加计时失败！");
        }
        else
        {
            System.out.println("添加计时成功！");
        }
    }

    //函数：添加温度
    public static void OnAddTemperature(EqCallUse m_DllLibrary,int m_iProgramIndex,int m_iCardNum)
    {
        EqCallUse.User_Temperature  Temperature = new EqCallUse.User_Temperature();

        Temperature.BkColor=0;
        Temperature.chTitle="";
        Temperature.DisplayType=0;
        Temperature.PartInfo.FrameColor=0xFF00;
        Temperature.PartInfo.iFrameMode=1;
        Temperature.PartInfo.iHeight=32;
        Temperature.PartInfo.iWidth=64;
        Temperature.PartInfo.iX=0;
        Temperature.PartInfo.iY=0;
        Temperature.FontInfo.bFontBold=false;
        Temperature.FontInfo.bFontItaic=false;
        Temperature.FontInfo.bFontUnderline=false;
        Temperature.FontInfo.colorFont=0xFFFF;
        Temperature.FontInfo.iAlignStyle=0;
        Temperature.FontInfo.iFontSize=12;
        Temperature.FontInfo.strFontName="宋体";

        if(-1 == m_DllLibrary.User_AddTemperature(m_iCardNum,Temperature,m_iProgramIndex))
        {
            System.out.println("添加温度失败！");
        }
        else
        {
            System.out.println("添加温度成功！");
        }
    }

    //函数：删除所有节目
    public static void OnDelAllProgram(EqCallUse m_DllLibrary,int m_iProgramCount,int m_iCardNum)
    {
        if(!m_DllLibrary.User_DelAllProgram(m_iCardNum))
        {
            System.out.println("删除节目失败！");
        }
        else
        {
            //提示信息
            m_iProgramCount=0;
            System.out.println("请首先添加节目！");
        }
    }

    //函数：发送数据到显示屏
    public static void OnSendToScreen(EqCallUse m_DllLibrary,int m_iCardNum)
    {
        if(!m_DllLibrary.User_SendToScreen(m_iCardNum))
        {
            System.out.println("数据发送失败！");
        }
        else
        {
            System.out.println("数据发送成功！");
        }

    }

    //函数：校正时间
    public static void OnAjusttime(EqCallUse m_DllLibrary,int m_iCardNum)
    {
        if(!m_DllLibrary.User_AdjustTime(m_iCardNum))
        {
            System.out.println("校准时间失败！");
        }
        else
        {
            System.out.println("校准时间成功！");
        }
    }

    //函数：打开显示屏
    public static void OnOpenScreen(EqCallUse m_DllLibrary,int m_iCardNum)
    {
        if(!m_DllLibrary.User_OpenScreen(m_iCardNum))
        {
            System.out.println("打开显示屏失败！");
        }
        else
        {
            System.out.println("打开显示屏成功！");
        }
    }

    //函数：关闭显示屏
    public static void OnCloseScreen(EqCallUse m_DllLibrary,int m_iCardNum)
    {
        if(!m_DllLibrary.User_CloseScreen(m_iCardNum))
        {
            System.out.println("关闭显示屏失败！");
        }
        else
        {
            System.out.println("关闭显示屏成功！");
        }
    }

    //函数：实时发送数据，建立连接
    //日期：2008-04-30
    public static boolean OnRealtimeConnect(EqCallUse m_DllLibrary,int m_iCardNum)
    {
        if(!m_DllLibrary.User_RealtimeConnect(m_iCardNum))
        {
            System.out.println("实时发送数据建立连接失败！");
            return false;
        }
        else
        {
            System.out.println("实时发送数据建立连接成功！");
            return true;
        }
    }

    //函数：实时发送数据，发送数据
    //日期：2008-04-30
    public static void OnRealtimeSendData(EqCallUse m_DllLibrary,String m_strUserPath,int m_iCardNum,GDI32 GDI,User32 USER)
    {
        // 通过图片句柄添加图片
        HANDLE hBitmap = USER.LoadImage(null,           	// 模块实例句柄
                m_strUserPath + "\\res\\BMP1.bmp",// 位图路径
                USER.IMAGE_BITMAP,	// 位图类型
                64,   				// 指定图片宽
                32,  				// 指定图片高
                USER.LR_LOADFROMFILE);// 从路径处加载图片
        if(hBitmap != null)
        {
            if(false == m_DllLibrary.User_RealtimeSendData(m_iCardNum,0,0,64,32,hBitmap))
            {
                System.out.println("发送实时图片句柄失败！");
            }
            else
            {
                System.out.println("发送实时图片句柄成功！");
            }
            GDI.DeleteObject(hBitmap);
        }
        else
        {
            System.out.println("发送实时图片句柄失败！");
        }
    }

    //函数：实时发送图片路径
    //日期：2015-09-21
    public static void OnRealtimeSendBmpData(EqCallUse m_DllLibrary,int m_iCardNum,String m_strUserPath)
    {
        if(false == m_DllLibrary.User_RealtimeSendBmpData(m_iCardNum,0,0,64,32,m_strUserPath + "\\res\\BMP2.bmp"))
        {
            System.out.println("发送实时图片路径失败！");
        }
        else
        {
            System.out.println("发送实时图片路径成功！");
        }
    }

    //函数：实时发送文本内容
    //日期：2015-09-21
    public static void OnRealtimeSendText(EqCallUse m_DllLibrary, int m_iCardNum, List<DeviceHistory> DeviceHistorys)
    {
        String   strText = "";
        for(DeviceHistory history : DeviceHistorys){
            JSONObject jsonObject= JSON.parseObject(history.getCaptureValue());
            String tem = jsonObject.getString("tem");
            String hum = jsonObject.getString("hum");
            strText = strText+"时间："+history.getCaptureTime()+"  温度："+tem+"  湿度："+hum+"\n";
        }
        EqCallUse.User_FontSet FontInfo = new EqCallUse.User_FontSet();

        FontInfo.bFontBold = false;
        FontInfo.bFontItaic= false;
        FontInfo.bFontUnderline = false;
        FontInfo.colorFont = 0xFFFF;
        FontInfo.iFontSize = 12;
        FontInfo.strFontName = "宋体";
        FontInfo.iAlignStyle = 0;
        FontInfo.iVAlignerStyle = 0;
        FontInfo.iRowSpace = 0;

        if(!m_DllLibrary.User_RealtimeSendText(m_iCardNum,0,0,64,32,strText,FontInfo))
        {
            System.out.println("发送实时文本失败！");
        }
        else
        {
            System.out.println("发送实时文本成功！");
        }
    }

    //函数：实时发送数据，断开连接
    //日期：2008-04-30
    public static void OnRealtimeDisConnect(EqCallUse m_DllLibrary,int m_iCardNum)
    {
        if(!m_DllLibrary.User_RealtimeDisConnect(m_iCardNum))
        {
            System.out.println("实时发送数据断开连接失败！");
        }
        else
        {
            System.out.println("实时发送数据断开连接成功！");
        }
    }


    //函数：清空控制卡节目
    //日期：2015-09-21
    public static void OnRealtimeScreenClear(EqCallUse m_DllLibrary,int m_iCardNum)
    {
        if(!m_DllLibrary.User_RealtimeScreenClear(m_iCardNum))
        {
            System.out.println("清空控制卡节目失败！");
        }
        else
        {
            System.out.println("清空控制卡节目成功！");
        }
    }
}
