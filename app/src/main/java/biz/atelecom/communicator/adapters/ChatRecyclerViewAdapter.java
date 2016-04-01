package biz.atelecom.communicator.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import biz.atelecom.atelecom.ItemFragment.OnMessageListFragmentListener;
import biz.atelecom.communicator.R;
import biz.atelecom.communicator.models.Message;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private List<Message> mValues = null;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

/*
    private final OnMessageListFragmentListener mListener;

    public ChatRecyclerViewAdapter(List<DummyItem> items, OnMessageListFragmentListener listener) {
        mValues = items;
        mListener = listener;
    }
*/
    public ChatRecyclerViewAdapter(List<Message> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Message msg = mValues.get(position);
        holder.mItem = msg;
        holder.tvFrom.setText(msg.from);
        holder.tvTo.setText(msg.to);
        holder.tvMessage.setText(msg.body);
        holder.tvDate.setText(sdf.format(msg.issueDate));

        if(!msg.viewed) {
            holder.tvMessage.setTypeface(null, Typeface.BOLD);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMessageStatClick(holder.mItem);
                }
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public Message mItem;
        public final TextView tvFrom;
        public final TextView tvTo;
        public final TextView tvMessage;
        public final TextView tvDate;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvFrom = (TextView) view.findViewById(R.id.tvFrom);
            tvTo = (TextView) view.findViewById(R.id.tvTo);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            tvDate = (TextView) view.findViewById(R.id.tvDate);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvMessage.getText() + "'";
        }
    }
}
