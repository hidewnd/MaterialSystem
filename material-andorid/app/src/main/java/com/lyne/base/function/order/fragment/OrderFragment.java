package com.lyne.base.function.order.fragment;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lyne.base.R;
import com.lyne.base.SpUtils;
import com.lyne.base.api.HttpPort;
import com.lyne.base.base.BaseFragment;
import com.lyne.base.bean.RecordRsp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends BaseFragment {

    private EditText mSearchEt;
    private ImageView mSearchIv;
    private RecyclerView mRv;
    private SwipeRefreshLayout mSrl;


    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_order, null);
        mSearchEt = view.findViewById(R.id.search_et);
        mSearchIv = view.findViewById(R.id.search_iv);
        mRv = view.findViewById(R.id.rv);
        mSrl = view.findViewById(R.id.srl);
        mSrl.setOnRefreshListener(() -> {
            String param = mSearchEt.getText().toString();
            if (param.length() == 0) {
                param = SpUtils.get().getValue("username", "");
            }
            HttpPort.getInstance().getRecordList("outer", 1, 20, "createBy", param)
                    .enqueue(new Callback<RecordRsp>() {
                        @Override
                        public void onResponse(Call<RecordRsp> call, Response<RecordRsp> response) {
                            RecordRsp body = response.body();
                            if (body != null) {
                                if (body.getCode() == 200) {
                                    Log.d("TAG", body.getData().toString());
                                } else {
                                    Toast.makeText(getContext(), body.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RecordRsp> call, Throwable t) {
                            Toast.makeText(getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
                        }
                    });
            mSrl.setRefreshing(false);
        });
        mSearchIv.setOnClickListener(v -> {
            String param = mSearchEt.getText().toString();
            if (param.length() == 0) {
                Toast.makeText(getContext(), "请输入查询条件", Toast.LENGTH_SHORT).show();
            } else {
                getHttpData(param);
            }

        });
        return view;
    }

    private void getHttpData(String arg2) {
        HttpPort.getInstance().getRecordList("outer", 1, 20, "createBy", arg2)
                .enqueue(new Callback<RecordRsp>() {
                    @Override
                    public void onResponse(Call<RecordRsp> call, Response<RecordRsp> response) {
                        RecordRsp body = response.body();
                        if (body != null) {
                            if (body.getCode() == 200) {
                                Log.d("TAG", body.getData().toString());
                            } else {
                                Toast.makeText(getContext(), body.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RecordRsp> call, Throwable t) {
                        Toast.makeText(getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void initData(View view) {

    }
}
