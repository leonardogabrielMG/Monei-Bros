package com.github.leonardogabrielmg.moneibros.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFireBase {

    private static FirebaseAuth autenticacao;
    private static DatabaseReference firebase;

    //retorna a instancia do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase(){
        if( firebase == null ){
            firebase = FirebaseDatabase.getInstance().getReference();
        }
        return  firebase;
    }

    //retorna a instancia do FireBaseAuth
    public static FirebaseAuth getFireBaseAutenticacao(){
        if( autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return  autenticacao;
    }

}
