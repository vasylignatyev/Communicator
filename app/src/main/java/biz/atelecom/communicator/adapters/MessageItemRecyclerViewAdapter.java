package biz.atelecom.communicator.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import biz.atelecom.communicator.MessagesFragment;
import biz.atelecom.communicator.R;
import biz.atelecom.communicator.models.MessageStat;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageItemRecyclerViewAdapter extends RecyclerView.Adapter<MessageItemRecyclerViewAdapter.ViewHolder> {

    private List<MessageStat> mValues = null;
    private final MessagesFragment.OnMessageListFragmentListener mListener;

    public MessageItemRecyclerViewAdapter(List<MessageStat> items, MessagesFragment.OnMessageListFragmentListener listener) {
        mValues = items;
        mListener = listener;
     }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM");

        holder.mItem = mValues.get(position);
        holder.tvContactId.setText(mValues.get(position).id);
        holder.tvMessage.setText(mValues.get(position).body);
        holder.tvUnviewed.setText(Integer.toString(mValues.get(position).unviewed));
        holder.tvMDate.setText(sdf.format(mValues.get(position).issueDate));
        if(mValues.get(position).online == 1)
            holder.tvOnline.setVisibility(View.VISIBLE);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onMessageStatClick(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivContact;
        public final TextView tvContactId;
        public final TextView tvMessage;
        public final TextView tvUnviewed;
        public final TextView tvMDate;
        public final TextView tvOnline;

        public MessageStat mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivContact = (ImageView) view.findViewById(R.id.ivContact);
            tvContactId = (TextView) view.findViewById(R.id.tvContactId);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            tvUnviewed = (TextView) view.findViewById(R.id.tvUnviewed);
            tvMDate = (TextView) view.findViewById(R.id.tvMDate);
            tvOnline = (TextView) view.findViewById(R.id.tvOnline);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + tvMessage.getText() + "'";
        }
    }
}
