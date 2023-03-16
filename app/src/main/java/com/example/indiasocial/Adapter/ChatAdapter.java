package com.example.indiasocial.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiasocial.Moduls.MessageModule;
import com.example.indiasocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModule> messageModules;
    Context context;
    String recId;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModule> messageModules, Context context) {
        this.messageModules = messageModules;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModule> messageModules, Context context, String recId) {
        this.messageModules = messageModules;
        this.context = context;
        this.recId = recId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.semple_sender,parent,false);
            return new senderViewHolder(view);
        }else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.semple_receiver,parent,false);
            return new recyclerViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModules.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
        {
            return SENDER_VIEW_TYPE;
        }
        else
        {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModule messageModule = messageModules.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure, you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(messageModule.getMessageId())
                                        .setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

                return false;
            }
        });

        if(holder.getClass() == senderViewHolder.class)
        {
            ((senderViewHolder) holder).senderMsg.setText(messageModule.getMessage());

            Date date = new Date(messageModule.getTimestamp());
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
            String strDate = dateFormat.format(date);
            ((senderViewHolder) holder).senderTime.setText(strDate.toString());
        }
        else
        {
            ((recyclerViewHolder) holder).receiverMag.setText(messageModule.getMessage());

            Date date = new Date(messageModule.getTimestamp());
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
            String strDate = dateFormat.format(date);
            ((recyclerViewHolder) holder).receiverTime.setText(strDate.toString());
        }
    }

    @Override
    public int getItemCount() {
        return messageModules.size();
    }

    public class recyclerViewHolder extends RecyclerView.ViewHolder{

        TextView receiverMag, receiverTime;

        public recyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMag = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }

    public class senderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg, senderTime;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }
}
