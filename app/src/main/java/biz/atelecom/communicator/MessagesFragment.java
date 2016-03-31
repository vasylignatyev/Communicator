package biz.atelecom.communicator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import biz.atelecom.communicator.adapters.ChatRecyclerViewAdapter;
import biz.atelecom.communicator.adapters.MessageItemRecyclerViewAdapter;
import biz.atelecom.communicator.ajax.HTTPManager;
import biz.atelecom.communicator.ajax.RequestPackage;
import biz.atelecom.communicator.dummy.DummyContent.DummyItem;
import biz.atelecom.communicator.models.Message;
import biz.atelecom.communicator.models.MessageStat;

public class MessagesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private  ArrayList<MessageStat> mMessageStatList = null;

    private RecyclerView mRecyclerView;

    public MessagesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MessagesFragment newInstance(int columnCount) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        getMessageStat();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            /*
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
                    */
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MessageStat item);
    }

    /**
     *  ASYNC TASKS
     */
    private void getMessageStat() {
        RequestPackage rp = new RequestPackage( MainActivity.AJAX );
        rp.setMethod("GET");
        rp.setParam("functionName", "get_messages_stat");
        rp.setParam("number", MainActivity.getNumber());
        //pg.show();

        GetMessageStatAsyncTask task = new GetMessageStatAsyncTask();
        task.execute(rp);
    }

    private class GetMessageStatAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "GetMessageStatAsyncTask:" + s);

            JSONArray jArray;
            mMessageStatList = new ArrayList<>();

            try {
                jArray = new JSONArray(s);
                for( int i = 0 ; i < jArray.length(); i++){
                    MessageStat messageStat = new MessageStat();

                    JSONObject jObj = jArray.getJSONObject(i);

                    if(jObj.has("ID")) {
                        messageStat.id =jObj.getString("ID");
                    }
                    if(jObj.has("BODY")) {
                        messageStat.body =jObj.getString("BODY");
                    }
                    if(jObj.has("UNVIEWED")) {
                        messageStat.unviewed =jObj.getInt("UNVIEWED");
                    }
                    mMessageStatList.add(messageStat);
                }
                mRecyclerView.setAdapter(new MessageItemRecyclerViewAdapter(mMessageStatList));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
