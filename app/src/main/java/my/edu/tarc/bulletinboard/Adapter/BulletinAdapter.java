package my.edu.tarc.bulletinboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.bulletinboard.Activity.MainActivity;
import my.edu.tarc.bulletinboard.Activity.BulletinDetails;
import my.edu.tarc.bulletinboard.Class.Bulletin;
import my.edu.tarc.bulletinboard.Class.BulletinDetail;
import my.edu.tarc.bulletinboard.Class.EventBulletin;
import my.edu.tarc.bulletinboard.Class.EventRegistration;
import my.edu.tarc.bulletinboard.Fragment.HomeFragment;
import my.edu.tarc.bulletinboard.MQTT.MqttHelper;
import my.edu.tarc.bulletinboard.R;
import my.edu.tarc.bulletinboard.SaveSharedPreference;

/**
 * Created by but on 17/1/2018.
 */

public class BulletinAdapter extends RecyclerView.Adapter<BulletinAdapter.ViewHolder> {
    Context context;
    public static int counter = 0;
    public static ArrayList<Bulletin> bulletinArrayList = new ArrayList<Bulletin>();
    public static ArrayList<BulletinDetail> bulletinDetail = new ArrayList<>();
    public static ArrayList<EventRegistration> EventRegistrationArrayList = new ArrayList<>();
    public ArrayList<EventBulletin> EventBulletinArrayList = new ArrayList<>();

    MqttHelper mqttHelper;
    public static ArrayList<Bulletin> bulletinsSelectionList = new ArrayList<>();
    public static boolean clickStatus = true;
    public static boolean selectAllStatus = false;
    public static String bulletinID;
    public BulletinAdapter(ArrayList<Bulletin> bulletinArrayList, Context context) {
        this.bulletinArrayList = bulletinArrayList;


    }
    public BulletinAdapter(ArrayList<Bulletin> bulletinArrayList,ArrayList<BulletinDetail> bulletinDetail,ArrayList<EventRegistration> EventRegistrationArrayList, ArrayList<EventBulletin> EventBulletinArrayList, Context context) {
        this.bulletinArrayList = bulletinArrayList;
        this.bulletinDetail = bulletinDetail;
        this.EventRegistrationArrayList = EventRegistrationArrayList;
        this.EventBulletinArrayList = EventBulletinArrayList;
        this.context=context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder;

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_layout, parent,false);
            viewHolder = new ViewHolder(itemView, context, bulletinArrayList,bulletinDetail,EventRegistrationArrayList,EventBulletinArrayList, viewType);

        return viewHolder;
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bulletin bulletin = bulletinArrayList.get(position);
        BulletinDetail bulletinDetailList = bulletinDetail.get(position);
        holder.textViewTitle.setText(bulletin.getBulletinTitle());
        holder.textViewDetails.setText(bulletin.getDescription());
        holder.textViewDate.setText(String.format("%1$tY %1$tb %1$td",bulletin.getPostDate()));
        holder.textViewPostedBy.setText(bulletin.getPostedBy());
        holder.imageViewRead.setVisibility(View.GONE);

