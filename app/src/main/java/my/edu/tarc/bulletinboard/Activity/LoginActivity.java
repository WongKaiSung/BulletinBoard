package my.edu.tarc.bulletinboard.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;

import my.edu.tarc.bulletinboard.MQTT.MqttHelper;
import my.edu.tarc.bulletinboard.R;
import my.edu.tarc.bulletinboard.SaveSharedPreference;
import my.edu.tarc.bulletinboard.Class.Student;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextID,editTextPassword;
    private AlertDialog dialog;
    private ProgressDialog pDialog;
    RequestQueue queue;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/StudentDetail.php";
    private static final String TAG = "Login";

    MqttHelper mqttHelper;
    public String USERID;
    ArrayList<Student> StudentArrayList = new ArrayList<Student>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pDialog = new ProgressDialog(this);
        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextPassword);
        StudentArrayList = new ArrayList<>();
        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }
        startMqtt();
    }
    private void requestData() {
        try {
            mqttHelper.publish("SC_BULLETIN_GET_USER,"+"USER"+",MY/TARUC/BBS/00005007");

        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005007");
    }
    private void startMqtt() {

        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                requestData();
//                Toast.makeText(getApplicationContext(), "Connected to MQTT", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                loadUserData(mqttMessage.toString(),getApplicationContext());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
    private void loadUserData(String data, Context context) throws Exception {

        String dataFromMQTT = data;
        String[] dataAfterProcess = dataFromMQTT.split(",");

        String topicFromMQTT = dataAfterProcess[0];
        String msgFromMQTT = dataAfterProcess[1];
        String idFromMQTT = dataAfterProcess[2];


            if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005007")) {
                retrieveIDPass(context, msgFromMQTT);
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            }


    }
    private void retrieveIDPass(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                response -> {
                    try {
                        StudentArrayList.clear();

                        for (int i = 0; i < response.length(); i++) {

                                JSONObject studentResponse = (JSONObject) response.get(i);

                                String UserID = studentResponse.getString("UserID");
                                String Name = studentResponse.getString("Name");
                                String ICNum = studentResponse.getString("ICNum");
                                String Contact = studentResponse.getString("Contact");
                                String Email = studentResponse.getString("Email");
                                String Address = studentResponse.getString("Address");
                                String UserType = studentResponse.getString("UserType");
                                String UserPassword = studentResponse.getString("UserPassword");
                                String ProgrammeID = studentResponse.getString("ProgrammeID");
                                String TutorialGroup = studentResponse.getString("TutorialGroup");
                                String INTAKE = studentResponse.getString("INTAKE");

                                Student student = new Student(UserID, Name, ICNum, Contact,Email,Address,UserType,UserPassword,ProgrammeID,TutorialGroup,INTAKE);
                                StudentArrayList.add(student);

                        }

                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                volleyError -> {
                    Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
    public void verifyAccount(View view) {

        if (editTextID.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Fill In UserID and password", Toast.LENGTH_SHORT).show();
        } else {

//            retrieveIDPass(getApplicationContext(), GET_URL);
                try {
                    Thread.sleep(2200);
                    boolean check = false;
                    for (int i = 0; i < StudentArrayList.size(); i++) {

                        USERID = StudentArrayList.get(i).getUserID();
                        if (editTextID.getText().toString().equalsIgnoreCase(USERID) && editTextPassword.getText().toString().equals(StudentArrayList.get(i).getUserPassword())) {
                            Toast.makeText(getApplicationContext(), "Welcome " + StudentArrayList.get(i).getName(), Toast.LENGTH_LONG).show();
                            SaveSharedPreference.setUserID(this, USERID);
                            // Success and proceed
                            finish();
                            Intent goToMenuNavi = new Intent(this, MainActivity.class);
    //                        goToMenuNavi.putExtra("passID",USERID );
                            startActivity(goToMenuNavi);
                            check = true;

                        }
                    }
                    if (check == false) {
                        editTextID.setText("");
                        editTextPassword.setText("");
                        Toast.makeText(getApplicationContext(), "Invalid ID or Password", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

    }
    }
}
