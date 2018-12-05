package com.skyseasoft.gltest2.server;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by junodeveloper on 15. 5. 30..
 */
public class JunoComConnector {
    public void upload(String title, String content) {
        try {
            URL url = new URL("http://skyseasoft.com/upload.php");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            PrintStream ps = new PrintStream(conn.getOutputStream());
            ps.print("title=" + title);
            ps.print("&content=" + content);
            ps.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line, response = "";
            while((line = br.readLine()) != null)
                response += line + "\n";
            Log.d("RESPONSE_FROM_PHP<up>", response);
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    public ArrayList<Post> getList() {
        ArrayList<Post> list = new ArrayList<Post>();
        String[] posts;
        String line, response = "";
        try {
            URL url = new URL("http://skyseasoft.com/list.php");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            PrintStream ps = new PrintStream(conn.getOutputStream());
            ps.print("status=1");
            ps.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while((line = br.readLine()) != null)
                response += line + "\n";
            Log.d("RESPONSE_FROM_PHP<list>", response);
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
        posts = response.split("#");
        Log.d("RESPONSE_FROM_PHP<len>", posts.length+"");
        for(int i=0;i<posts.length-1;i++) {
            if(posts[i].charAt(0)=='@')
                posts[i]="[Untitled]" + posts[i];
            if(posts[i].charAt(posts[i].length()-1)=='@')
                posts[i]+=" ";
            String[] token = posts[i].split("@");
            list.add(new Post(token[0], token[1]));
        }
        return list;
    }
}