package rsea.tool.resource.temp;

import java.util.HashMap;

public class ResourceCode {
    public static final int SUCCESS = 0;
    public static final int SERVER_ERROR 	= 9993;

    public static final int E8000 = 8000;				//WIFI NOT Connect
    public static final int E9993 = SERVER_ERROR;		//USER ERROR
    public static final int E9994 = 9994;				//HTTP Error
    public static final int E9995 = 9995;				//Connection Timeout
    public static final int E9996 = 9996;				//Socket Timeout
    public static final int E9997 = 9997;				//UnKnow Host
    public static final int E9998 = 9998;				//Parsing Error
    public static final int E9999 = 9999;				//UnKnown Error


    private static final HashMap<Integer,String> ERROR_MAP = new HashMap<Integer, String>();
    static{
        ERROR_MAP.put(E8000,"인터넷이 연결되어있지 않습니다.(0)");
        ERROR_MAP.put(E9994,"데이터 통신에 실패하였습니다.(4)");
        ERROR_MAP.put(E9995,"서버 연결시간 초과.(5)");
        ERROR_MAP.put(E9996,"죄송합니다. 네트워크가 불안정하여 연결이 원활하지 않습니다. 잠시 후 다시 접속해주십시오.(6)");
        ERROR_MAP.put(E9997,"서버를 찾을수 없습니다.(7)");
        ERROR_MAP.put(E9998,"데이터 변환시 오류발생.(8)");
        ERROR_MAP.put(E9999,"서버와의 통신이 원활하지 않습니다.(9)");

    }
    public static String getErrorMsg(int errorCode){
        return ERROR_MAP.get(errorCode);
    }

    public static boolean containDefaultError(int errorCode){
        return ERROR_MAP.containsKey(errorCode);
    }
}
