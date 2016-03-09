package amal.com.maddiscovery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import amal.com.maddiscovery.model.MadEvent;
import amal.com.maddiscovery.db.DatabaseHandler;
import amal.com.maddiscovery.adapter.EventListAdapter;
import amal.com.maddiscovery.utill.EventUtil;

public class MainActivity extends AppCompatActivity {

    private EventUtil eventUtil;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<MadEvent> madEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventUtil = new EventUtil();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        //Item Click
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                MadEvent madEvent = madEventList.get(position);
                EventUtil.selectedEvent = madEvent;
                startActivity(new Intent(getApplicationContext(), EventInfoActivity.class));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //Fab Action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EventAddActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showAllEvents();
    }

    private void showAllEvents() {
        List<MadEvent> dataList = new ArrayList<>();
        DatabaseHandler db = new DatabaseHandler(this);

        madEventList = db.getAllMadEvents();
        int dataSize = madEventList.size();

        if (dataSize == 0) {
            eventUtil.showToast(getApplicationContext(), "No Data");
        } else {

            for (MadEvent mdv : madEventList) {
                dataList.add(mdv);
            }

            mAdapter = new EventListAdapter(getApplicationContext(), dataList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }


    //item Click on
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    //Item Click end

}
