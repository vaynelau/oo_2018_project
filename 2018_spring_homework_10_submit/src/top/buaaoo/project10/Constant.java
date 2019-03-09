package top.buaaoo.project10;

import java.util.regex.Pattern;

public interface Constant {
    /**
     * Overview: 保存程序中需要用到的一些常量值
     * 
     */
    static final String REQUEST = "\\[CR,\\((([0-9])|([1-7][0-9])),(([0-9])|([1-7][0-9]))\\),\\((([0-9])|([1-7][0-9])),(([0-9])|([1-7][0-9]))\\)\\]";
    static final Pattern pREQUEST = Pattern.compile(REQUEST);
    
    static final String MAP = "[0123]{80}";
    static final Pattern pMAP = Pattern.compile(MAP);
    
    static final String LIGHT = "[01]{80}";
    static final Pattern pLIGHT = Pattern.compile(MAP);
    
    static final String LOAD = "Load\\[.{1,}\\]";
    static final Pattern pLOAD = Pattern.compile(LOAD);

    static final String SET_ROAD = "(setRoadStatus\\(((([0-9])|([1-7][0-9])),){4}[01]\\);){0,4}(setRoadStatus\\(((([0-9])|([1-7][0-9])),){4}[01]\\))";
    static final Pattern pSET_ROAD = Pattern.compile(SET_ROAD);
    
    static final String SEARCH_TAXI_NUM = "searchTaxiNum\\((([0-9])|([1-9][0-9]))\\)";
    static final Pattern pSEARCH_TAXI_NUM = Pattern.compile(SEARCH_TAXI_NUM);

    static final String SEARCH_TAXI_STATUS = "searchTaxiStatus\\([0123]\\)";
    static final Pattern pSEARCH_TAXI_STATUS = Pattern.compile(SEARCH_TAXI_STATUS);
    
    
            
    static final int MAX_INPUT_LINES = 1000;

    static final int MAP_SIZE = 80;
    static final int TAXI_NUM = 100;
    static final int MATRIX_SIZE = 6400;

    static final int STOP = 0;
    static final int SERVE = 1;
    static final int WAIT = 2;
    static final int RECEIVE_ORDER = 3;
    static final int ORDER_RECEIVED = 3;

    static final int UP = 0;
    static final int DOWN = 1;
    static final int LEFT = 2;
    static final int RIGHT = 3;
}
