package com.example.krati.contentproviderrsa;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.rsaContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static final String PUBLIC_KEY = "public_key";
    static final String PRIVATE_KEY = "private_key";

    MatrixCursor matrixCursor;
    String publicKey;
    String privateKey;


    public RSAContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        throw new UnsupportedOperationException("Not yet implemented");

    }

    //Generate RSA keys using KeyPaitGenerator
    public void keyGeneration(){
        try{

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            publicKey = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT);
            privateKey = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT);

        }catch (Exception e){

        }
    }

    @Override
    public boolean onCreate() {
       return true;
    }

    //Create the MatrixCursor and put the keys into it and notify contentResolver
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        keyGeneration();

        String[] columnNames = {"publicKey", "privateKey"};
        matrixCursor = new MatrixCursor(columnNames);
        matrixCursor.addRow(new Object[]{publicKey, privateKey});
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return matrixCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
