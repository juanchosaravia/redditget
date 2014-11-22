package com.droidcba.redditget;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.droidcba.redditget.adapter.CustomArrayAdapter;
import com.droidcba.redditget.rest.api.RedditApi;
import com.droidcba.redditget.rest.pojo.Child;
import com.droidcba.redditget.rest.pojo.Listing;
import com.droidcba.redditget.utils.Utilities;

import java.util.List;

import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the Callbacks.
 * interface.
 */
public class ItemFragment extends ListFragment {

    private static final String TAG = "ItemFragment";
    private static String mAfterPage;
    private static List<Child> sListItems;
    private CustomArrayAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private RedditApi mRedditApi;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    public static ItemFragment newInstance() {
        return new ItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (sListItems == null) {
            Log.d(TAG, "Subscribing..");
            loadData(null);
        } else {
            mAdapter = new CustomArrayAdapter(getActivity(), android.R.id.list,
                    sListItems);
            setListAdapter(mAdapter);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnLoadMore = new Button(getActivity());
        btnLoadMore.setBackgroundResource(0);
        btnLoadMore.setText("Load more...");
        getListView().addFooterView(btnLoadMore);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mAfterPage)) {
                    loadData(mAfterPage);
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.item_no_more_items_to_load),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void loadData(String page) {
        Log.d(TAG, "Invoked loadData with page: " + page);

        if (mRedditApi == null) {
            mRedditApi = mListener.getRestAdapter().create(RedditApi.class);
        }

        AndroidObservable.bindFragment(this, mRedditApi.getTop(page))
                .subscribeOn(Schedulers.computation())
                .subscribe(new Action1<Listing>() {
                    @Override
                    public void call(Listing listing) {
                        Log.d(TAG, "Items arrived!");
                        mAfterPage = listing.getData().getAfter();
                        int lastPosition = 0;
                        if (sListItems != null) {
                            // Add more items...
                            lastPosition = getListView().getFirstVisiblePosition();
                            sListItems.addAll(listing.getData().getChildren());
                        } else {
                            sListItems = listing.getData().getChildren();
                        }

                        mAdapter = new CustomArrayAdapter(getActivity(), android.R.id.list,
                                sListItems);
                        setListAdapter(mAdapter);
                        getListView().setSelection(lastPosition);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Log.d(TAG, "There was an error: " + e.getMessage());
                    }
                });
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


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Child item = (Child) this.getListAdapter().getItem(position);
        String picUrl = item.getData().getThumbnail();

        if (Utilities.isValidUrl(picUrl)) {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(picUrl));

            startActivity(browserIntent);
        } else {
            Log.d(TAG, "Url malfomed: " + picUrl);
            Toast.makeText(
                    getActivity(),
                    getResources().getString(R.string.item_missed_thumbnail),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
