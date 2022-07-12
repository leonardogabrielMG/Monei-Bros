package com.github.leonardogabrielmg.moneibros.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.leonardogabrielmg.moneibros.R;
import com.github.leonardogabrielmg.moneibros.config.ConfiguracaoFireBase;
import com.github.leonardogabrielmg.moneibros.helper.Base64Custom;
import com.github.leonardogabrielmg.moneibros.helper.DateUtil;
import com.github.leonardogabrielmg.moneibros.model.Movimentacao;
import com.github.leonardogabrielmg.moneibros.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private EditText editDataReceita, editCategoriaReceita, editDescricaoReceita, editValorReceita;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFireBase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFireBaseAutenticacao();
    private Double receitaTotal;
    private Double receitaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        editDataReceita = findViewById(R.id.editDataReceita);
        editCategoriaReceita = findViewById(R.id.editCategoriaReceita);
        editDescricaoReceita = findViewById(R.id.editDescricaoReceita);
        editValorReceita = findViewById(R.id.editValorReceita);

        editDataReceita.setText(DateUtil.dataAtual());

        recuperarReceitaTotal();
    }

    public void salvarReceita(View view){

        if(validarCamposReceita()){

            String dataEscolhida = editDataReceita.getText().toString();
            Double valorRecuperado = Double.parseDouble(editValorReceita.getText().toString());

            movimentacao = new Movimentacao();

            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(editCategoriaReceita.getText().toString());
            movimentacao.setDescricao(editDescricaoReceita.getText().toString());
            movimentacao.setData(dataEscolhida);
            movimentacao.setTipo("r");

            receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);

            movimentacao.salvar(dataEscolhida);

            finish();

        }


    }

    public Boolean validarCamposReceita(){

        String textoValor = editValorReceita.getText().toString();
        String textoData = editDataReceita.getText().toString();
        String textoCategoria = editCategoriaReceita.getText().toString();
        String textoDescricao = editDescricaoReceita.getText().toString();

        if(!textoValor.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoCategoria.isEmpty()){
                    if(!textoDescricao.isEmpty()){
                        return true;

                    }else{

                        Toast.makeText(ReceitasActivity.this,
                                "Descrição não preenchida!",
                                Toast.LENGTH_SHORT).show();

                        return false;

                    }

                }else{

                    Toast.makeText(ReceitasActivity.this,
                            "Categoria não preenchida!",
                            Toast.LENGTH_SHORT).show();

                    return false;

                }

            }else{

                Toast.makeText(ReceitasActivity.this,
                        "Data não preenchida!",
                        Toast.LENGTH_SHORT).show();

                return false;

            }

        }else{

            Toast.makeText(ReceitasActivity.this,
                    "Valor não preenchido!",
                    Toast.LENGTH_SHORT).show();

            return false;

        }

    }

    public void recuperarReceitaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios")
                .child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError DatabaseError) {

            }
        });
    }

    public void atualizarReceita(Double receita){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios")
                .child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);

    }

}