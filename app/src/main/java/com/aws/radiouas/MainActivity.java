package com.aws.radiouas;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.longtailvideo.jwplayer.JWPlayerFragment;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.fullscreen.DefaultFullscreenHandler;
import com.longtailvideo.jwplayer.fullscreen.FullscreenHandler;
import com.longtailvideo.jwplayer.fullscreen.MaximizingFullscreenHandler;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private String program = "Radio UAS 96.1FM";

    private String message = "";

    private EditText userName;

    private EditText userMessage;

    private TextView programName;

    private TextView serverResponse;

    private final String videoURL = "http://video7.stream.mx:1935/8030/8030/playlist.m3u8";

    private final String programURL = "http://www.radiouas.org/wp-content/plugins/programacion/current_program.php";

    private final String messagesURL = "http://www.radiouas.org/messages/inbox.php";

    //public static String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        this.setContentView(R.layout.activity_main);

        this.programName = (TextView) findViewById(R.id.programName);
        this.serverResponse = (TextView) findViewById(R.id.serverMessage);
        this.getProgram();



        LinearLayout PlayerContainerLayout = (LinearLayout)findViewById(R.id.PlayerContainer);
        ViewGroup.LayoutParams params = PlayerContainerLayout.getLayoutParams();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        params.height = metrics.widthPixels / 16 * 9;
        PlayerContainerLayout.setLayoutParams(params);


        // JW Player
        JWPlayerFragment fragment = (JWPlayerFragment) getFragmentManager().findFragmentById(R.id.appPlayer);
        fragment.setFullscreenOnDeviceRotate(false);
        JWPlayerView playerView = fragment.getPlayer();
        PlaylistItem video = new PlaylistItem(videoURL);
        playerView.setFullscreen(false, false);
        //playerView.setSkin("http://www.radiouas.org/resources/RadioUASTVSkin.css");


        // a ver si con esto puedo manejar el fullscreen
        DefaultFullscreenHandler fullscreenHandler = new DefaultFullscreenHandler(this, playerView){
            @Override
            public void onFullscreenRequested(){
                // hay que calcular el alto de la pantalla en modo landscape y asignarlo a LinearLayout#PlayerContainer
            }
            @Override
            public void onFullscreenExitRequested(){
                // hay que regresar a LinearLayout#PlayerContainer el valor original
            }
        };



        playerView.load(video);
    }

    public void sendMessage(View view) {
        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        userName = (EditText) findViewById(R.id.userName);
        userMessage = (EditText) findViewById(R.id.userMessage);

        String name = userName.getText().toString();
        String message = userMessage.getText().toString();

        /* Send HTTP POST Request */
        this.sendMessageToServer(name, message);
    }

    private void sendMessageToServer(final String n, final String m){
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection;
                try  {
                    ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
                    if(networkInfo != null && networkInfo.isConnected()) {
                        URL url = new URL(messagesURL);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            urlConnection.setDoOutput(true);
                            String data = "n=" + URLEncoder.encode(n,"UTF-8") + "&m=" + URLEncoder.encode(m, "UTF-8");
                            urlConnection.setFixedLengthStreamingMode(data.getBytes().length);
                            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                            //Send request
                            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());
                            wr.writeBytes(data);
                            wr.flush();
                            wr.close();

                            //Get Response
                            InputStream is = urlConnection.getInputStream();
                            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                            String line;
                            StringBuffer response = new StringBuffer();
                            while((line = rd.readLine()) != null) {
                                response.append(line);
                                response.append("\r");
                            }
                            rd.close();
                            message = response.toString();
                            urlConnection.disconnect();
                        }
                        catch(Exception e){
                            message = e.getMessage();
                        }
                        finally {
                            urlConnection.disconnect();
                        }
                    }
                    else {
                        // No internet connection
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkThread.start();
        try {
            networkThread.join();
        } catch(InterruptedException e){
            this.serverResponse.setText(e.getMessage());
        }
        this.userName.setEnabled(false);
        this.userMessage.setText("");
        this.userMessage.clearFocus();
        this.serverResponse.setText(this.message);
    }

    private void getProgram(){
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection;
                try  {
                    ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
                    if(networkInfo != null && networkInfo.isConnected()) {
                        try {
                            URL url = new URL(programURL);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                            program = InputStreamToString.getString(stream);
                            urlConnection.disconnect();
                        }
                        catch(Exception e){
                            program = e.getMessage();
                        }
                    }
                    else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkThread.start();
        try {
            networkThread.join();
        } catch(InterruptedException e){
            this.programName.setText(e.getMessage());
        }
        this.programName.setText(this.program);
    }
}