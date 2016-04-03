package biz.atelecom.communicator.adapters;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private List<Message> mValues = null;
    private final String mNumber;
    private Resources mResources;

    public ChatRecyclerViewAdapter(Resources resources, String number, List<Message> items) {
        mValues = items;
        mNumber = number;
        mResources = resources;
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

        TextView tv  = (msg.to.equals(mNumber)) ? holder.tvMessageIn : holder.tvMessageOut;
        tv.setText(msg.body);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setVisibility(View.VISIBLE);

        holder.tvDate.setText(getDateStr(msg.issueDate));
        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMessageStatClick(holder.mItem);
                }
            }

        });
        */
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public Message mItem;
        public final TextView tvMessageIn;
        public final TextView tvMessageOut;
        public final TextView tvDate;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvMessageIn = (TextView) view.findViewById(R.id.tvMessageIn);
            tvMessageOut = (TextView) view.findViewById(R.id.tvMessageOut);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
         }

        @Override
        public String toString() {
            return super.toString() + " '" + tvMessageIn.getText() + "'";
        }
    }
}
