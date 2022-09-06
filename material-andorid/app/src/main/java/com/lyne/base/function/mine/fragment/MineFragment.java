package com.lyne.base.function.mine.fragment;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lyne.base.R;
import com.lyne.base.base.BaseFragment;

public class MineFragment extends BaseFragment {
    private EditText mSearchEt;
    private ImageView mSearchIv;
    private SwipeRefreshLayout mSrl;
    private RecyclerView mRv;



    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_order,null);
        mSearchEt = (EditText) view.findViewById(R.id.search_et);
        mSearchIv = (ImageView) view.findViewById(R.id.search_iv);
        mSrl = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        mRv = (RecyclerView) view.findViewById(R.id.rv);

        return view;
    }

    @Override
    public void initData(View view) {
    }
}
