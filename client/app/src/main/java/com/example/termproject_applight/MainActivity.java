package com.example.termproject_applight;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_TEST = 1;
    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    TextView powerStatus, colorStatus, timeStatus;
    Button powerButton, redButton, greenButton, blueButton, yelloButton, whiteButton, oneHour, halfHour, quarterHour, noTime, saveButton, viewButton, mode;

    String name = "";
    String power = "";

    String color = "";

    String time = "";

    Socket socket = null;

    int modeNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();

        powerStatus = findViewById(R.id.powerStatus);
        colorStatus = findViewById(R.id.colorStatus);
        timeStatus = findViewById(R.id.timeStatus);

        powerButton = findViewById(R.id.powerButton);

        redButton = findViewById(R.id.redButton);
        greenButton = findViewById(R.id.greenButton);
        blueButton = findViewById(R.id.blueButton);
        yelloButton = findViewById(R.id.yelloButton);
        whiteButton = findViewById(R.id.whiteButton);

        oneHour = findViewById(R.id.oneHour);
        halfHour = findViewById(R.id.halfHour);
        quarterHour = findViewById(R.id.quarterHour);
        noTime = findViewById(R.id.noTime);
        saveButton = findViewById(R.id.saveSetting);
        viewButton = findViewById(R.id.viewSetting);
        mode = findViewById(R.id.gestureMode);

        String ipAddress = "172.30.1.59";


        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (powerButton.getText().equals("OFF")) {
                    powerButton.setText("ON");
                    powerStatus.setText("전원: ON");
                    power = "p1";
                    MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, power);
                    myClientTask.execute();
                } else if (powerButton.getText().equals("ON")) {
                    powerButton.setText("OFF");
                    powerStatus.setText("전원: OFF");

                    colorStatus.setText("색상: ");

                    timeStatus.setText("시간: ");

                    power = "p0";
                    MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, power);
                    myClientTask.execute();
                }
            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorStatus.setText("색상: red");
                color = "c4";
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, color);
                myClientTask.execute();

            }
        });

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorStatus.setText("색상: green");
                color = "c2";
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, color);
                myClientTask.execute();

            }
        });


        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorStatus.setText("색상: blue");
                color = "c5";
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, color);
                myClientTask.execute();
            }
        });

        yelloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorStatus.setText("색상: yellow");
                color = "c3";
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, color);
                myClientTask.execute();
            }
        });

        whiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorStatus.setText("색상: white");
                color = "c1";
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, color);
                myClientTask.execute();
            }
        });

        oneHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStatus.setText("시간: 60분");
                time = "t6";
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, time);
                myClientTask.execute();

            }
        });
        halfHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStatus.setText("시간: 30분");
                time = "t3";
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, time);
                myClientTask.execute();

            }
        });
        quarterHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStatus.setText("시간: 15분");
                time = "t1";
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, time);
                myClientTask.execute();

            }
        });

        noTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeStatus.setText("시간: ");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color = colorStatus.getText().toString();

                if (color.equals("색상: white")) {
                    name = "c1";
                    color = "white";
                } else if (color.equals("색상: green")) {
                    name = "c2";
                    color = "green";
                } else if (color.equals("색상: red")) {
                    name = "c4";
                    color = "red";
                } else if (color.equals("색상: blue")) {
                    name = "c5";
                    color = "blue";
                } else if (color.equals("색상: yellow")) {
                    name = "c3";
                    color = "yellow";
                }

                name = name + " ";

                time = timeStatus.getText().toString();
                if (time.equals("시간: 60분")) {
                    name = name + "t6";
                    time = "60분";
                } else if (time.equals("시간: 30분")) {
                    name = name + "t3";
                    time = "30분";
                } else if (time.equals("시간: 15분")) {
                    name = name + "t1";
                    time = "10분";
                } else if (time.equals("시간: ")) {
                    time = "";
                }

                insert(name, color, time);

            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, REQUEST_TEST);
            }
        });

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String modeMsg;
                if (modeNum == -1) {
                    modeMsg = "0";
                    modeNum = 0;
                    mode.setText("리모컨 모드");
                } else {
                    modeMsg = "-1";
                    modeNum = -1;
                    mode.setText("제스쳐 모드");
                }
                MyClientTask myClientTask = new MyClientTask(ipAddress, 7777, modeMsg);
                myClientTask.execute();

            }
        });

    }

    public void insert(String name, String color, String time) {
        db.execSQL("INSERT INTO setting VALUES ('" + name + "', '" + color + "', '" + time + "');");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            if (resultCode == 1) {
                name = data.getStringExtra("setting");
                color = data.getStringExtra("color");
                time = data.getStringExtra("time");

                colorStatus.setText("색상: " + color);
                timeStatus.setText("시간: " + time);

            } else {

            }
        }
        MyClientTask myClientTask = new MyClientTask("172.30.1.59", 7777, name);
        myClientTask.execute();

    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        String myMessage = "";

        //constructor
        MyClientTask(String addr, int port, String message) {
            dstAddress = addr;
            dstPort = port;
            myMessage = message;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            myMessage = myMessage.toString();
            try {
                socket = new Socket(dstAddress, dstPort);
                //송신
                OutputStream out = socket.getOutputStream();
                out.write(myMessage.getBytes());

                //수신
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                /*
                 * notice:
                 * inputStream.read() will block if no data return
                 */
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                response = "서버의 응답: " + response;

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}