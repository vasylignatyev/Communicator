package biz.atelecom.communicator.adapters;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

//import biz.atelecom.atelecom.ItemFragment.OnMessageListFragmentListener;
import biz.atelecom.communicator.R;
import biz.atelecom.communicator.models.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private final List<Message> mValues;
    private final String mNumber;

    public ChatRecyclerViewAdapter( String number, List<Message> items) {
        mValues = items;
        mNumber = number;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    private String getDateStr(Date date) {
        SimpleDateFormat sdf;
        Calendar calendar = Calendar.getInstance();
        if ((calendar.getTime().getTime() - date.getTime()) > 86400000) {
            sdf = new SimpleDateFormat("dd MMM");
        } else {
            sdf = new SimpleDateFormat("HH:mm");
        }
        return sdf.format(date);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Message msg = mValues.get(position);
        holder.mItem = msg;

        TextView tv;
        LinearLayout ll;
        TextView tvMessageDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy E");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(msg.issueDate);

        Calendar prevDate = Calendar.getInstance();
        if(position != 0 ) {
            prevDate.setTime(mValues.get(position-1).issueDate);
            prevDate.set(Calendar.SECOND, 0);
            prevDate.set(Calendar.MINUTE, 0);
            prevDate.set(Calendar.HOUR_OF_DAY, 0);
            prevDate.add(Calendar.DATE, 1);
        }

        if(position == 0 || calendar.after(prevDate)){
            holder.ll_separator.setVisibility(View.VISIBLE);
            holder.tvDate.setText(sdf.format(msg.issueDate));
         }

        sdf = new SimpleDateFormat("HH:mm");
        if(msg.to.equals(mNumber)) {
            holder.tvMessageIn.setText(msg.body);
            holder.tvMessageIn.setTypeface(null, Typeface.BOLD);
            holder.tv_dateIn.setText(sdf.format(msg.issueDate));
            holder.ll_incoming.setVisibility(View.VISIBLE);
            holder.ll_outgoing.setVisibility(View.GONE);
        } else {
           holder.tvMessageOut.setText(msg.body);
           holder.tvMessageOut.setTypeface(null, Typeface.BOLD);
           holder.tv_dateOut.setText(sdf.format(msg.issueDate));
           holder.ll_outgoing.setVisibility(View.VISIBLE);
           holder.ll_incoming.setVisibility(View.GONE);
        }
     }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public Message mItem;
        public LinearLayout ll_separator;
        public final TextView tvMessageIn;
        public final TextView tvMessageOut;
        public final TextView tvDate;
        public final TextView tv_dateIn;
        public final TextView tv_dateOut;
        public final LinearLayout ll_incoming;
        public final LinearLayout ll_outgoing;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ll_separator = (LinearLayout)view.findViewById(R.id.ll_separator);
            tvMessageIn = (TextView) view.findViewById(R.id.tvMessageIn);
            tvMessageOut = (TextView) view.findViewById(R.id.tvMessageOut);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tv_dateIn = (TextView) view.findViewById(R.id.tv_dateIn);
            tv_dateOut = (TextView) view.findViewById(R.id.tv_dateOut);
            ll_incoming = (LinearLayout) view.findViewById(R.id.ll_incoming);
            ll_outgoing = (LinearLayout) view.findViewById(R.id.ll_outgoing);
         }

        @Override
        public String toString() {
            return super.toString() + " '" + tvMessageIn.getText() + "'";
        }
    }
}
