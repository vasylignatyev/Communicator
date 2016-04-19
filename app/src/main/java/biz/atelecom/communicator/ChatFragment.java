package biz.atelecom.communicator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import biz.atelecom.communicator.adapters.ChatListViewAdapter;
import biz.atelecom.communicator.adapters.ChatRecyclerViewAdapter;
import biz.atelecom.communicator.ajax.HTTPManager;
import biz.atelecom.communicator.ajax.RequestPackage;
import biz.atelecom.communicator.dummy.DummyContent.DummyItem;
import biz.atelecom.communicator.models.Message;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatFragment extends Fragment {

    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_I_MESSAGE = "i_message";
    public static final String ARG_NUMBER_B = "numberB";
    public static final String ARG_NUMBER_A = "numberA";
    public static final String ARG_BODY = "body";

    private static final String MESSAGE_VALUE = "message_value";
    private String mSavedMessage = null;

    private int mColumnCount = 1;

    private String mNumberA;
    private String mNumberB;

    private EditText mEtMessage;

    private ProgressDialog pg;

    ListView mList;

    private final ArrayList<Message> mMessageList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ChatListViewAdapter mChatListViewAdapter;

    private OnListFragmentInteractionListener mListener;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatFragment newInstance(String numberA , String numberB) {
        Log.d("MyApp", "ChatFragment.newInstance");
        Log.d("MyApp", "ChatActivity PHONE_A: " + numberA);
        Log.d("MyApp", "ChatActivity PHONE_B: " + numberB);

        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NUMBER_A, numberA);
        args.putString(ARG_NUMBER_B, numberB);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MyApp", "ChatFragment: onCreate");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT, 1);
            mNumberA = getArguments().getString(ARG_NUMBER_A, null);
            mNumberB = getArguments().getString(ARG_NUMBER_B, null);
        }

        Log.d("MyApp", "mNumberA: " + mNumberA);
        if(savedInstanceState != null) {
            Log.d("MyApp", "onCreate mSavedMessage: " + mSavedMessage);
            mSavedMessage = savedInstanceState.getString(MESSAGE_VALUE, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new ChatRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        //mList = (ListView) view.findViewById(R.id.list);

        mEtMessage = (EditText) view.findViewById(R.id.etMessage);
        if(mSavedMessage != null) {
            Log.d("MyApp", "onCreateView mSavedMessage: " + mSavedMessage);
            mEtMessage.setText(mSavedMessage);
        }

        final Button btSend = (Button) view.findViewById(R.id.btSend);
        final Button btRefresh = (Button) view.findViewById(R.id.btRefresh);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEtMessage.getText().toString();
                if (message.length() > 0) {
                    sendMessage(message);
                }
            }
        });

        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessageList();
            }
        });

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        if(toolbar != null) {
            toolbar.setTitle("Chat to: " + ((mNumberB == null) ? "(Empty)" : mNumberB) );
        }

        if(mNumberB != null) {
            getMessageList();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        pg = new ProgressDialog(context);
        pg.setMessage("Wait please ...");
        pg.setTitle("Connecting to server");
       /*
        if (context instanceof OnMessageListFragmentListener) {
            mListener = (OnMessageListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMessageListFragmentListener");
        }
        */
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyBus.getInstance().register(this);
    }

    /**
     * Receive message from push services
     * @param message
     */
    @Subscribe
    public void onReceivePush( Message message) {
        Log.d("MyApp", "mNumberA:" + mNumberA + " mNumberB:" + mNumberB);
        Log.d("MyApp", "from:" + message.from + " to:" + message.to);
        if(mNumberB.equals(message.from)) {
            getMessageList();
        }
    }

    @Override
    public void onPause() {
        MyBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        String etMessage = mEtMessage.getText().toString();
        outState.putString(MESSAGE_VALUE, etMessage);
        Log.d("MyApp", "onSaveInstanceState etMessage: " + etMessage);
        super.onSaveInstanceState(outState);
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
        void onListFragmentInteraction(DummyItem item);
    }

    /**
     *  ASYNC TASKS
     */
    public void getMessageList() {
        RequestPackage rp = new RequestPackage( MainActivity.AJAX );
        rp.setMethod("GET");
        rp.setParam("functionName", "get_message_list");
        rp.setParam("numberA", mNumberA);
        rp.setParam("numberB", mNumberB);
        pg.show();

        GetMessageListAsyncTask task = new GetMessageListAsyncTask();
        task.execute(rp);
    }

    private class GetMessageListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "GetMessageListAsyncTask:" + s);

            Message message;
            JSONObject jObj;
            try {
                JSONArray jArray = new JSONArray(s);
                mMessageList.clear();
                for (int i = 0; i < jArray.length(); i++) {
                    jObj = jArray.getJSONObject(i);
                     mMessageList.add(new Message(jObj));
                }
                mRecyclerView.setAdapter(new ChatRecyclerViewAdapter( mNumberB, mMessageList));
                mRecyclerView.scrollToPosition(mMessageList.size() - 1);
                //mList.setAdapter(new ChatListViewAdapter(getActivity(), R.layout.chat_item, mMessageList, mNumberB));
                //mList.smoothScrollToPosition(mMessageList.size() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                Log.d("MyApp", "Prser error" + e.getMessage());
                e.printStackTrace();
            } finally {
                pg.hide();
            }
        }
    }
    private void sendMessage(String body) {
        RequestPackage rp = new RequestPackage( MainActivity.AJAX );
        if(mNumberB == null ) {
            return;
        }
        rp.setMethod("GET");
        rp.setParam("functionName", "send_message");
        rp.setParam("numberA", mNumberA);
        rp.setParam("numberB", mNumberB);
        rp.setParam("body", body);
        pg.show();

        SendMessageAsyncTask task = new SendMessageAsyncTask();
        task.execute(rp);
    }

    private class SendMessageAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "GetMessageListAsyncTask:" + s);
            mEtMessage.setText("");
            pg.hide();
            getMessageList();
        }
    }
}

