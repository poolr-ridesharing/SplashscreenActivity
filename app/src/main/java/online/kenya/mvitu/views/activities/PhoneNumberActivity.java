package online.kenya.mvitu.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import online.kenya.mvitu.R;
import online.kenya.mvitu.Utils.Sanitizer;
import online.kenya.mvitu.controller.FirebaseAuth;
import online.kenya.mvitu.models.Customer;
import online.kenya.mvitu.views.layouts.LoadingDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneNumberActivity extends AppCompatActivity{
    EditText phone;
    Button submit;
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        initViews();
        setListeners();
    }

    private void setListeners() {
        submit.setOnClickListener(view -> login());
    }

    private void login() {
        dialog.startLoadingAlertDialog();
        String phoneNumber=phone.getText().toString().trim();
        if (phoneIsValid(phoneNumber)){
            Customer customer=new Customer();

            customer.setPhoneNumber(Sanitizer.toE164(phoneNumber));

            FirebaseAuth.logInWithPhoneNumber(
                    customer,
                    PhoneNumberActivity.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            Intent intent=new Intent(getApplicationContext(),verifyOTP.class);
                            intent.putExtra("backEndOTP",s);
                            intent.putExtra("phoneNumber",customer);
                            startActivity(intent);
                        }

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            //main activity
                            Toast.makeText(PhoneNumberActivity.this, "Successfully detected code", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(PhoneNumberActivity.this, "error in verification: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
            });
        }
        else Toast.makeText(this, "write a valid number", Toast.LENGTH_SHORT).show();

    }

    private boolean phoneIsValid(String phoneNumber) {
    if (phoneNumber.isEmpty())
        return false;
    if (phoneNumber.length()>13)
        return false;
    return true;
    }


    private void initViews() {
        phone=findViewById(R.id.editTextPhone);
        submit=findViewById(R.id.btnSignUp);
        dialog=new LoadingDialog(this);
    }
}