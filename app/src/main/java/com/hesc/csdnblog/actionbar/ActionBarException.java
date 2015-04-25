package com.hesc.csdnblog.actionbar;

/**
 * Created by hesc on 15/4/24.
 */
public class ActionBarException extends  Exception {
    public ActionBarException(){
        super();
    }

    public ActionBarException(String detailMessage){
        super(detailMessage);
    }

    public ActionBarException(String detailMessage, Throwable throwable){
        super(detailMessage, throwable);
    }

    public ActionBarException(Throwable throwable){
        super(throwable);
    }
}
