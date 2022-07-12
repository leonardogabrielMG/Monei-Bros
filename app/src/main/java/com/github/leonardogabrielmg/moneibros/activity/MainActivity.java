package com.github.leonardogabrielmg.moneibros.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.leonardogabrielmg.moneibros.R;
import com.github.leonardogabrielmg.moneibros.config.ConfiguracaoFireBase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private GoogleSignInClient mGoogleSignInClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Monei Bros");

        //recupera o token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("763000483752-2bb5lefsavq9jrdbclh1tm652b5rs9pv.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        autenticacao = FirebaseAuth.getInstance();

       findViewById(R.id.signInButton).setOnClickListener(view -> {
           signIn();
       });

    }

    //utiliza o metodo de verificação de usuario
   public void onStart(){
        super.onStart();
        verificarUsuarioLogado();
        FirebaseUser currentUser = autenticacao.getCurrentUser();
        //updateUI(currentUser);
    }

    //Metodo de Login com o google
    //abre o google intent
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
        abreActivity.launch(signInIntent);
    }
    ActivityResultLauncher<Intent> abreActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                    try{
                        GoogleSignInAccount conta = task.getResult(ApiException.class);
                        loginComGoogle(conta.getIdToken());
                    } catch (ApiException exception){
                        Toast.makeText(this,
                                "Nenhum usuário Google logado!", Toast.LENGTH_SHORT).show();
                        Log.d("Erro", exception.toString());
                    }
                }
            }
    );


    private void loginComGoogle(String token){
        AuthCredential credencial = GoogleAuthProvider.getCredential(token, null);

        autenticacao.signInWithCredential(credencial).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Toast.makeText(this,
                        "login com Google efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                abrirTelaPrincipal();
                finish();
            }else{
                Toast.makeText(this,
                        "Erro ao efetuar Login com Google!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == 1){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try{
                GoogleSignInAccount conta = task.getResult(ApiException.class);
                loginComGoogle(conta.getIdToken());
            } catch (ApiException exception){
                Toast.makeText(this,
                        "Nenhum usuário Google logado!", Toast.LENGTH_SHORT).show();
                Log.d("Erro", exception.toString());
            }
        }

    }

    //metodos de cadastro e login
    public void btEntra(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void btCadastra(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }

    //verificando se o usuario esta realmente logado
    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFireBase.getFireBaseAutenticacao();
        //autenticacao.signOut();
        //autenticacao.getInstance().signOut();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }

    //abre tela principal do app
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
    }
    
}