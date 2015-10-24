package com.kinth.football.chat.listener;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;
import android.util.Log;

import com.kinth.football.bean.User;
import com.kinth.football.chat.ConnectionImp;
import com.kinth.football.chat.XmppManager;
import com.kinth.football.manager.UserManager;

public class TaxiConnectionListener implements ConnectionListener {  
    private Timer tExit = new Timer();  
    private int logintime = 2000;  
    private Context context;
    
    public TaxiConnectionListener(Context context) {
    	this.context  = context;
	}

	@Override  
    public void connectionClosed() {  
		Log.e("connectionClosed","connectionClosed"); 
        // 關閉連接  
        //XmppManager.getInstance(context).closeConnection();  
        // 重连服务器  
        /*tExit = new Timer();  
        tExit.schedule(new timetask(), logintime);  */
    }  
  
    @Override  
    public void connectionClosedOnError(Exception e) {  
        // 判斷為帳號已被登錄  
        boolean error = e.getMessage().equals("stream:error (conflict)");  
        if (!error) {  
            // 關閉連接  
        	//XmppManager.getInstance(context).closeConnection();  
            // 重连服务器  
            tExit.schedule(new timetask(), logintime);  
        }  
    }  
  
    class timetask extends TimerTask {  
    	
        @Override  
        public void run() {
                // 连接服务器   Config
                User user = UserManager.getInstance(context).getCurrentUser();
        		if (user == null || user.getPlayer() == null) {
        			return;
        		}
        		XmppManager.getInstance(context).loginAndAddChatManagerListener(
        				context, user.getPlayer().getAccountName(), user.getToken(),
        				new ConnectionImp() {
        					@Override
        					public void onSucc() {
        					}

							@Override
        					public void onFail() {
        						tExit.schedule(new timetask(), logintime);  
        					}
        				});
        }  
    }  
  
    @Override  
    public void reconnectingIn(int arg0) {  
    }  
  
    @Override  
    public void reconnectionFailed(Exception arg0) {  
    }  
  
    @Override  
    public void reconnectionSuccessful() {  
    }

	@Override
	public void authenticated(XMPPConnection arg0, boolean arg1) {
	}

	@Override
	public void connected(XMPPConnection arg0) {
	}  
	
}