        if (bulletin.getDescription().length() >= 45) {
            holder.textViewDetails.setText(bulletin.getDescription().substring(0, 45) + "...");
        } else {
            holder.textViewDetails.setText(bulletin.getDescription());
        }
//        if(!MainActivity.is_in_action_mode){
//            holder.checkBoxBookmark.setVisibility(View.GONE);
//
//        }else{
//            holder.checkBoxBookmark.setVisibility(View.VISIBLE);
//            holder.checkBoxBookmark.setChecked(false);
//
//        }
//        if (selectAllStatus){
//            holder.checkBoxBookmark.setChecked(true);
//        }else
//            holder.checkBoxBookmark.setChecked(false);
        if (bulletinDetailList.getBookmarkStatus() == 1){
            holder.checkBoxBookmark.setChecked(true);
            holder.imageButtonDelete.setVisibility(View.GONE);
        }
        if (bulletinDetailList.getReadStatus() == 0){
//            holder.imageViewRead.setVisibility(View.VISIBLE);
            holder.textViewTitle.setTextColor(Color.RED);
        }


    }


    @Override
    public int getItemCount() {

        return bulletinArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        public final static int LAYOUT_ONE = 1;
        public final static int LAYOUT_TWO = 2;
        public TextView textViewTitle,textViewDetails,textViewDate,textViewPostedBy;
        public ImageView imageViewRead;
        public ImageButton imageButtonDelete;
        ArrayList<Bulletin> bulletins = new ArrayList<>();

        ArrayList<BulletinDetail> bulletinDetails = new ArrayList<>();
        ArrayList<EventRegistration> EventRegistrations = new ArrayList<>();
        ArrayList<EventBulletin> EventBulletins = new ArrayList<>();
        CheckBox checkBoxBookmark;
        Context context;
        RelativeLayout bulletinlayout;


        public ViewHolder(View itemView, Context context, ArrayList<Bulletin> bulletins, ArrayList<BulletinDetail> bulletinDetails, ArrayList<EventRegistration> EventRegistrations, ArrayList<EventBulletin> EventBulletins, int layoutType){
            super(itemView);
            this.bulletins = bulletins;
            this.bulletinDetails = bulletinDetails;
            this.EventRegistrations = EventRegistrations;
            this.EventBulletins = EventBulletins;
            this.context = context;
            startMqtt(context);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                imageViewRead = itemView.findViewById(R.id.imageViewRead);
                textViewPostedBy= itemView.findViewById(R.id.textViewPostedBy);
                textViewTitle = itemView.findViewById(R.id.textViewTitle);
                textViewDetails = itemView.findViewById(R.id.textViewDetails);
                checkBoxBookmark = itemView.findViewById(R.id.checkBoxBookmark);
                bulletinlayout = itemView.findViewById(R.id.bulletinlayout);
                textViewDate = itemView.findViewById(R.id.textViewDate);
                imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);
                checkBoxBookmark.setOnClickListener(this);
                imageButtonDelete.setOnClickListener(this);
                MainActivity.checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view == itemView) {
//                if(clickStatus == true) {
                    Bulletin bulletin = bulletins.get(position);
                    BulletinDetail bulletinDetail = bulletinDetails.get(position);
//                    Toast.makeText(context,position+bulletins.get(position).getBulletinID(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this.context, BulletinDetails.class);
                    intent.putExtra("bulletin", bulletin);
                    intent.putExtra("bulletinDetail", bulletinDetail);
                    this.context.startActivity(intent);

//                }
            }
            else if(view == checkBoxBookmark){
                bulletinID = bulletins.get(position).getBulletinID();
                if (checkBoxBookmark.isChecked()){
                    requestBookmarkLink();
                }else {
                    requestUnMarkLink();
                }

            }else if(view == imageButtonDelete){
                bulletinID = bulletins.get(position).getBulletinID();
                new AlertDialog.Builder(context)
                    .setTitle("Delete Bulletin")
                    .setMessage("Are you sure want to delete this bulletin?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                        requestDeleteLink();
//                        Toast.makeText(context, "Bulletin deleted!", Toast.LENGTH_SHORT).show();

                    })
                    .setNegativeButton(android.R.string.no, null).show();


            }
