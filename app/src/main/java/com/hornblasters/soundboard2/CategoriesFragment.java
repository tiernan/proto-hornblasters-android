package com.hornblasters.soundboard2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hornblasters.xml.Category;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ArrayList<Category> categories = null;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategoriesFragment.
     */
    public static CategoriesFragment newInstance(ArrayList<Category> categories) {
        CategoriesFragment fragment = new CategoriesFragment();
        fragment.categories = categories;
        fragment.setRetainInstance(true);
        return fragment;
    }

    public CategoriesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ListView listView = (ListView) inflater.inflate(R.layout.fragment_categories, container, false);

        CategoriesAdapter adapter = new CategoriesAdapter(getActivity(), categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                onButtonPressed((int) v.findViewById(R.id.category_name).getTag());
            }
        });

        return listView;
    }

    @Override
    public void onResume() {
        super.onResume();
        (getActivity()).setTitle(getString(R.string.title_activity_store));
    }

    public void onButtonPressed(int id) {
        if (mListener != null) {
            mListener.onFragmentInteraction(OnFragmentInteractionListener.Action.CATEGORY, id);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
