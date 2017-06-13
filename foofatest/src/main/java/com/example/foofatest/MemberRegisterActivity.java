package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.foofatest.Gson.GSonParsingControl;
import com.example.foofatest.dto.Member;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MemberRegisterActivity extends AppCompatActivity {

    private Member member;
    private EditText idEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idEdit = (EditText) findViewById(R.id.reg_id);
        final EditText password1Edit = (EditText) findViewById(R.id.reg_password1);
        final EditText password2Edit = (EditText) findViewById(R.id.reg_password2);
        final EditText birthdayEdit = (EditText) findViewById(R.id.reg_birthday);
        final EditText emailEdit = (EditText) findViewById(R.id.reg_email);
        final RadioButton maleRadio = (RadioButton) findViewById(R.id.reg_genderM);

        Button submitBtn = (Button)findViewById(R.id.reg_memberBtn);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(password1Edit.getText().toString().equals(password2Edit.getText().toString())){
                    Toast.makeText(MemberRegisterActivity.this, "회원가입 시도.", Toast.LENGTH_SHORT).show();
                    member = new Member();
                    member.setMemberId(idEdit.getText().toString());
                    member.setPassword(password2Edit.getText().toString());
                    member.setBirthday(birthdayEdit.getText().toString());
                    member.setEmail(emailEdit.getText().toString());
                    if(maleRadio.isChecked()){
                        member.setGender("M");
                    }else {
                        member.setGender("F");
                    }
                    new MemberRegisterActivity.RegisterTask().execute("http://106.242.203.67:8888/FoodtruckFinderProject/mobile/memberRegister.do", member);
                }else {
                    Toast.makeText(MemberRegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private class RegisterTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
                Member member = (Member) params[1];
                return GSonParsingControl.POST((String) params[0], member);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("life","result"+result);

            Intent intent = null;
            switch (result){
                case "true":
                    intent = new Intent(MemberRegisterActivity.this, LoginActivity.class);
                    break;
                case "false":
                    Toast.makeText(MemberRegisterActivity.this, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MemberRegisterActivity.this, MemberRegisterActivity.class);
                    break;
                case "duplicatedId":
                    Toast.makeText(MemberRegisterActivity.this, "중복되는 아이디입니다. 다른 아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                    idEdit.requestFocus();
                    idEdit.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    break;
            }
                if(intent!=null){
                intent = new Intent(MemberRegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }


}