//            else if (view == MainActivity.checkBox){
//
//                if (MainActivity.checkBox.isChecked()){
//                    counter = 0;
//                    selectAllStatus = true;
//                    for (int i = 0; i< HomeFragment.bulletinAdapter.getItemCount(); i++){
//                        BulletinAdapter.bulletinsSelectionList.add(bulletins.get(i));
//                        counter =  counter + 1;
//                    }
//                }else {
//                    selectAllStatus = false;
//                    for (int i=0;i<HomeFragment.bulletinAdapter.getItemCount();i++){
//                        BulletinAdapter.bulletinsSelectionList.remove(bulletins.get(i));
//                        counter =  counter - 1;
//                    }
//                }
//                updateCounter(counter);
//                notifyDataSetChanged();
//            }
        }
        private void startMqtt(final Context context) {
            mqttHelper = new MqttHelper(context);
            mqttHelper.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {
                }

                @Override
                public void connectionLost(Throwable throwable) {
//                Toast.makeText(context,"Lost",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                    Log.w("Debug", mqttMessage.toString());
//                setMessageNotification("New Bulletin", new String(mqttMessage.getPayload()));
//                onRefresh();
//                testData(mqttMessage.toString(),context);
                    getReplyMessage(mqttMessage.toString(), context);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        }
        private void requestUnMarkLink() {
            try {
                mqttHelper.publish("SC_BULLETIN_UNMARK,"+SaveSharedPreference.getUserID(context)+",MY/TARUC/BBS/00005006");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005006");
        }
        private void requestBookmarkLink() {
            try {
                mqttHelper.publish("SC_BULLETIN_BOOKMARK,"+ SaveSharedPreference.getUserID(context)+",MY/TARUC/BBS/00005005");

            } catch (MqttException e) {
                e.printStackTrace();
            }
            mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005005");
        }
        private void requestDeleteLink() {
            try {
                mqttHelper.publish("SC_BULLETIN_DELETE,"+SaveSharedPreference.getUserID(context)+",MY/TARUC/BBS/00005004");

            } catch (MqttException e) {
                e.printStackTrace();
            }
            mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005004");
        }
        public void getReplyMessage(String data, Context context) throws Exception {
            String dataFromMQTT = data;
            String[] dataAfterProcess = dataFromMQTT.split(",");

            String topicFromMQTT = dataAfterProcess[0];
            String msgFromMQTT = dataAfterProcess[1];
            String idFromMQTT = dataAfterProcess[2];
//        JSONObject jsonMsg = new JSONObject(data.toString());

            //only looking for receive bulletin command
            if ((SaveSharedPreference.getUserID(context).equalsIgnoreCase(idFromMQTT))) {
                if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005005")) {//bookmark bulletin
                    makeServiceCall(context, msgFromMQTT, SaveSharedPreference.getUserID(context), bulletinID);
                   Toast.makeText(context, "Bookmark successful!", Toast.LENGTH_SHORT).show();
//                    mqttHelper.unSubscribeToTopic("MY/TARUC/BBS/00005005");

                } else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005006")) {//unMark bulletin
                    makeServiceCall(context, msgFromMQTT, SaveSharedPreference.getUserID(context), bulletinID);
                    Toast.makeText(context, "Removed Bookmark!", Toast.LENGTH_SHORT).show();

                }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005004")) {//delete bulletin
                    makeServiceCall(context, msgFromMQTT, SaveSharedPreference.getUserID(context), bulletinID);
                    Toast.makeText(context, "Deleted Bulletin!", Toast.LENGTH_SHORT).show();

                }
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            }
        }
        public void makeServiceCall(final Context context, String url,final String UserID,final String BulletinID) {
            //mPostCommentResponse.requestStarted();
            RequestQueue queue = Volley.newRequestQueue(context);

            //Send data
            try {
                StringRequest postRequest = new StringRequest(
                        Request.Method.POST,
                        url,
                        response -> {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success==0) {
                                    //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                }else{
                                    //Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> Toast.makeText(context, "Error. " + error.toString(), Toast.LENGTH_LONG).show()) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("UserID", UserID);
                        params.put("BulletinID", BulletinID);

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        return params;
                    }
                };
                queue.add(postRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public boolean onLongClick(View view){
//            MainActivity.toolbar.getMenu().clear();
//            MainActivity.toolbar.inflateMenu(R.menu.menu_action_mode);
//
//            MainActivity.textViewCount.setVisibility(View.VISIBLE);
//            MainActivity.checkBox.setVisibility(View.VISIBLE);
//            MainActivity.imageButton.setVisibility(View.VISIBLE);
//            MainActivity.textViewTitle.setVisibility(View.GONE);
//            MainActivity.is_in_action_mode = true;
//            HomeFragment.bulletinAdapter.notifyDataSetChanged();
//            clickStatus = false;

            return true;
        }
        public void prepareSelection(View view,int position){

            if (((checkBoxBookmark)).isChecked()){
//                bulletinlayout.setBackgroundColor(Color.LTGRAY);
                BulletinAdapter.bulletinsSelectionList.add(bulletins.get(position));
                counter =  counter + 1;
                updateCounter(counter);
            }else{
                if(selectAllStatus = true){
                    MainActivity.checkBox.setChecked(false);
                }else {
//                    bulletinlayout.setBackgroundColor(Color.WHITE);
                    BulletinAdapter.bulletinsSelectionList.remove(bulletins.get(position));
                }
                counter = counter - 1;
                updateCounter(counter);

            }
        }
        public void updateCounter(int counter){
            if (counter == 0){
                MainActivity.textViewCount.setText("0 item");

            }else {
                MainActivity.textViewCount.setText(counter + " items");
            }
        }

    }
    public void setFilter(ArrayList<Bulletin> newList){
        bulletinArrayList = new ArrayList<>();
        bulletinArrayList.addAll(newList);
        notifyDataSetChanged();

    }


}
