package com.lyne.base.function.home.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.huawei.hms.framework.common.StringUtils;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.lyne.base.R;
import com.lyne.base.api.HttpPort;
import com.lyne.base.app.MainActivity;
import com.lyne.base.base.BaseFragment;
import com.lyne.base.bean.LoginRsp;
import com.lyne.base.bean.OuterBody;
import com.lyne.base.bean.OuterSaveRsp;
import com.lyne.base.bean.ProofRsp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment {
    private final int REQUEST_CODE_SCAN_ONE = 9872;

    private EditText mRecord;
    private TextView mSend;
    private TextView mScan;
    private String proof = "";
    private String recordId = "";

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_home, null);

        mRecord = view.findViewById(R.id.record);
        mSend = view.findViewById(R.id.send);
        mScan = view.findViewById(R.id.scan);
        mSend.setOnClickListener(v -> {
            String rerecordId = mRecord.getText().toString();
            if (rerecordId.length() == 0) {
                Toast.makeText(getContext(), "请先输入出库编号", Toast.LENGTH_SHORT).show();
            } else {
                HttpPort.getInstance().getProof(rerecordId).enqueue(new Callback<ProofRsp>() {
                    @Override
                    public void onResponse(Call<ProofRsp> call, Response<ProofRsp> response) {
                        ProofRsp body = response.body();
                        if (body != null) {
                            if (body.getCode() == 200 && body.getData() != null) {
                                Toast.makeText(getContext(), body.getMsg(), Toast.LENGTH_SHORT).show();
                                proof = body.getData();
                                recordId = mRecord.getText().toString();
                            } else {
                                Toast.makeText(getContext(), body.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProofRsp> call, Throwable t) {
                        Toast.makeText(getContext(), "获取出库凭证失败", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", t.getMessage());
                    }
                });
            }
        });
        mScan.setOnClickListener(v -> permissionLauncher.launch(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE}));

        return view;
    }

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    (isGranted) -> {
                        if (isGranted.size() == 2) {
                            ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE,
                                    new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE)
                                            .create());
                        }
                    });

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.d("TAG", "onActivityResult: " + data.getParcelableExtra(ScanUtil.RESULT));
            if (requestCode == REQUEST_CODE_SCAN_ONE && resultCode == RESULT_OK) {
                HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
                if (obj != null) {
                    if (proof == null || proof.length() == 0) {
                        Toast.makeText(getContext(), "出库凭证不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (recordId == null || recordId.length() == 0) {
                        Toast.makeText(getContext(), "出库编号不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String originalValue = obj.originalValue;
                    if (originalValue == null || originalValue.length() == 0) {
                        Toast.makeText(getContext(), "物料二维码为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    OuterBody outer = new OuterBody(recordId, proof, obj.originalValue);
                    HttpPort.getInstance().outerSave(outer).enqueue(new Callback<OuterSaveRsp>() {
                        @Override
                        public void onResponse(Call<OuterSaveRsp> call, Response<OuterSaveRsp> response) {
                            OuterSaveRsp body = response.body();
                            if (body != null) {
                                Toast.makeText(getContext(), body.getMsg(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "出库失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<OuterSaveRsp> call, Throwable t) {
                            Toast.makeText(getContext(), "出库失败", Toast.LENGTH_SHORT).show();
                            Log.e("TAG", t.getMessage());
                        }
                    });
                }
            }
        }
    }

    @Override
    public void initData(View view) {
    }


}
