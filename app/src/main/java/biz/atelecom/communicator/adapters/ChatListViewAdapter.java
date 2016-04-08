package biz.atelecom.communicator.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import biz.atelecom.communicator.R;
import biz.atelecom.communicator.models.Message;

public class ChatListViewAdapter extends ArrayAdapter<Message> {

    private final List<Message> mMessageList;
    private final Context mContext;
    private final String mNumber;

    public ChatListViewAdapter(Context context, int resource, List<Message> objects, String number) {
        super(context, resource, objects);

        mContext = context;
        mMessageList = objects;
        mNumber = number;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        Log.d("MyApp", "Position: " + position);

        if(view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.chat_item, parent, false);
        }
        LinearLayout ll_separator = (LinearLayout)view.findViewById(R.id.ll_separator);
        TextView tvMessageIn = (TextView) view.findViewById(R.id.tvMessageIn);
        TextView tvMessageOut = (TextView) view.findViewById(R.id.tvMessageOut);
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tv_dateIn = (TextView) view.findViewById(R.id.tv_dateIn);
        TextView tv_dateOut = (TextView) view.findViewById(R.id.tv_dateOut);
        LinearLayout ll_incoming = (LinearLayout) view.findViewById(R.id.ll_incoming);
        LinearLayout ll_outgoing = (LinearLayout) view.findViewById(R.id.ll_outgoing);

        Message msg = mMessageList.get(position);

        TextView tv;
        LinearLayout ll;
        TextView tvMessageDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm E");

        Calendar currDate = Calendar.getInstance();
        currDate.setTime(msg.issueDate);

        Calendar prevDate = Calendar.getInstance();
        if(position != 0 ) {
            prevDate.setTime(mMessageList.get(position - 1).issueDate);
            prevDate.set(Calendar.SECOND, 0);
            prevDate.set(Calendar.MINUTE, 0);
            prevDate.set(Calendar.HOUR_OF_DAY, 0);
            prevDate.add(Calendar.DATE, 1);
        }

        //if(position == 0 || currDate.after(prevDate)){
            ll_separator.setVisibility(View.VISIBLE);
            //tvDate.setText(sdf.format(msg.issueDate));
            tvDate.setText(sdf.format(prevDate.getTime()));
        //}

        if(msg.to.equals(mNumber)) {
            tv= tvMessageIn;
            ll = ll_incoming;
            tvMessageDate = tv_dateIn;

        } else {
            tv= tvMessageOut;
            ll = ll_outgoing;
            tvMessageDate = tv_dateOut;
        }

        tv.setText(msg.body);
        tv.setTypeface(null, Typeface.BOLD);

        ll.setVisibility(View.VISIBLE);
        sdf = new SimpleDateFormat("HH:mm");
        tvMessageDate.setText(sdf.format(msg.issueDate));

        return view;
    }
}
