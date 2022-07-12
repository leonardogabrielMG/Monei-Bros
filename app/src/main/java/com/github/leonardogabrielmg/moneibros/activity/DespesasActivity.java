package com.github.leonardogabrielmg.moneibros.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class DespesasActivity extends AppCompatActivity {
    private EditText editDataDespesa, editCategoriaDespesa, editDescricaoDespesa, editValorDespesa;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFireBase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFireBaseAutenticacao();
    private Double despesaTotal;
    private Double despesaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        editDataDespesa = findViewById(R.id.editDataDespesa);
        editCategoriaDespesa = findViewById(R.id.editCategoriaDespesa);
        editDescricaoDespesa = findViewById(R.id.editDescricaoDespesa);
        editValorDespesa = findViewById(R.id.editValorDespesa);

        editDataDespesa.setText(DateUtil.dataAtual());

        recuperarDespesaTotal();
    }

    public void salvarDespesa(View view){

        if(validarCamposDespesa()){

            String dataEscolhida = editDataDespesa.getText().toString();
            Double valorRecuperado = Double.parseDouble(editValorDespesa.getText().toString());

            movimentacao = new Movimentacao();

            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(editCategoriaDespesa.getText().toString());
            movimentacao.setDescricao(editDescricaoDespesa.getText().toString());
            movimentacao.setData(dataEscolhida);
            movimentacao.setTipo("d");

            despesaAtualizada = despesaTotal + valorRecuperado;
            atualizarDespesa(despesaAtualizada);

            movimentacao.salvar(dataEscolhida);

            finish();

        }


    }

    public Boolean validarCamposDespesa(){

        String textoValor = editValorDespesa.getText().toString();
        String textoData = editDataDespesa.getText().toString();
        String textoCategoria = editCategoriaDespesa.getText().toString();
        String textoDescricao = editDescricaoDespesa.getText().toString();

        if(!textoValor.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoCategoria.isEmpty()){
                    if(!textoDescricao.isEmpty()){
                        return true;

                    }else{

                        Toast.makeText(DespesasActivity.this,
                                "Descrição não preenchida!",
                                Toast.LENGTH_SHORT).show();

                        return false;

                    }

                }else{

                    Toast.makeText(DespesasActivity.this,
                            "Categoria não preenchida!",
                            Toast.LENGTH_SHORT).show();

                    return false;

                }

            }else{

                Toast.makeText(DespesasActivity.this,
                        "Data não preenchida!",
                        Toast.LENGTH_SHORT).show();

                return false;

            }

        }else{

            Toast.makeText(DespesasActivity.this,
                    "Valor não preenchido!",
                    Toast.LENGTH_SHORT).show();

            return false;

        }

    }

    public void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError DatabaseError) {

            }
        });
    }

    public void atualizarDespesa(Double despesa){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios")
                .child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesa);

    }
}