package com.fpoly.thi_test1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fpoly.thi_test1.API.APIContains;
import com.fpoly.thi_test1.Adapter.SinhVienAdapter;
import com.fpoly.thi_test1.DTO.SinhVien;
import com.fpoly.thi_test1.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SinhVienAdapter.EventSinhVien {

    private ActivityMainBinding binding;
    private String TAG = "taoLog";

    private String msv = null;
    private int count = 0;

    private SinhVienAdapter adapter;

    private String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        
        setContentView(binding.getRoot());
        
        addAction();

        LoadRc();
    }

    private void LoadRc() {
        adapter = new SinhVienAdapter(this);
        adapter.SetData(new ArrayList<>());
        binding.rcListSv.setLayoutManager(new LinearLayoutManager(this));
        binding.rcListSv.setAdapter(adapter);
        ReLoadRc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReLoadRc();
    }

    private void ReLoadRc(){
        HandlerLoad(true);
        Call<List<SinhVien>> call = APIContains.SINHVIEN().GetAll();
        call.enqueue(new Callback<List<SinhVien>>() {
            @Override
            public void onResponse(Call<List<SinhVien>> call, Response<List<SinhVien>> response) {
                for(SinhVien s : response.body()){
                    Log.d(TAG, "reload " + s.toString());
                }
                adapter.SetData(response.body());
                HandlerLoad(false);
            }

            @Override
            public void onFailure(Call<List<SinhVien>> call, Throwable t) {
                Log.d(TAG, "REload " + t);
                HandlerLoad(false);
            }
        });
    }

    private void addAction() {
        binding.floatAdd.setOnClickListener(v -> {
            showCustomDialog(false,null);
        });

    }


    @Override
    public void Sua(SinhVien sv) {
        showCustomDialog(false,sv);
    }

    @Override
    public void Xoa(String msv) {

        Runnable xoa = new Runnable() {
            @Override
            public void run() {
                Call<Integer> call = APIContains.SINHVIEN().DeleteElement(msv);
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.body() == 1){

                            Toast.makeText(MainActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                            LoadRc();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });
            }
        };

        GeneralFunc.ShowTwoOptionDsialog(this,"Xoá",xoa,"huỷ",null,"NOTE","Xác nhận xoá !");
    }

    @Override
    public void View(SinhVien sv) {
        showCustomDialog(true,sv);
    }

    public void showCustomDialog(boolean isView, SinhVien svOld) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
        dialogBuilder.setView(dialogView);

        EditText ed_msv = dialogView.findViewById(R.id.dialogCus_ed_msv);
        EditText ed_ten = dialogView.findViewById(R.id.dialogCus_ed_ten);
        EditText ed_diem = dialogView.findViewById(R.id.dialogCus_ed_diem);
        EditText ed_diachi = dialogView.findViewById(R.id.dialogCus_ed_diaChi);
        EditText ed_anh = dialogView.findViewById(R.id.dialogCus_ed_anh);
        EditText ed_email = dialogView.findViewById(R.id.dialogCus_ed_email);

        if(svOld != null) {
            ed_anh.setText(svOld.getAnh());
            ed_msv.setText(svOld.getMasv());
            ed_ten.setText(svOld.getHoten());
            ed_diachi.setText(svOld.getDiaChi());
            ed_diem.setText(String.valueOf(svOld.getDiem()));
            ed_email.setText(svOld.getEmail());
        }

        if(isView){
            ed_msv.setEnabled(false);
            ed_anh.setEnabled(false);
            ed_ten.setEnabled(false);
            ed_diem.setEnabled(false);
            ed_diachi.setEnabled(false);
            ed_anh.setEnabled(false);
            ed_email.setEnabled(false);
        }else
        {
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String msv = ed_msv.getText().toString();
                    String ten = ed_ten.getText().toString();
                    String diem = ed_diem.getText().toString();
                    String diachi = ed_diachi.getText().toString();
                    String anh = ed_anh.getText().toString();
                    String email = ed_email.getText().toString();

                    SinhVien sv = new SinhVien(msv,ten,email,diachi,Float.parseFloat(diem),anh);
                    Call<Integer> call = null;
                    if(svOld != null){
                        call = APIContains.SINHVIEN().UpdateElement(svOld.getMasv(),sv);
                    }else{
                        call = APIContains.SINHVIEN().CreateElement(sv);
                    }

                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.body() == 0){
                                Toast.makeText(MainActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                            }
                            LoadRc();
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t);
                        }
                    });
                }
            });

        }



        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void HandlerLoad(boolean isLoad){
        if(isLoad){
            binding.actiMainLoad.setVisibility(View.VISIBLE);
            binding.actiMainLayout.setVisibility(View.INVISIBLE);
        }else{
            binding.actiMainLoad.setVisibility(View.INVISIBLE);
            binding.actiMainLayout.setVisibility(View.VISIBLE);
        }
    }

}