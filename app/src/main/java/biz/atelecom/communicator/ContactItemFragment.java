package biz.atelecom.communicator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import biz.atelecom.communicator.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactItemFragment newInstance(int columnCount) {
        ContactItemFragment fragment = new ContactItemFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactitem_list, container, false);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        Button bt_select = (Button) view.findViewById(R.id.bt_select);

        bt_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneStr = et_phone.getText().toString();
                phoneStr = (phoneStr.length() == 0) ? null : ("380894" + phoneStr);
                Fragment fr = ChatFragment.newInstance(MainActivity.getNumber(), phoneStr);

                //FragmentManager fm =getActivity().getSupportFragmentManager();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fr).commit();
            }
        });

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new ContactItemRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnMessageListFragmentListener) {
            mListener = (OnMessageListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMessageListFragmentListener");
        }
        */
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if(toolbar != null) {
            toolbar.setTitle("Contacts");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
