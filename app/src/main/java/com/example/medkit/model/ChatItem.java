package com.example.medkit.model;

public class ChatItem {
    private String name;
    private String lastMessage;
    private String time;
    private Integer imageUrl;

    public ChatItem(String name, String lastmessage, String time, Integer imageurl) {
        this.name = name;
        this.lastMessage = lastmessage;
        this.time = time;
        this.imageUrl = imageurl;
    }

    public ChatItem() {

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastmessage() {
        return lastMessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastMessage = lastmessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getImageurl() {
        return imageUrl;
    }

    public void setImageurl(Integer imageurl) {
        this.imageUrl = imageurl;
    }
    //    @Override
//    public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
//        TextView name=viewHolder.itemView.findViewById(R.id.chat_name_tv);
//        name.setText(User.USERTYPE);
//        TextView time=viewHolder.itemView.findViewById(R.id.time_tv);
//        name.setText(User.AGE);
//        TextView lastMessage=viewHolder.itemView.findViewById(R.id.last_message_tv);
//        name.setText(User.USERTYPE);
//        CircleImageView chatImage=viewHolder.itemView.findViewById(R.id.chat_image);
//        chatImage.setImageResource(R.drawable.icon_awesome_user_alt);
//
//    }
//
//    @Override
//    public int getLayout() {
//        return R.layout.chat_item;
//    }
}
