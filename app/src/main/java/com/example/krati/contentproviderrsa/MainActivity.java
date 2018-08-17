package com.example.krati.contentproviderrsa;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends Activity {

    Button createKeysButton;
    Button encryptButton;
    Button decryptButton;
    EditText messageEditText;

    ContentResolver contentResolver;
    Cursor cursor;
    String publicKey;
    String privateKey;

    Cipher cipher;
    String message;
    String encrypted;
    KeyFactory keyFactory;
    String decrypted;
    byte[] encryptedBytes;

    private static final String AUTHORITY = "com.rsaContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createKeysButton = (Button) findViewById(R.id.createKeysButton);
        encryptButton = (Button) findViewById(R.id.encryptButton);
        decryptButton = (Button) findViewById(R.id.decryptButton);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        contentResolver = getContentResolver();

        //when button CreateKeys clicked generate the keys in the contentProvider and get them through the cursor
        createKeysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor = contentResolver.query(Uri.parse("content://com.rsaContentProvider"), null, null, null, null, null);
                cursor.moveToFirst();
                Toast.makeText(getApplicationContext(), "Created New Keys", Toast.LENGTH_SHORT).show();
            }
        });

        //when encrypt button clicked, get publicKey from cursor and change to PublicKey and encrypt the message
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    message = messageEditText.getText().toString();
                    publicKey = cursor.getString(0);
                    byte[] publicKeyBytes = Base64.decode(publicKey, Base64.DEFAULT);
                    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                    keyFactory = KeyFactory.getInstance("RSA");
                    PublicKey pubKey = keyFactory.generatePublic(publicKeySpec);

                    cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, pubKey);
                    encryptedBytes = cipher.doFinal(message.getBytes());
                    encrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
                    Toast.makeText(MainActivity.this, encrypted, Toast.LENGTH_SHORT).show();
                    messageEditText.setText(encrypted.toString());

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }

            }
        });

        //get privateKey string from cursor and convert to PrivateKey and then decrypt the message
        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    privateKey = cursor.getString(1);
                    byte[] privateKeyBytes = Base64.decode(privateKey, Base64.DEFAULT);
                    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                    keyFactory = KeyFactory.getInstance("RSA");
                    PrivateKey privKey = keyFactory.generatePrivate(privateKeySpec);

                    cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, privKey);
                    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                    decrypted = new String(decryptedBytes);
                    Toast.makeText(MainActivity.this, decrypted, Toast.LENGTH_LONG).show();
                    messageEditText.setText(decrypted);

                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }

            }
        });

    }


}
