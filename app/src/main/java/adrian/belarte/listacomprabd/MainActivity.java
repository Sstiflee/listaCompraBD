package adrian.belarte.listacomprabd;

import android.content.DialogInterface;
import android.os.Bundle;

import com.j256.ormlite.dao.Dao;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import adrian.belarte.listacomprabd.configuaraciones.Configuracion;
import adrian.belarte.listacomprabd.databinding.ActivityMainBinding;
import adrian.belarte.listacomprabd.helpers.ProductHelper;
import adrian.belarte.listacomprabd.modelos.ProductAdapter;
import adrian.belarte.listacomprabd.modelos.Producto;

import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ArrayList<Producto> listaProductos;
    private ProductAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProductHelper helper;
    private Dao<Producto, Integer> daoProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listaProductos = new ArrayList<>();

        helper = new ProductHelper(this,Configuracion.BD_NAME,null,Configuracion.BD_VERSION);

        adapter = new ProductAdapter(MainActivity.this, listaProductos, R.layout.product_view_holder);
        layoutManager = new LinearLayoutManager(this);

        if(helper != null){
            try {
                daoProductos = helper.getDaoProductos();
                listaProductos.addAll(daoProductos.queryForAll());
                adapter.notifyItemRangeInserted(0,listaProductos.size());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        binding.contentMain.contenedor.setAdapter(adapter);
        binding.contentMain.contenedor.setLayoutManager(layoutManager);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearProducto().show(); //metodo para mostrar la ventana
            }
        });
    }

    private AlertDialog crearProducto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("CREAR PRODUCTO");
        builder.setCancelable(false);

        View productView = LayoutInflater.from(this).inflate(R.layout.product_view_model, null);
        EditText txtNombre = productView.findViewById(R.id.txtNombreViewModel);
        EditText txtCantidad = productView.findViewById(R.id.txtcantidadViewModel);
        EditText txtPrecio = productView.findViewById(R.id.txtprecioViewModel);
        builder.setView(productView);

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(txtNombre.getText().toString().isEmpty() ||
                        txtCantidad.getText().toString().isEmpty() ||
                txtPrecio.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Faltan datos", Toast.LENGTH_SHORT).show();
                }else{
                    Producto producto = new Producto(
                            txtNombre.getText().toString(),
                            Integer.parseInt(txtCantidad.getText().toString()),
                            Float.parseFloat(txtPrecio.getText().toString())
                    );
                    listaProductos.add(producto);
                    adapter.notifyItemInserted(listaProductos.size()-1);//importante(sino no se muestra)

                    //guardar en la base de datos
                    try {
                        daoProductos.create(producto);
                        int id = daoProductos.extractId(producto);
                        producto.setId(id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return builder.create();
    }
}