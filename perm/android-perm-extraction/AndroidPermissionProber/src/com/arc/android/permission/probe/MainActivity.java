package com.arc.android.permission.prober;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView info = (TextView) findViewById(R.id.info);
        
        info.setText("Probing permissions ... ");
        
        Map<String, List<String>> perms = probeDangerousPermissions();
        
		String jsonRepr =  new JSONObject(perms).toString();
		
		Log.i("PermissionProbe", jsonRepr);   
		
        info.setText("Done, use adb [-s device] logcat -v raw -s PermissionProbe:I"
        		+ " to view the JSON representation of Android permissions\n\n");
        
        info.append(jsonRepr);
    }

    public Map<String, List<String>> probeDangerousPermissions(){
        
    	Map<String, List<String>> perms = new HashMap<String, List<String>>();
    	
        PackageInfo packageInfo;
		try {
			packageInfo = getPackageManager().getPackageInfo("android", PackageManager.GET_PERMISSIONS);
	        if (packageInfo.permissions != null) {
	          for (PermissionInfo permission : packageInfo.permissions) {
	            if(permission.protectionLevel==PermissionInfo.PROTECTION_DANGEROUS){
	            	if(perms.containsKey(permission.group)){
	            		perms.get(permission.group).add(permission.name);
	            	} else{
	            		List<String> permList = new LinkedList<String>();
	            		permList.add(permission.name);
	            		perms.put(permission.group, permList);
	            	}
	            }
	          }   
	        }
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return perms;
    }
}
