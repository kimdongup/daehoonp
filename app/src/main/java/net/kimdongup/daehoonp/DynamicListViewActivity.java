package net.kimdongup.daehoonp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import java.util.ArrayList;

public class DynamicListViewActivity extends Activity implements OnScrollListener

{
    private static final String LOG = "DynamicListViewActivity";
    private CustomAdapter mAdapter;
    private ListView mListView;
    private LayoutInflater mInflater;
    private ArrayList<String> mRowList;
    private boolean mLockListView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamiclistviewactivity);


        // 멤버 변수 초기화
        mRowList = new ArrayList<String>();
        mLockListView = true;

        // 어댑터와 리스트뷰 초기화
        mAdapter = new CustomAdapter(this, R.layout.row, mRowList);
        mListView = (ListView) findViewById(R.id.listView1);

        // 푸터를 등록합니다. setAdapter 이전에 해야 합니다.
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListView.addFooterView(mInflater.inflate(R.layout.footer, null));

        // 스크롤 리스너를 등록합니다. onScroll에 추가구현을 해줍니다.
        mListView.setOnScrollListener(this);
        mListView.setAdapter(mAdapter);

        // 데미데이터를 추가하기 위해 임의로 만든 메서드 호출
        addItems(50);

    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
// 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고
// 가정합니다.
        int count = totalItemCount - visibleItemCount;
        if(firstVisibleItem >= count && totalItemCount != 0 && mLockListView == false)
        {
            Log.i(LOG, "Loading next items");
            addItems(50);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
    }



    /**
     * 임의의 방법으로 더미 아이템을 추가합니다.
     *
     * @param size
     */

    private void addItems(final int size)
    {
// 아이템을 추가하는 동안 중복 요청을 방지하기 위해 락을 걸어둡니다.
        mLockListView = true;
        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                for(int i = 0 ; i < size ; i++)
                {
                    mRowList.add("Item " + i);
                }
// 모든 데이터를 로드하여 적용하였다면 어댑터에 알리고 리스트뷰의 락을 해제합니다.
                mAdapter.notifyDataSetChanged();
                mLockListView = false;
            }
        };

// 속도의 딜레이를 구현하기 위한 꼼수
        Handler handler = new Handler();
        handler.postDelayed(run, 500);

    }

}

