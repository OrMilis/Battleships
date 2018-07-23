package com.example.ormil.battleships;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.ormil.battleships.Logic.LeaderboardRepository;
import com.example.ormil.battleships.Logic.eDifficulty;

/**
 * Created by ormil on 13/01/2018.
 */

public class LeaderboardListFragment extends Fragment {

    LeaderboardAdapter mAdapter;
    OnLeaderboardActionListener mListener;

    private Button easyButton;
    private Button normalButton;
    private Button hardButton;

    public LeaderboardListFragment(){}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnLeaderboardActionListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLeaderboardActionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        easyButton = view.findViewById(R.id.easyButton);
        normalButton = view.findViewById(R.id.normalButton);
        hardButton = view.findViewById(R.id.hardButton);

        mAdapter = new LeaderboardAdapter(getContext(), LeaderboardRepository.getInstance().getmTopUsersEasy());

        final ListView listView = view.findViewById(R.id.leaderboard_list);

        listView.setAdapter(mAdapter);
        listView.setDivider(null);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mListener.moveToMarker(listView.indexOfChild(view));
                mAdapter.setActiveView(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        easyButton.setOnClickListener(setOnClickListenerByDifficulty(eDifficulty.EASY));
        normalButton.setOnClickListener(setOnClickListenerByDifficulty(eDifficulty.NORMAL));
        hardButton.setOnClickListener(setOnClickListenerByDifficulty(eDifficulty.HARD));

        normalButton.performClick();

        mListener.setListViewReference(listView);

        return view;
    }

    public View.OnClickListener setOnClickListenerByDifficulty(final eDifficulty difficulty){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.setUserList(LeaderboardRepository.getInstance().getTopUsersByDifficulty(difficulty));
                mAdapter.notifyDataSetChanged();
                mListener.updateMarkersData(difficulty);
            }
        };
        return listener;
    }

    public interface OnLeaderboardActionListener {
        void updateMarkersData(eDifficulty difficulty);

        void moveToMarker(int position);

        void setListViewReference(ListView listViewReference);
    }

}
