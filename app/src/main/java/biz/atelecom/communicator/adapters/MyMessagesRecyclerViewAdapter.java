package biz.atelecom.communicator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import biz.atelecom.atelecom.ItemFragment.OnListFragmentInteractionListener;
import biz.atelecom.communicator.R;
import biz.atelecom.communicator.dummy.DummyContent.DummyItem;
import biz.atelecom.communicator.models.Message;

import java.util.List;

public class MyMessagesRecyclerViewAdapter extends RecyclerView.Adapter<MyMessagesRecyclerViewAdapter.ViewHolder> {

    private List<Message> mValues = null;
/*
    private final OnListFragmentInteractionListener mListener;

    public MyMessagesRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }
*/
    public MyMessagesRecyclerViewAdapter(List<Message> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvFrom.setText(mValues.get(position).from);
        holder.tvTo.setText(mValues.get(position).to);
        holder.tvMessage.setText(mValues.get(position).body);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
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
        /*
        public final TextView mIdView;
        public final TextView mContentView;
        */
        public Message mItem;
        public final TextView tvFrom;
        public final TextView tvTo;
        public final TextView tvMessage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            //mContentView = (TextView) view.findViewById(R.id.content);

            tvFrom = (TextView) view.findViewById(R.id.tvFrom);
            tvTo = (TextView) view.findViewById(R.id.tvTo);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvMessage.getText() + "'";
        }
    }
}
