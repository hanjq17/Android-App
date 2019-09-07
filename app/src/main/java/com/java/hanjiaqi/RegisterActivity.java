package com.java.hanjiaqi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button button=findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=((EditText)findViewById(R.id.username1)).getText().toString();
                String password=((EditText)findViewById(R.id.password1)).getText().toString();
                String password1=((EditText)findViewById(R.id.password2)).getText().toString();

                if(username.length()==0||username.length()>20){
                    Toast.makeText(RegisterActivity.this, "用户名不能为空或长度超过20", Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.length()<6){
                    Toast.makeText(RegisterActivity.this, "密码不要短于6位哦", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!password.equals(password1)){
                    Toast.makeText(RegisterActivity.this, "两次输入密码不一致哦", Toast.LENGTH_LONG).show();
                    return;
                }



                NewsDatabaseManager manager=NewsDatabaseManager.getInstance(RegisterActivity.this);
                if(manager.addUser(username,password)){
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                }
                else{
                    Toast.makeText(RegisterActivity.this, "用户名重复，换一个用户名吧", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
