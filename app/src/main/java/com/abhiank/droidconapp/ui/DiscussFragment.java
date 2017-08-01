package com.abhiank.droidconapp.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abhiank.droidconapp.R;
import com.abhiank.droidconapp.data.DiscussItem;
import com.abhiank.droidconapp.util.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhimanyuagrawal on 17/12/15.
 */
public class DiscussFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private ArrayList<DiscussItem> mAnnouncementDataList = new ArrayList<>();

    private static DiscussFragment discussFragment = null;

    public static DiscussFragment newInstance() {
        if(discussFragment==null)
            discussFragment = new DiscussFragment();
        return discussFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_looking_for, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.looking_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getLookingForFromParse();

        return rootView;
    }

    public void getLookingForFromParse(){

        if(Utils.isNetworkAvailable(getActivity())) {

            Utils.showProgressDialogue(getActivity(), "");

            ParseQuery<DiscussItem> placeParseQuery = ParseQuery.getQuery(DiscussItem.class);
            placeParseQuery.orderByDescending("createdAt");
            placeParseQuery.findInBackground(new FindCallback<DiscussItem>() {
                @Override
                public void done(List<DiscussItem> list, ParseException e) {
                    if (e == null) {
                        mAnnouncementDataList.clear();
                        for (DiscussItem announceItem : list)
                            mAnnouncementDataList.add(announceItem); //// TODO: 17/12/15

                        adapter = new MyRecyclerAdapter(getActivity(), mAnnouncementDataList);
                        mRecyclerView.setAdapter(adapter);

                        Utils.dismissProgressDialogue();
                    } else {
                        Utils.dismissProgressDialogue();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            Toast.makeText(getActivity(), "No Internet Available",Toast.LENGTH_LONG).show();
        }
    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private List<DiscussItem> feedItemList;
        private Context mContext;

        public MyRecyclerAdapter(Context context, List<DiscussItem> feedItemList) {
            this.feedItemList = feedItemList;
            this.mContext = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.announce_list_row, null);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DiscussItem feedItem = feedItemList.get(i);
                    startActivity(new Intent(getActivity(), DiscussActivity.class).putExtra("id",feedItem.getObjectId()));
                }
            });

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
            DiscussItem feedItem = feedItemList.get(i);

            customViewHolder.titleTextView.setText(feedItem.getTopic());
            customViewHolder.timeTextView.setText(feedItem.getTime());
            customViewHolder.postByTextView.setText(feedItem.getPostBy());
        }

        @Override
        public int getItemCount() {
            return (null != feedItemList ? feedItemList.size() : 0);
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView titleTextView;
        protected TextView timeTextView;
        protected TextView postByTextView;

        public CustomViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.title_text_view);
            this.postByTextView = (TextView) view.findViewById(R.id.contact_text_view);
            this.timeTextView = (TextView) view.findViewById(R.id.time_text_view);
        }
    }

}
