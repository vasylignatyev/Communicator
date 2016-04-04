package biz.atelecom.communicator;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import biz.atelecom.communicator.ajax.HTTPManager;
import biz.atelecom.communicator.ajax.RequestPackage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TMP_PHONE_NUMBER = "tmpPhoneNumber";

    private String mParam1;
    private String mParam2;

    EditText mTePhoneNumber;
    EditText mTePassword;
    CheckBox mCbShowPassword;

    String mTmpPhoneNumber = null;

    private String mNumber;
    private String mPassword;
    private String mToken;



    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private ProgressDialog pg;

    private OnLoginInteractionListener mListener;

    final private OnLoginInteractionListener getListener() {
        return mListener;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

        mFragmentActivity = (FragmentActivity)context;
        pg = new ProgressDialog(context);
        pg.setMessage("Wait please ...");
        pg.setTitle("Connecting to server");

        if (context instanceof OnLoginInteractionListener) {
            mListener = (OnLoginInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d("MyApp", "onCreate");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if(savedInstanceState != null) {
            mTmpPhoneNumber = savedInstanceState.getString(TMP_PHONE_NUMBER);
            Log.d("MyApp", "mTmpPhoneNumber: " + mTmpPhoneNumber);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;

        Log.d("MyApp", "onCreateView");
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false);

        // Find all views
        mTePhoneNumber = (EditText) v.findViewById(R.id.tePhoneNumber);
        mTePassword = (EditText) v.findViewById(R.id.tePassword);
        mCbShowPassword = (CheckBox) v.findViewById(R.id.cbShowPassword);
        Button btLogin = (Button) v.findViewById(R.id.btLogin);

        if(mTmpPhoneNumber != null) {
            Log.d("MyApp", "mTmpPhoneNumber: " + mTmpPhoneNumber);
            mTePhoneNumber.setText(mTmpPhoneNumber);
        }

        mTePassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("MyApp", "mTePassword.setOnKeyListener " + keyCode);
                if (keyCode == 66) {
                    hideKeyboard(v);
                    //onLoginClick(mTePhoneNumber, mTePassword);
                    return true; //this is required to stop sending key event to parent
                }
                return false;
            }
        });
        mCbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mTePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    mTePassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick(mTePhoneNumber, mTePassword);
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Log.d("MyApp", "onSaveInstanceState");

        mTmpPhoneNumber = mTePhoneNumber.getText().toString();
        Log.d("MyApp", "mTmpPhoneNumber: " + mTmpPhoneNumber);
        savedInstanceState.putString(TMP_PHONE_NUMBER, mTmpPhoneNumber);
        savedInstanceState.putString("mTePassword", mTePassword.getText().toString());
        savedInstanceState.putBoolean("mCbShowPassword", mCbShowPassword.isChecked());
    }

    private void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null)
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void onLoginClick(EditText tePhoneNumber, EditText tePassword) {
        String phoneNumber = tePhoneNumber.getText().toString();
        String password = tePassword.getText().toString();

        if( phoneNumber.length() < 6 ) {
            Toast.makeText(mContext, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(mContext, "Enter Password", Toast.LENGTH_SHORT).show();
        } else {
            mNumber = "380894" + phoneNumber;
            login( mNumber, password);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
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
    public interface OnLoginInteractionListener {
        // TODO: Update argument type and name
        void loginSuccess(String number);
    }
    private void login(String phoneNumber, String password) {
        RequestPackage rp = new RequestPackage( MainActivity.AJAX );
        rp.setMethod("GET");
        rp.setParam("functionName", "login");
        rp.setParam("number", phoneNumber);
        rp.setParam("password", password);

        pg.show();

        LoginAsyncTask task = new LoginAsyncTask();
        task.execute(rp);
    }
    private class LoginAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getHashString:" + s);
            if( s != null ) {
                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.has("result")) {
                        boolean result = obj.getBoolean("result");
                        if (result) {
                            mListener.loginSuccess(mNumber);
                        } else {
                            Toast.makeText(mContext, "Wrong number or password!!!", Toast.LENGTH_LONG).show();
                            mNumber = null;
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            pg.hide();
        }
    }
}
