package com.ulearning.ulms.hk.service;

import com.alibaba.fastjson.JSON;
import com.ulearning.ulms.hk.HCNetSDK;
import com.ulearning.ulms.hk.config.SdkInit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author chenyu
 * @desc 客流量上传
 * @datetime 2021年11月03日 11:53
 * Copyright (c) 2006-2021.Beijing WenHua Online Sci-Tech Development Co. Ltd
 * All rights reserved.
 */
@Slf4j
@Service
public class MemberFlowUploadService {

    static HCNetSDK hCNetSDK = null;
    static int lUserID;
    static HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
    static HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息
    static HCNetSDK.NET_DVR_DEVICEINFO_V30 m_deviceInfo30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
    static HCNetSDK.NET_DVR_DEVICEINFO m_deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO();



    @PostConstruct
    public void init() throws IOException {
        try {
            hCNetSDK = SdkInit.init(hCNetSDK);
        } catch (Exception e) {
            log.error("Sdk Init Failed: {}", e.getMessage());
        }
    }


    public void initMemberFlowUpload(String m_sDeviceIP, String m_sUsername, String m_sPassword, short m_sPort) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        // 初始化
        hCNetSDK.NET_DVR_Init();
        hCNetSDK.NET_DVR_SetLogToFile(3, ResourceUtils.getFile("hkLog").getPath(), false);
        //设置连接时间与重连时间
        hCNetSDK.NET_DVR_SetConnectTime(2000, 1);
        hCNetSDK.NET_DVR_SetReconnect(10000, true);
        // 注册设备-登录参数，包括设备地址、登录用户、密码等
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());
        m_strLoginInfo.wPort = m_sPort;
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        m_strLoginInfo.write();
        //设备信息, 输出参数
        // int lUserID = hCNetSDK.NET_DVR_Login_V30(m_sDeviceIP, m_sPort, m_sUsername, m_sPassword, m_deviceInfo30);
        lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        System.out.println("lUserID.size-->" + lUserID);
        if(lUserID < 0){
            System.out.println("hCNetSDK.NET_DVR_Login_V30()"+"\n" + hCNetSDK.NET_DVR_GetErrorMsg(null));
            hCNetSDK.NET_DVR_Cleanup();
        }
        //设置报警回调函数
        hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(new MemberFlowUploadCallBackImpl(), null);
        //启用布防-其他报警布防参数不需要设置，不支持
        HCNetSDK.NET_DVR_SETUPALARM_PARAM lpSetupParam = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
        lpSetupParam.dwSize = 0;
        int lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID,lpSetupParam);
        if (lAlarmHandle< 0)
        {
            System.out.println("NET_DVR_SetupAlarmChan_V41 error, \n" + hCNetSDK.NET_DVR_GetErrorMsg(null));
            hCNetSDK.NET_DVR_Logout(lUserID);
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }

        long endTime = System.currentTimeMillis();

        //等待过程中，如果设备上传报警信息，在报警回调函数里面接收和处理报警信息
        Timer timer = new Timer();// 实例化Timer类
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //撤销布防上传通道
                if (! hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle))
                {
                    System.out.println("! hCNetSDK.NET_DVR_CloseAlarmChan_V31(lAlarmHandle)\n" + hCNetSDK.NET_DVR_GetLastError() +"\n" + hCNetSDK.NET_DVR_GetErrorMsg(null) );
                    hCNetSDK.NET_DVR_Logout(lUserID);
                    hCNetSDK. NET_DVR_Cleanup();
                    return;
                }

                //注销用户
                hCNetSDK.NET_DVR_Logout(lUserID);
                //释放SDK资源
                hCNetSDK.NET_DVR_Cleanup();
                this.cancel();
                System.gc();//主动回收垃圾
                log.warn("User Logout Success...");
            }
        }, 5 * 60 * 1000 - (endTime - startTime) - 1000);// 这里提前1秒执行, 是以防定时线程启动时将其用户注销了...
    }


    public static final int ISAPI_DATA_LEN = 1024*1024;
    public static final int ISAPI_STATUS_LEN = 4*4096;



    /**
     * 搜索
     * @return
     */
    public String countingSearch() {
        HCNetSDK.NET_DVR_XML_CONFIG_INPUT struXMLInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
        struXMLInput.read();
        struXMLInput.dwSize = struXMLInput.size();
        String strURL = "POST /ISAPI/System/Video/inputs/channels/1/counting/search";
        int iURLlen = strURL.length();
        HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(iURLlen+1);
        System.arraycopy(strURL.getBytes(), 0, ptrUrl.byValue, 0, strURL.length());
        ptrUrl.write();
        struXMLInput.lpRequestUrl = ptrUrl.getPointer();
        struXMLInput.dwRequestUrlLen = iURLlen;
        String strInbuffer = "<CountingStatisticsDescription version=\"2.0\" xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\n" +
                "  <statisticType>\n" +
                "    enterExitDuplicate\n" +
                "  </statisticType>\n" +
                "  <reportType>\n" +
                "    daily\n" +
                "  </reportType>\n" +
                "  <timeSpanList>\n" +
                "    <timeSpan>\n" +
                "      <startTime>2024-12-09T00:00:00</startTime>\n" +
                "      <endTime>2024-12-09T23:59:59</endTime>\n" +
                "    </timeSpan>\n" +
                "  </timeSpanList>\n" +
                "</CountingStatisticsDescription>";
        int iInBufLen = strInbuffer.length();
        if(iInBufLen==0)
        {
            struXMLInput.lpInBuffer=null;
            struXMLInput.dwInBufferSize=0;
            struXMLInput.write();
        }
        else
        {
            HCNetSDK.BYTE_ARRAY ptrInBuffer = new HCNetSDK.BYTE_ARRAY(iInBufLen+1);
            ptrInBuffer.read();
            ptrInBuffer.byValue = strInbuffer.getBytes();
            ptrInBuffer.write();

            struXMLInput.lpInBuffer = ptrInBuffer.getPointer();
            struXMLInput.dwInBufferSize = iInBufLen;
            struXMLInput.write();

        }
        HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(ISAPI_STATUS_LEN);
        ptrStatusByte.read();

        HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(ISAPI_DATA_LEN);
        ptrOutByte.read();

        HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struXMLOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
        struXMLOutput.read();
        struXMLOutput.dwSize = struXMLOutput.size();
        struXMLOutput.lpOutBuffer = ptrOutByte.getPointer();
        struXMLOutput.dwOutBufferSize = ptrOutByte.size();
        struXMLOutput.lpStatusBuffer = ptrStatusByte.getPointer();
        struXMLOutput.dwStatusSize  = ptrStatusByte.size();
        struXMLOutput.write();

        if(!hCNetSDK.NET_DVR_STDXMLConfig(lUserID, struXMLInput, struXMLOutput))
        {
            int iErr = hCNetSDK.NET_DVR_GetLastError();
            log.error( "NET_DVR_STDXMLConfig失败，错误号：" + iErr);
            return null;

        }
        else
        {
            struXMLOutput.read();
            ptrOutByte.read();
            ptrStatusByte.read();
            // 输出结果
            String strOutXML = new String(ptrOutByte.byValue).trim();
            System.out.println("strOutXML: " + strOutXML);
            // 输出状态
            String strStatus = new String(ptrStatusByte.byValue).trim();
            System.out.println("strStatus: " + strStatus);
            return strOutXML;
        }
    }

    /**
     * 搜索
     * @return
     */
    public String countingSearchCapabilities() {
        HCNetSDK.NET_DVR_XML_CONFIG_INPUT struXMLInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
        struXMLInput.read();
        struXMLInput.dwSize = struXMLInput.size();
        String strURL = "GET /ISAPI/System/Video/inputs/channels/1/counting/search/capabilities";
        int iURLlen = strURL.length();
        HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(iURLlen+1);
        System.arraycopy(strURL.getBytes(), 0, ptrUrl.byValue, 0, strURL.length());
        ptrUrl.write();
        struXMLInput.lpRequestUrl = ptrUrl.getPointer();
        struXMLInput.dwRequestUrlLen = iURLlen;
        String strInbuffer = "";
        int iInBufLen = strInbuffer.length();
        if(iInBufLen==0)
        {
            struXMLInput.lpInBuffer=null;
            struXMLInput.dwInBufferSize=0;
            struXMLInput.write();
        }
        else
        {
            HCNetSDK.BYTE_ARRAY ptrInBuffer = new HCNetSDK.BYTE_ARRAY(iInBufLen+1);
            ptrInBuffer.read();
            ptrInBuffer.byValue = strInbuffer.getBytes();
            ptrInBuffer.write();

            struXMLInput.lpInBuffer = ptrInBuffer.getPointer();
            struXMLInput.dwInBufferSize = iInBufLen;
            struXMLInput.write();

        }
        HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(ISAPI_STATUS_LEN);
        ptrStatusByte.read();

        HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(ISAPI_DATA_LEN);
        ptrOutByte.read();

        HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struXMLOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
        struXMLOutput.read();
        struXMLOutput.dwSize = struXMLOutput.size();
        struXMLOutput.lpOutBuffer = ptrOutByte.getPointer();
        struXMLOutput.dwOutBufferSize = ptrOutByte.size();
        struXMLOutput.lpStatusBuffer = ptrStatusByte.getPointer();
        struXMLOutput.dwStatusSize  = ptrStatusByte.size();
        struXMLOutput.write();

        if(!hCNetSDK.NET_DVR_STDXMLConfig(lUserID, struXMLInput, struXMLOutput))
        {
            int iErr = hCNetSDK.NET_DVR_GetLastError();
            log.error( "NET_DVR_STDXMLConfig失败，错误号：" + iErr);
            return null;

        }
        else
        {
            struXMLOutput.read();
            ptrOutByte.read();
            ptrStatusByte.read();
            // 输出结果
            String strOutXML = new String(ptrOutByte.byValue).trim();
            System.out.println("GET search capabilities strOutXML: " + strOutXML);
            // 输出状态
            String strStatus = new String(ptrStatusByte.byValue).trim();
            return strOutXML;
        }
    }

}
