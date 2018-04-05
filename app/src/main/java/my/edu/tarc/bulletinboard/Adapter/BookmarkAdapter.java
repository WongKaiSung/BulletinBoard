//package my.edu.tarc.bulletinboard.Adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//import my.edu.tarc.bulletinboard.Activity.MainActivity;
//import my.edu.tarc.bulletinboard.Fragment.BookmarkFragment;
//import my.edu.tarc.bulletinboard.Activity.BulletinDetails;
//import my.edu.tarc.bulletinboard.Class.Bulletin;
//import my.edu.tarc.bulletinboard.Class.BulletinDetail;
//import my.edu.tarc.bulletinboard.Class.EventBulletin;
//import my.edu.tarc.bulletinboard.Class.EventRegistration;
//import my.edu.tarc.bulletinboard.R;
//
///**
// * Created by but on 17/1/2018.
// */
//
//public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
//    Context context;
//    public static int bookmark_counter = 0;
//    public static ArrayList<Bulletin> bulletinArrayList = new ArrayList<>();
//    public static ArrayList<BulletinDetail> bulletinDetail = new ArrayList<>();
//    public static ArrayList<EventRegistration> EventRegistrationArrayList = new ArrayList<>();
//    public ArrayList<EventBulletin> EventBulletinArrayList = new ArrayList<>();
//
//    public static ArrayList<Bulletin> bulletinsSelectionList = new ArrayList<>();
//    public static boolean clickStatus = true;
//    public static boolean selectAllStatus = false;
//
//    public BookmarkAdapter(ArrayList<Bulletin> bulletinArrayList,ArrayList<BulletinDetail> bulletinDetail ,ArrayList<EventRegistration> EventRegistrationArrayList,ArrayList<EventBulletin> EventBulletinArrayList,Context context) {
//        this.bulletinArrayList = bulletinArrayList;
//        this.bulletinDetail = bulletinDetail;
//        this.EventRegistrationArrayList = EventRegistrationArrayList;
//        this.EventBulletinArrayList = EventBulletinArrayList;
//
//        this.context=context;
//
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        ViewHolder viewHolder;
//
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_layout, parent,false);
//        viewHolder = new ViewHolder(itemView, context, bulletinArrayList,bulletinDetail ,EventRegistrationArrayList,EventBulletinArrayList,viewType);
//
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        Bulletin bulletin = bulletinArrayList.get(position);
//        BulletinDetail bulletinDetailList = bulletinDetail.get(position);
//
//        holder.textViewTitle.setText(bulletin.getBulletinTitle());
//        holder.textViewDetails.setText(bulletin.getDescription());
//        holder.textViewDate.setText(String.format("%1$tY %1$tb %1$td",bulletin.getPostDate()));
//        holder.textViewPostedBy.setText(bulletin.getPostedBy());
//        holder.imageViewRead.setVisibility(View.GONE);
//
//        if (bulletin.getDescription().length() >= 55) {
//            holder.textViewDetails.setText(bulletin.getDescription().substring(0, 55) + "...");
//        } else {
//            holder.textViewDetails.setText(bulletin.getDescription());
//        }
//        if(!MainActivity.is_in_action_mode){
//            holder.checkBoxDelete.setVisibility(View.GONE);
//
//        }else{
//            holder.checkBoxDelete.setVisibility(View.VISIBLE);
//            holder.checkBoxDelete.setChecked(false);
//
//        }
//        if (selectAllStatus){
//            holder.checkBoxDelete.setChecked(true);
//        }else
//            holder.checkBoxDelete.setChecked(false);
//        if (bulletinDetailList.getReadStatus() == 0){
////            holder.imageViewRead.setVisibility(View.VISIBLE);
//            holder.textViewTitle.setTextColor(Color.RED);
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//        return position;
//    }
//
//
//
//    @Override
//    public int getItemCount() {
//
//        return bulletinArrayList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
//        public final static int LAYOUT_ONE = 1;
//        public final static int LAYOUT_TWO = 2;
//        public TextView textViewTitle,textViewDetails,textViewDate,textViewPostedBy;
//        ArrayList<Bulletin> bulletins = new ArrayList<>();
//        ArrayList<BulletinDetail> bulletinDetails = new ArrayList<>();
//        ArrayList<EventRegistration> EventRegistrations = new ArrayList<>();
//        ArrayList<EventBulletin> EventBulletins = new ArrayList<>();
//
//        CheckBox checkBoxDelete;
//        public ImageView imageViewRead;
//
//        Context context;
//
//        public ViewHolder(View itemView,Context context,ArrayList<Bulletin> bulletins, ArrayList<BulletinDetail> bulletinDetails,ArrayList<EventRegistration> EventRegistrations,ArrayList<EventBulletin> EventBulletins, int layoutType){
//            super(itemView);
//            this.bulletins = bulletins;
//            this.bulletinDetails = bulletinDetails;
//            this.EventRegistrations = EventRegistrations;
//            this.EventBulletins = EventBulletins;
//
//            this.context = context;
//
//            itemView.setOnClickListener(this);
////            itemView.setOnLongClickListener(this);
//            imageViewRead = itemView.findViewById(R.id.imageViewRead);
//            textViewTitle = itemView.findViewById(R.id.textViewTitle);
//            textViewDetails = itemView.findViewById(R.id.textViewDetails);
//            checkBoxDelete = itemView.findViewById(R.id.checkBoxBookmark);
//            textViewDate = itemView.findViewById(R.id.textViewDate);
//            textViewPostedBy= itemView.findViewById(R.id.textViewPostedBy);
////            checkBoxBookmark.setOnClickListener(this);
////            MainActivity.checkBox.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//
//            int position = getAdapterPosition();
//
//            if (view == itemView) {
//                if(clickStatus == true) {
//                    Bulletin bulletin = bulletins.get(position);
//                    BulletinDetail bulletinDetail = bulletinDetails.get(position);
////                    EventRegistration eventRegistration = EventRegistrations.get(position);
////                    EventBulletin eventBulletin = EventBulletins.get(position);
//
//                    Intent intent = new Intent(this.context, BulletinDetails.class);
//                    intent.putExtra("bulletin", bulletin);
//                    intent.putExtra("bulletinDetail", bulletinDetail);
////                    intent.putExtra("eventRegistration", eventRegistration);
////                    intent.putExtra("eventBulletin", eventBulletin);
//                    this.context.startActivity(intent);
//
//                }
//            }else if(view == checkBoxDelete){
//                selectAllStatus=false;
//                prepareSelection(view,position);
//
//            }
//            else if (view == MainActivity.checkBox){
//
//                if (MainActivity.checkBox.isChecked()){
////                    bookmark_counter = 0;
////                    selectAllStatus = true;
////                    for (int i=0;i<BookmarkFragment.adapter.getItemCount();i++){
////                        BookmarkAdapter.bulletinsSelectionList.add(bulletins.get(i));
////                        bookmark_counter =  bookmark_counter + 1;
////                    }
//                }else {
////                    selectAllStatus = false;
////
////                    for (int i=0;i<BookmarkFragment.adapter.getItemCount();i++){
////                        BookmarkAdapter.bulletinsSelectionList.remove(bulletins.get(i));
////                        bookmark_counter =  bookmark_counter - 1;
////                    }
//                }
////                updateCounter(bookmark_counter);
////                BookmarkFragment.adapter.notifyDataSetChanged();
//            }
//        }
//
//        @Override
//        public boolean onLongClick(View view){
//            MainActivity.toolbar.getMenu().clear();
//            MainActivity.toolbar.inflateMenu(R.menu.menu_action_mode);
//            MainActivity.textViewCount.setVisibility(View.VISIBLE);
//            MainActivity.checkBox.setVisibility(View.VISIBLE);
//            MainActivity.imageButton.setVisibility(View.VISIBLE);
//            MainActivity.textViewTitle.setVisibility(View.GONE);
//            MainActivity.is_in_action_mode = true;
//            BookmarkFragment.adapter.notifyDataSetChanged();
//            clickStatus = false;
//
//            return true;
//        }
//        public void prepareSelection(View view,int position){
//
//            if (((checkBoxDelete)).isChecked()){
////                BookmarkAdapter.bulletinsSelectionList.add(bulletins.get(position));
////                bookmark_counter =  bookmark_counter + 1;
////                updateCounter(bookmark_counter);
//            }else{
////                BookmarkAdapter.bulletinsSelectionList.remove(bulletins.get(position));
////                bookmark_counter = bookmark_counter - 1;
////                updateCounter(bookmark_counter);
//
//            }
//        }
//        public void updateCounter(int counter){
//            if (counter == 0){
//                MainActivity.textViewCount.setText("0 item");
//            }else {
//                MainActivity.textViewCount.setText(bookmark_counter + " items");
//            }
//        }
//
//    }
//
//    public void setFilter(ArrayList<Bulletin> newList){
//        bulletinArrayList = new ArrayList<>();
//        bulletinArrayList.addAll(newList);
//        notifyDataSetChanged();
//
//    }
//}
